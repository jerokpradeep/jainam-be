package in.codifi.funds.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.razorpay.Order;

import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.config.HazelcastConfig;
import in.codifi.funds.entity.primary.BOPaymentLogEntity;
import in.codifi.funds.entity.primary.ClientBankDetailsEntity;
import in.codifi.funds.entity.primary.PaymentRefEntity;
import in.codifi.funds.entity.primary.PayoutDetailsEntity;
import in.codifi.funds.entity.primary.UpiDetailsEntity;
import in.codifi.funds.helper.PaymentHelper;
import in.codifi.funds.model.request.FormDataModel;
import in.codifi.funds.model.request.PaymentReqModel;
import in.codifi.funds.model.request.UPIReqModel;
import in.codifi.funds.model.request.VerifyPaymentReqModel;
import in.codifi.funds.model.response.BankDetails;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.model.response.GetPaymentResposeModel;
import in.codifi.funds.model.response.PayInResponse;
import in.codifi.funds.model.response.PaymentResponseModel;
import in.codifi.funds.model.response.PayoutDetailsResp;
import in.codifi.funds.model.response.PayoutResponse;
import in.codifi.funds.model.response.RazorpayModel;
import in.codifi.funds.repository.BankDetailsRepository;
import in.codifi.funds.repository.ClientBankDetailRepository;
import in.codifi.funds.repository.DropdownRepository;
import in.codifi.funds.repository.PaymentRefRepository;
import in.codifi.funds.repository.PayoutDetailsRepository;
import in.codifi.funds.repository.UpiRepository;
import in.codifi.funds.service.spec.PaymentServiceSpec;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.CodifiUtil;
import in.codifi.funds.utility.EmailUtils;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.BankDetailsRestResp;
import in.codifi.funds.ws.service.BackOfficeRestService;
import in.codifi.funds.ws.service.FundsRestService;
import in.codifi.funds.ws.service.RazorpayRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PaymentService implements PaymentServiceSpec {
	@Inject
	PrepareResponse prepareResponse;
//	@Inject
//	BankDetailsEntityManager bankDetailsEntityManager;
	@Inject
	EmailUtils emailUtils;
	@Inject
	PaymentHelper paymentHelper;
	@Inject
	PaymentRefRepository paymentRefRepo;
	@Inject
	BackOfficeRestService backOfficeRestService;
	@Inject
	UpiRepository accRepo;
	@Inject
	RazorpayRestService razorpayRestService;
	@Inject
	CodifiUtil commonMethods;
	@Inject
	DropdownRepository dropdownRepository;
	@Inject
	FundsRestService fundsRestService;
	@Inject
	BankDetailsRepository bankDetailsRepository;
//	@Inject
//	BankDetailsEntityManager bankDetailsEntityManager;
	@Inject
	ClientBankDetailRepository clientBankDetailRepository;
	@Inject
	PayoutDetailsRepository payoutDetailsRepository;

	/**
	 * method to create new payment details
	 * 
	 * @author Gowthaman M
	 * @param paymentReqModel
	 * @param clientInfo
	 */
	@SuppressWarnings("static-access")
	@Override
	public RestResponse<GenericResponse> createNewPayment(PaymentReqModel paymentReqModel, ClinetInfoModel info) {
		try {
			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "userSession";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			if (!validateCreatePaymentReq(paymentReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			if (paymentReqModel.getAmount() < 100)
				return prepareResponse.prepareFailedResponse(AppConstants.LESS_THEN_MINIMUM);

			ClientBankDetailsEntity bankDetails = new ClientBankDetailsEntity();
			bankDetails = clientBankDetailRepository.findByBankAcnoAndClientId(paymentReqModel.getBankActNo(),
					info.getUserId());

			if (bankDetails != null) {
				paymentReqModel.setBankActNo(bankDetails.getBankAcno());
				paymentReqModel.setBankName(bankDetails.getClientBankName());
				paymentReqModel.setClientName(bankDetails.getClientName());
				paymentReqModel.setIfscCode(bankDetails.getIfscCode());
				String receipt = commonMethods.randomAlphaNumeric(15);
				paymentReqModel.setReceipt(receipt);
				if (StringUtil.isNullOrEmpty(receipt))
					return prepareResponse.prepareFailedResponse(AppConstants.PAYMENT_FAILED_ID_NULL);
				if (paymentReqModel.getAmount() <= 0)
					return prepareResponse.prepareFailedResponse(AppConstants.AMOUNT_ZERO);
				RazorpayModel rzpayModel = paymentHelper.createPayment(paymentReqModel, info);
				if (rzpayModel.getStat() == 0)
					return prepareResponse.prepareFailedResponse(AppConstants.PAYMENT_CREATION_FAILED);
				Order order = rzpayModel.getOrder();
				if (order == null)
					return prepareResponse.prepareFailedResponse(AppConstants.PAYMENT_CREATION_FAILED);
				String orderId = order.get("id");
				paymentReqModel.setOrderId(orderId);
				paymentReqModel.setUserId(info.getUserId());

				PaymentRefEntity paymentRefEntity = preparePaymentDetails(paymentReqModel, order.toString());
				PaymentRefEntity savedData = paymentRefRepo.save(paymentRefEntity);
				if (savedData != null) {
					PaymentResponseModel response = new PaymentResponseModel();
					response.setOrderId(orderId);
					response.setReceiptId(receipt);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate create new payment details
	 * 
	 * @author Gowthaman M
	 * @param paymentReqModel
	 * @param clientInfo
	 */
	private boolean validateCreatePaymentReq(PaymentReqModel paymentReqModel) {
		if (StringUtil.isNotNullOrEmpty(paymentReqModel.getBankActNo()) && paymentReqModel.getAmount() > 0
				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getPayMethod())
				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getIfscCode())) {
			return true;

		}

		return false;
	}

	/**
	 * method to get bank details from back office
	 * 
	 * @author SOWMIYA
	 * @param userId,ifsc,accno
	 *
	 */
	@SuppressWarnings("unused")
	private BankDetails getBankDetailsFromBo(String userId, String ifsc, String accNo) {
		BankDetails bankDetails = new BankDetails();
		try {
			Object boBankDetails = backOfficeRestService.loginBackOffice(userId);
			if (boBankDetails != null) {
				JSONArray arrResponse = (JSONArray) boBankDetails;
				if (arrResponse != null && arrResponse.size() > 0) {
					JSONObject results = (JSONObject) arrResponse.get(0);
					JSONArray data = (JSONArray) results.get("DATA");
					for (int i = 0; i < data.size(); i++) {
						JSONArray dataObj = new JSONArray();
						dataObj = (JSONArray) data.get(i);
						if (dataObj != null && dataObj.size() > 0) {
							String ifscCode = (String) dataObj.get(3);
							String bankName = (String) dataObj.get(11);
							String bankAccNo = (String) dataObj.get(8);
							String clientName = (String) dataObj.get(7);
							if (ifscCode.equalsIgnoreCase(ifsc) && bankAccNo.endsWith(accNo)) {
								bankDetails.setIfscCode(ifscCode);
								bankDetails.setBankName(bankName);
								bankDetails.setBankActNo(bankAccNo);
								bankDetails.setClientName(clientName);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return bankDetails;
	}

	/**
	 * method to prepare payment details
	 * 
	 * @author SOWMIYA
	 * @param paymentReqModel
	 * @param req
	 */
	private PaymentRefEntity preparePaymentDetails(PaymentReqModel paymentReqModel, String req) {
		PaymentRefEntity refEntity = new PaymentRefEntity();
		try {
			refEntity.setAccNum(paymentReqModel.getBankActNo());
			refEntity.setBankName(paymentReqModel.getBankName());
			refEntity.setExchSeg(paymentReqModel.getSegment());
			refEntity.setUserId(paymentReqModel.getUserId());
			refEntity.setOrderId(paymentReqModel.getOrderId());
			refEntity.setPaymentMethod(paymentReqModel.getPayMethod());
			refEntity.setReceiptId(paymentReqModel.getReceipt());
			refEntity.setRequest(req);
			refEntity.setAmount(paymentReqModel.getAmount());
			refEntity.setUpiId(paymentReqModel.getUpiId());
			refEntity.setPaymentStatus("created");
			refEntity.setCreatedBy(paymentReqModel.getUserId());
		} catch (Exception e) {
			Log.error(e);
		}
		return refEntity;

	}

	/**
	 * method to validate parameters
	 * 
	 * @author SOWMIYA
	 * @param paymentReqModel
	 *
	 */
//	private boolean validateNewPaymentReq(PaymentReqModel paymentReqModel) {
//		if (StringUtil.isNotNullOrEmpty(paymentReqModel.getBankActNo()) && paymentReqModel.getAmount() > 0
//				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getSegment())
//				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getPayMethod())
//				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getDevice())
//				&& StringUtil.isNotNullOrEmpty(paymentReqModel.getIfscCode())) {
//			return true;
//
//		}
//
//		return false;
//	}

	/*
	 * method to get upi id
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */

	@Override
	public RestResponse<GenericResponse> getUPIId(ClinetInfoModel info) {
		try {
			UpiDetailsEntity accRes = accRepo.findByUserId(info.getUserId());
			if (accRes != null) {
				return prepareResponse.prepareSuccessResponseObject(accRes);
			} else {
				return prepareResponse.prepareSuccessMessage(AppConstants.NO_DATA);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/*
	 * method to set upi id
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> setUPIId(ClinetInfoModel info, UPIReqModel model) {
		UpiDetailsEntity upiDetailsEntity = new UpiDetailsEntity();
		try {
			if (StringUtil.isNullOrEmpty(model.getUpiId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			upiDetailsEntity = accRepo.findByUserId(info.getUserId());
			if (upiDetailsEntity != null) {
				upiDetailsEntity.setUpiId(model.getUpiId());
				upiDetailsEntity.setUpdatedBy(info.getUserId());
			} else {
				upiDetailsEntity = new UpiDetailsEntity();
				upiDetailsEntity.setUserId(info.getUserId());
				upiDetailsEntity.setUpiId(model.getUpiId());
				upiDetailsEntity.setCreatedBy(info.getUserId());
			}
			if (upiDetailsEntity != null) {
				UpiDetailsEntity update = accRepo.save(upiDetailsEntity);
				if (update != null)
					if (HazelcastConfig.getInstance().getPaymentDetails().containsKey(info.getUserId())) {
						GetPaymentResposeModel paymentResponseModel = HazelcastConfig.getInstance().getPaymentDetails()
								.get(info.getUserId());
						paymentResponseModel.setUpiId(update.getUpiId());
						HazelcastConfig.getInstance().getPaymentDetails().remove(info.getUserId());
						HazelcastConfig.getInstance().getPaymentDetails().put(info.getUserId(), paymentResponseModel);
						return prepareResponse.prepareSuccessResponseObject(paymentResponseModel);
					}
				return prepareResponse.prepareSuccessResponseObject(update);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to get payment details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPaymentDetails(ClinetInfoModel info) {
		GetPaymentResposeModel paymentResponseModel = new GetPaymentResposeModel();
		try {
			// TODO if client details not exist need to load **//
			if (HazelcastConfig.getInstance().getClientDetails().containsKey((info.getUserId()))) {
				if (HazelcastConfig.getInstance().getPaymentDetails().containsKey(info.getUserId())) {
					paymentResponseModel = HazelcastConfig.getInstance().getPaymentDetails().get(info.getUserId());
					System.out.println("paymentResponseModel.mobile--" + paymentResponseModel.getPhone());
					return prepareResponse.prepareSuccessResponseObject(paymentResponseModel);
				} else {
					ClientDetailsModel clientModel = HazelcastConfig.getInstance().getClientDetails()
							.get(info.getUserId());

					System.out.println("mobile--" + clientModel.getMobNo());

					paymentResponseModel.setPhone(clientModel.getMobNo());
					paymentResponseModel.setEmail(clientModel.getEmail());
					paymentResponseModel.setSegEnable(clientModel.getExchange());
					paymentResponseModel.setUserName(clientModel.getClientName());
					UpiDetailsEntity accRes = accRepo.findByUserId(info.getUserId());
					if (accRes != null) {
						paymentResponseModel.setUpiId(accRes.getUpiId());
					} else {
						paymentResponseModel.setUpiId("NA");
					}
					List<BankDetails> bankDetails = new ArrayList<>();
					List<ClientBankDetailsEntity> clientBankDetails = new ArrayList<>();
					clientBankDetails = clientBankDetailRepository.findByClientId(info.getUserId());

//					bankDetails = bankDetailsEntityManager.getBankDetails(info.getUserId());
//					if (bankDetails == null || StringUtil.isListNullOrEmpty(bankDetails) || bankDetails.size() <= 0) {
//						Log.info("bankDetails size from DB " + bankDetails.size());
//						/** Get bank details from back office **/
//						bankDetails = getBankDetailsFromBo(info.getUserId());
//					}
					for (int i = 0; i < clientBankDetails.size(); i++) {
						BankDetails detail = new BankDetails();
						detail.setBankActNo(clientBankDetails.get(i).getBankAcno());
						BankDetails bankDetailsFromRazorpay = fundsRestService
								.getBankDetailsFromRazorpay(clientBankDetails.get(i).getIfscCode());
						detail.setBankCode(bankDetailsFromRazorpay.getBankCode());
						detail.setBankName(clientBankDetails.get(i).getClientBankName());
						detail.setClientName(clientBankDetails.get(i).getClientName());
						detail.setIfscCode(clientBankDetails.get(i).getIfscCode());
						bankDetails.add(detail);

					}
					paymentResponseModel.setBankDetails(bankDetails);

//					List<String> payoutReasons = dropdownRepository.getPayoutReasons();
//					if (StringUtil.isListNotNullOrEmpty(payoutReasons)) {
//						paymentResponseModel.setPayoutReasons(payoutReasons);
//					}

					HazelcastConfig.getInstance().getPaymentDetails().put(info.getUserId(), paymentResponseModel);
					return prepareResponse.prepareSuccessResponseObject(paymentResponseModel);
				}
			} else {
				Log.error("Client Info is Empty");
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return prepareResponse.prepareSuccessResponseObject(paymentResponseModel);
	}

	/*
	 * method to get user bank details from back office
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<BankDetails> getBankDetailsFromBo(String userId) {
		List<BankDetails> userBankDetails = new ArrayList<>();
		try {
			Object boBankDetails = backOfficeRestService.loginBackOffice(userId);
			if (boBankDetails != null) {
				JSONArray arrResponse = (JSONArray) boBankDetails;
				if (arrResponse != null && arrResponse.size() > 0) {
					JSONObject results = (JSONObject) arrResponse.get(0);
					JSONArray data = (JSONArray) results.get("DATA");
					for (int i = 0; i < data.size(); i++) {
						BankDetails bank = new BankDetails();
						JSONArray dataObj = new JSONArray();
						dataObj = (JSONArray) data.get(i);
						if (dataObj != null && dataObj.size() > 0) {
							String ifscCode = (String) dataObj.get(3);
							String bankName = (String) dataObj.get(11);
							String bankAccNo = (String) dataObj.get(8);
							bankAccNo = bankAccNo.replaceAll(".(?=.{4})", "*");
							String bankCode = "NA";
							if (HazelcastConfig.getInstance().getIfscCodeMapping().containsKey(ifscCode)) {
								bankCode = HazelcastConfig.getInstance().getIfscCodeMapping().get(ifscCode);
							} else {
								/** Get bank details by IFSC CODE **/
								BankDetailsRestResp bankdetails = razorpayRestService.getBankDetails(ifscCode);
								if (bankdetails != null && StringUtil.isNotNullOrEmpty(bankdetails.getBankcode())) {
									bankCode = bankdetails.getBankcode();
								}
							}
							bank.setBankCode(bankCode);
							bank.setBankActNo(bankAccNo);
							bank.setIfscCode(ifscCode);
							bank.setBankName(bankName);
							userBankDetails.add(bank);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return userBankDetails;
	}

	/**
	 * method to verify payments
	 * 
	 * @author SOWMIYA
	 * @param info
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> verifyPayments(ClinetInfoModel info, VerifyPaymentReqModel model) {
		try {
			if (!validatePaymentParameter(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			boolean isEqual = paymentHelper.verifyPayment(model, info.getUserId());
			if (isEqual) {
//				updateBoPayment(model.getRazorpayOrderId(), model.getRazorpayPaymentId());
				paymentRefRepo.updatePaymentDetails(info.getUserId(), model.getRazorpayOrderId(), "Sucessfull");
				return prepareResponse.prepareSuccessResponseObject(AppConstants.VERIFY_SUCCEED);
			} else {
				paymentRefRepo.updatePaymentDetails(info.getUserId(), model.getRazorpayOrderId(), "Failed");
				return prepareResponse.prepareFailedResponse(AppConstants.VERIFY_NOT_SUCCEED);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to validate payment parameters
	 * 
	 * @author SOWMIYA
	 * @param info
	 * @param model
	 * @return
	 */
	private boolean validatePaymentParameter(VerifyPaymentReqModel model) {
		if (model.getAmount() > 0 && StringUtil.isNotNullOrEmpty(model.getCurrency())
				&& StringUtil.isNotNullOrEmpty(model.getReceipt())
				&& StringUtil.isNotNullOrEmpty(model.getRazorpayOrderId())
				&& StringUtil.isNotNullOrEmpty(model.getRazorpayPaymentId())
				&& StringUtil.isNotNullOrEmpty(model.getRazorpaySignature())) {
			return true;
		}
		return false;
	}

	/**
	 * method to update back office payment
	 * 
	 * @author SOWMIYA
	 * @param orderId
	 * @param paymentId
	 */
	@SuppressWarnings("unused")
	private void updateBoPayment(String orderId, String paymentId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		PaymentRefEntity paymentDetailsDB = paymentRefRepo.findByOrderId(orderId);
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// Get order details from the Database for given order id

//					PaymentRefEntity paymentDetailsDB = paymentRefRepo.findByOrderId(orderId);
//					Log.info("First Resp " + paymentDetailsDB.getOrderId());

					Log.info("second Resp " + paymentDetailsDB.getOrderId());
					if (paymentDetailsDB != null) {
						// Check Bo is already pushed or not
						String voucherId = paymentDetailsDB.getVoucherNo();
						if (paymentDetailsDB.getBoUpdate() == 0 && StringUtil.isNullOrEmpty(voucherId)) {
							// Call to BO For Updating the Payin
							JSONArray boPayInResponse = (JSONArray) backOfficeRestService.loginBackOfficePayIn(
									paymentDetailsDB.getUserId(), paymentDetailsDB.getExchSeg(), orderId,
									paymentDetailsDB.getAmount(), paymentDetailsDB.getAccNum(), paymentId,
									paymentDetailsDB.getPaymentMethod());
							if (boPayInResponse != null && boPayInResponse.size() > 0) {
								JSONObject boPayResponseJson = (JSONObject) boPayInResponse.get(0);
								if (boPayResponseJson != null) {
									JSONArray boPayInData = (JSONArray) boPayResponseJson.get("DATA");
									if (boPayInData != null && boPayInData.size() > 0) {
										JSONArray boPayInData2 = (JSONArray) boPayInData.get(0);
										if (boPayInData2 != null && boPayInData2.size() > 0) {
											String originalData = (String) boPayInData2.get(0);
											if (StringUtil.isNotNullOrEmpty(originalData)) {
												String paymentStatus = "Sucessfull";
												String[] data = originalData.split(",");
												String status = data[0];
												String voucherNo = data[1];
												if (status.equalsIgnoreCase("Sucess:1")
														&& StringUtil.isNotNullOrEmpty(voucherNo)) {
													paymentRefRepo.updateboStatus(orderId, voucherNo, paymentStatus,
															paymentDetailsDB.getUserId());
												}
											} else {
												// Send payment Failure Mail to Admin's
												String message = " Payment of user " + paymentDetailsDB.getUserId()
														+ " with bank account no : " + paymentDetailsDB.getAccNum()
														+ " with amount " + paymentDetailsDB.getAmount()
														+ " is Failed with razorpay Id : " + paymentId;
												emailUtils.paymentFailureEmail(message);
											}
										} else {
											// Send payment Failure Mail to Admin's
											String message = " Payment of user " + paymentDetailsDB.getUserId()
													+ " with bank account no : " + paymentDetailsDB.getAccNum()
													+ " with amount " + paymentDetailsDB.getAmount()
													+ " is Failed with razorpay Id : " + paymentId;
											emailUtils.paymentFailureEmail(message);
										}
									} else {
										// Send payment Failure Mail to Admin's
										String message = " Payment of user " + paymentDetailsDB.getUserId()
												+ " with bank account no : " + paymentDetailsDB.getAccNum()
												+ " with amount " + paymentDetailsDB.getAmount()
												+ " is Failed with razorpay Id : " + paymentId;
										emailUtils.paymentFailureEmail(message);
									}
								} else {
									// Send payment Failure Mail to Admin's
									String message = " Payment of user " + paymentDetailsDB.getUserId()
											+ " with bank account no : " + paymentDetailsDB.getAccNum()
											+ " with amount " + paymentDetailsDB.getAmount()
											+ " is Failed with razorpay Id : " + paymentId;
									emailUtils.paymentFailureEmail(message);
								}
							} else {
								// Send payment Failure Mail to Admin's
								String message = " Payment of user " + paymentDetailsDB.getUserId()
										+ " with bank account no : " + paymentDetailsDB.getAccNum() + " with amount "
										+ paymentDetailsDB.getAmount() + " is Failed with razorpay Id : " + paymentId;
								emailUtils.paymentFailureEmail(message);
							}
						}
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();

	}

	/*
	 * method to get the payout check balance
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> payOutCheckBalance(ClinetInfoModel info) {
		try {
			JSONArray checkBalanceResponse = (JSONArray) backOfficeRestService
					.loginBackOfficeCheckBalance(info.getUserId());
			if (checkBalanceResponse != null) {
				return prepareResponse.prepareSuccessResponseObject(checkBalanceResponse);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.CANNOT_GET_BANK_DETAILS);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/*
	 * method to get pay out details
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPayOutDetails(ClinetInfoModel info) {
		try {

//			List<BOPaymentLogEntity> paymentLogEntity = boPaymentRepo.findByUserIdWithLimit(info.getUserId(), 5);
//			if (paymentLogEntity != null && paymentLogEntity.size() > 0) {
//				paymentLogEntity = preparePaymentResponse(paymentLogEntity);
//				return prepareResponse.prepareSuccessResponseObject(paymentLogEntity);
//			} else {
//				return prepareResponse.prepareSuccessMessage(AppConstants.NO_DATA);
//			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/*
	 * method to prepare BO Payment details
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<BOPaymentLogEntity> preparePaymentResponse(List<BOPaymentLogEntity> paymentLogEntity) {
		List<BOPaymentLogEntity> boPayment = new ArrayList<>();
		try {
			for (BOPaymentLogEntity logEntity : paymentLogEntity) {
				String bankActNo = logEntity.getBankActNo();
				if (StringUtil.isNotNullOrEmpty(logEntity.getBankActNo())) {
					bankActNo = bankActNo.replaceAll(".(?=.{4})", "*");
				}
				logEntity.setBankActNo(bankActNo);
				boPayment.add(logEntity);

			}

		} catch (Exception e) {
			Log.error(e);
		}
		return boPayment;
	}

	/**
	 * Method to pay out
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> payOut(ClinetInfoModel info, PaymentReqModel model) {
		try {
			/** Validate Request **/
			if (!validatePayoutParams(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** create pay out details **/
//			createPayOut(info.getUserId(), model);
			PayoutDetailsResp payoutDetailsResp = fundsRestService.getPayoutDetailsResp(info.getUserId());
			BankDetails bankDetailsFromRazorpay = fundsRestService.getBankDetailsFromRazorpay(model.getIfscCode());

			PayoutDetailsEntity payoutDetails = new PayoutDetailsEntity();
			if (info.getUserId().equalsIgnoreCase("109508") || info.getUserId().equalsIgnoreCase("117995")
					|| info.getUserId().equalsIgnoreCase("111560") || info.getUserId().equalsIgnoreCase("ES0837")
					|| info.getUserId().equalsIgnoreCase("S00600")) {
				String bankcode = "";
				if (model.getIfscCode().equalsIgnoreCase("UTIB0000014")) {
					bankcode = "AXIS";
				} else if (model.getIfscCode().equalsIgnoreCase("ICIC0006037")) {
					bankcode = "ICI";
				} else if (model.getIfscCode().equalsIgnoreCase("HDFC0000751")) {
					bankcode = "HDFC";
				} else if (model.getIfscCode().equalsIgnoreCase("HDFC0000492")) {
					bankcode = "HDFC";
				} else if (model.getIfscCode().equalsIgnoreCase("600229006")) {
					bankcode = "ICI";
				}
				payoutDetails = preparePayoutDetails(model, payoutDetailsResp, bankcode, info);
			} else {
				payoutDetails = preparePayoutDetails(model, payoutDetailsResp, bankDetailsFromRazorpay.getBankCode(),
						info);
			}

			PayoutDetailsEntity payoutDetailsEntity = payoutDetailsRepository.save(payoutDetails);
			if (payoutDetailsEntity == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			return prepareResponse.prepareSuccessResponseObject(payoutDetailsEntity);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to prepare payout
	 * 
	 * @author Gowthaman M
	 * @param string
	 * @param payoutDetailsResp
	 * @param info
	 * @return
	 */
	private PayoutDetailsEntity preparePayoutDetails(PaymentReqModel model, PayoutDetailsResp payoutDetailsResp,
			String bankCode, ClinetInfoModel info) {
		PayoutDetailsEntity response = new PayoutDetailsEntity();
		try {
			response.setAmount(String.valueOf(model.getAmount()));
			response.setBankActNo(model.getBankActNo());
			response.setBankName(model.getBankName());
			response.setBackofficeCode(payoutDetailsResp.getBackofficeCode());
			response.setBankCode(bankCode);
			response.setPayment(AppConstants.PAYMENT);
			response.setIfscCode(model.getIfscCode());
			if (model.getIfscCode().contains("HDFC")) {
				response.setPayMethod(AppConstants.TRANSFER);
			} else {
				response.setPayMethod(AppConstants.NEFT);
			}
			response.setClientName(model.getClientName());
			response.setDevice(model.getDevice());
			response.setOrderId(model.getOrderId());
			response.setPaymentReason(model.getPaymentReason());
			response.setPaymentStatus(model.getPaymentStatus());
			response.setReceipt(model.getReceipt());
			response.setSegment(model.getSegment());
			response.setUpiId(model.getUpiId());
			response.setUserId(info.getUserId());
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to validate Payout parameter
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private boolean validatePayoutParams(PaymentReqModel model) {
		if (StringUtil.isNotNullOrEmpty(model.getBankActNo()) && StringUtil.isNotNullOrEmpty(model.getIfscCode())
				&& model.getAmount() > 0) {
			return true;
		}
		return false;
	}

	/*
	 * Method to create pay out details
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private void createPayOut(String userId, PaymentReqModel model) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// Check the given Amount is lesser than Due Amount
					JSONArray checkBalanceResponse = (JSONArray) backOfficeRestService
							.loginBackOfficeCheckBalance(userId);
					if (checkBalanceResponse != null) {
						JSONObject tempJson = (JSONObject) checkBalanceResponse.get(0);
						if (tempJson != null) {
							JSONArray tempData = (JSONArray) tempJson.get("DATA");
							if (tempData != null && tempData.size() > 0) {
								JSONArray data = (JSONArray) tempData.get(0);
								if (data != null && data.size() > 0) {
//							Double balance = (Double) data.get(data.size() - 2);
									String balance = data.get(data.size() - 1).toString();
									Double dueAmount = Double.parseDouble(balance);
									if (model.getAmount() <= dueAmount) {
										JSONArray boPayOutResponse = (JSONArray) backOfficeRestService
												.loginBackOfficeBopayOut(userId, model.getBankActNo(),
														model.getIfscCode(), model.getSegment(), model.getAmount(),
														model.getPaymentReason());
										if (boPayOutResponse != null && boPayOutResponse.size() > 0) {
											JSONObject boPayResponseJson = (JSONObject) boPayOutResponse.get(0);
											if (boPayResponseJson != null) {
												JSONArray boPayOutData = (JSONArray) boPayResponseJson.get("DATA");
												if (boPayOutData != null && boPayOutData.size() > 0) {
													JSONArray boPayOutData2 = (JSONArray) boPayOutData.get(0);
													if (boPayOutData2 != null && boPayOutData2.size() > 0) {
														String originalData = (String) boPayOutData2.get(0);
														if (StringUtil.isNotNullOrEmpty(originalData)) {
															if (originalData.equalsIgnoreCase("Sucess:1")) {
															} else {
																String message = " Payout for user " + userId
																		+ " with bank account no : "
																		+ model.getBankActNo() + " with amount "
																		+ model.getAmount()
																		+ " is Failed in BackOffice ";
																emailUtils.paymentFailureEmail(message);

															}
														} else {
															String message = " Payout for user " + userId
																	+ " with bank account no : " + model.getBankActNo()
																	+ " with amount " + model.getAmount()
																	+ " is Failed in BackOffice ";
															emailUtils.paymentFailureEmail(message);
														}
													}
												} else {
													String message = " Payout for user " + userId
															+ " with bank account no : " + model.getBankActNo()
															+ " with amount " + model.getAmount()
															+ " is Failed in BackOffice ";
													emailUtils.paymentFailureEmail(message);
												}
											} else {
												String message = " Payout for user " + userId
														+ " with bank account no : " + model.getBankActNo()
														+ " with amount " + model.getAmount()
														+ " is Failed in BackOffice ";
												emailUtils.paymentFailureEmail(message);
											}
										} else {
											String message = " Payout for user " + userId + " with bank account no : "
													+ model.getBankActNo() + " with amount " + model.getAmount()
													+ " is Failed in BackOffice ";
											emailUtils.paymentFailureEmail(message);
										}
									}
								} else {
									String message = " Payout for user " + userId + " with bank account no : "
											+ model.getBankActNo() + " with amount " + model.getAmount()
											+ " is Failed in BackOffice ";
									emailUtils.paymentFailureEmail(message);
								}
							} else {
								String message = " Payout for user " + userId + " with bank account no : "
										+ model.getBankActNo() + " with amount " + model.getAmount()
										+ " is Failed in BackOffice ";
								emailUtils.paymentFailureEmail(message);
							}
						} else {
							String message = " Payout for user " + userId + " with bank account no : "
									+ model.getBankActNo() + " with amount " + model.getAmount()
									+ " is Failed in BackOffice ";
							emailUtils.paymentFailureEmail(message);
						}
					} else {
						String message = " Payout for user " + userId + " with bank account no : "
								+ model.getBankActNo() + " with amount " + model.getAmount()
								+ " is Failed in BackOffice ";
						emailUtils.paymentFailureEmail(message);
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();

	}

	/**
	 * Method to cancel payout
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> cancelPayout(ClinetInfoModel info, PaymentReqModel model) {
		try {
			if (!validateCancelPayoutReq(info, model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			int canclePayout = payoutDetailsRepository.canclePayout(info.getUserId(), model.getBankActNo(),
					model.getIfscCode());
			if (canclePayout > 0)
				return prepareResponse.prepareSuccessMessage(AppConstants.PAYOUT_CANCELLED);
			return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);

		} catch (Exception e) {
			Log.error("cancelPayOut", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/*
	 * method to cancel payout
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> cancelPayOut(ClinetInfoModel info, PaymentReqModel model) {
//		try {
//			if (!validateCancelPayoutReq(info, model))
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//
//			JSONArray boPayOutResponse = (JSONArray) backOfficeRestService.loginBackOfficeBopayOut(info.getUserId(),
//					model.getBankActNo(), model.getIfscCode(), model.getSegment(), 0, model.getPaymentReason());
//			if (boPayOutResponse != null && boPayOutResponse.size() > 0) {
//				JSONObject boPayResponseJson = (JSONObject) boPayOutResponse.get(0);
//				if (boPayResponseJson != null) {
//					JSONArray boPayOutData = (JSONArray) boPayResponseJson.get("DATA");
//					if (boPayOutData != null && boPayOutData.size() > 0) {
//						JSONArray boPayOutData2 = (JSONArray) boPayOutData.get(0);
//						if (boPayOutData2 != null && boPayOutData2.size() > 0) {
//							String originalData = (String) boPayOutData2.get(0);
//							if (StringUtil.isNotNullOrEmpty(originalData)) {
//								if (originalData.equalsIgnoreCase("Sucess:1")) {
//									return prepareResponse.prepareSuccessResponseObject(originalData);
//								} else {
//									String[] data2 = originalData.split(",");
//									String statusContent = data2[1];
//									return prepareResponse.prepareFailedResponse(statusContent);
//
//								}
//							} else {
//								return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
//							}
//						}
//					} else {
//						return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
//					}
//				} else {
//					return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
//				}
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
//			}
//
//		} catch (Exception e) {
//			Log.error(e.getMessage());
//			e.printStackTrace();
//		}
//		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//	}

	/**
	 * method to validate cancel payout parameters
	 * 
	 * @author SOWMIYA
	 * @param paymentReqModel
	 * @param info
	 */
	private boolean validateCancelPayoutReq(ClinetInfoModel info, PaymentReqModel model) {
		if (StringUtil.isNotNullOrEmpty(info.getUserId()) && StringUtil.isNotNullOrEmpty(model.getBankActNo())
				&& StringUtil.isNotNullOrEmpty(model.getIfscCode())) {
			return true;
		}
		return false;
	}

	/**
	 * Method to update Payout
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> updatePayout(ClinetInfoModel info, PaymentReqModel model) {
		try {
			if (!validateCancelPayoutReq(info, model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			int canclePayout = payoutDetailsRepository.updatePayout(String.valueOf(model.getAmount()), info.getUserId(),
					model.getBankActNo(), model.getIfscCode());
			if (canclePayout > 0)
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);

		} catch (Exception e) {
			Log.error("cancelPayOut", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to upload bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> uploadBankDetails(FormDataModel file) {
		try {
			/** check parameters **/
			if (file.getFile() == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			Path files = file.getFile().filePath();
//			List<BankDetailsEntity> bankDetails = new ArrayList<>();
			List<ClientBankDetailsEntity> bankDetails = new ArrayList<>();
			try (BufferedReader br = new BufferedReader(new FileReader(files.toString()))) {
				String line;
				int lineNumber = 0;
				while ((line = br.readLine()) != null) {
//			    	BankDetailsEntity detail = new BankDetailsEntity();
					ClientBankDetailsEntity detail = new ClientBankDetailsEntity();
					lineNumber++;
					if (lineNumber != 1 && line.length() != 1) {
						String[] columns = line.split("\\|");

						String userId = columns[0];
						String userName = columns[1];
						String mobileNo = columns[2];
						String email = columns[3];
						String accNo = columns[4];
						String IFSCCode = columns[5];
						String bankName = columns[6];
						String defaultBank = columns[7];

						detail.setClientId(userId);
						detail.setClientName(userName);
						detail.setMobile(mobileNo);
						detail.setEmail(email);
						detail.setBankAcno(accNo);
						detail.setIfscCode(IFSCCode);
						detail.setClientBankName(bankName);
						detail.setDefaultAccBank(defaultBank);
						bankDetails.add(detail);
//			    		detail.setUserId(userId);
//			    		detail.setUserName(userName);
//			    		detail.setMobileNo(mobileNo);
//			    		detail.setEmail(email);
//			    		detail.setAccNo(accNo);
//			    		detail.setIfscCode(IFSCCode);
//			    		detail.setBankName(bankName);
//			    		detail.setDefaultBank(defaultBank);
//			    		bankDetails.add(detail);

					}
				}
			} catch (IOException e) {
				Log.error(e);
			}

//			List<BankDetailsEntity> saveReq = bankDetailsRepository.saveAll(bankDetails);
			List<ClientBankDetailsEntity> saveReq = clientBankDetailRepository.saveAll(bankDetails);
			if (StringUtil.isListNotNullOrEmpty(saveReq)) {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.SUCCESS_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get last five payin transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getpayInTransactions(ClinetInfoModel info) {
		try {
			List<PaymentRefEntity> payInTransactionsData = paymentRefRepo.getpayInTransactions(info.getUserId());
			if (StringUtil.isListNotNullOrEmpty(payInTransactionsData)) {
//				List<PaymentRefEntity> list = new ArrayList<>();
				List<PayInResponse> list = new ArrayList<>();

				int size = 0;
				if (payInTransactionsData.size() <= 4) {
					size = payInTransactionsData.size();
				} else {
					size = 5;
				}

				for (int i = 0; i <= size - 1; i++) {
//					PaymentRefEntity resp = new PaymentRefEntity();
					PayInResponse resp = new PayInResponse();
					resp.setAccNum(payInTransactionsData.get(i).getAccNum());
//					resp.setActiveStatus(payInTransactionsData.get(i).getActiveStatus());
					resp.setAmount(payInTransactionsData.get(i).getAmount());
					resp.setBankName(payInTransactionsData.get(i).getBankName());
					resp.setBoUpdate(payInTransactionsData.get(i).getBoUpdate());
//					resp.setCreatedBy(payInTransactionsData.get(i).getCreatedBy());
					resp.setCreatedOn(payInTransactionsData.get(i).getCreatedOn());
					resp.setExchSeg(payInTransactionsData.get(i).getExchSeg());
//					resp.setId(payInTransactionsData.get(i).getId());
					resp.setOrderId(payInTransactionsData.get(i).getOrderId());
					resp.setPaymentMethod(payInTransactionsData.get(i).getPaymentMethod());
					resp.setPaymentStatus(payInTransactionsData.get(i).getPaymentStatus());
					resp.setReceiptId(payInTransactionsData.get(i).getReceiptId());
//					resp.setUpdatedBy(payInTransactionsData.get(i).getUpdatedBy());
					resp.setUpdatedOn(payInTransactionsData.get(i).getUpdatedOn());
					resp.setUpiId(payInTransactionsData.get(i).getUpiId());
					resp.setUserId(payInTransactionsData.get(i).getUserId());
					resp.setVoucherNo(payInTransactionsData.get(i).getVoucherNo());
					list.add(resp);
				}
				return prepareResponse.prepareSuccessResponseObject(list);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Get pay in transaction -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get last five payout transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getpayOutTransactions(ClinetInfoModel info) {
		try {
			List<PayoutDetailsEntity> payOutTransactionsData = payoutDetailsRepository
					.getpayOutTransactions(info.getUserId());
			if (StringUtil.isListNotNullOrEmpty(payOutTransactionsData)) {
//				List<PayoutDetailsEntity> list = new ArrayList<>();
				List<PayoutResponse> list = new ArrayList<>();
				int size = 0;
				if (payOutTransactionsData.size() <= 4) {
					size = payOutTransactionsData.size();
				} else {
					size = 4;
				}

				for (int i = 0; i <= size - 1; i++) {
//					PayoutDetailsEntity resp = new PayoutDetailsEntity();
					PayoutResponse resp = new PayoutResponse();
//					resp.setActiveStatus(payOutTransactionsData.get(i).getActiveStatus());
					resp.setAmount(payOutTransactionsData.get(i).getAmount());
					resp.setBackofficeCode(payOutTransactionsData.get(i).getBackofficeCode());
					resp.setBankActNo(payOutTransactionsData.get(i).getBankActNo());
					resp.setBankCode(payOutTransactionsData.get(i).getBankCode());
					resp.setBankName(payOutTransactionsData.get(i).getBankName());
					resp.setClientName(payOutTransactionsData.get(i).getClientName());
//					resp.setCreatedBy(payOutTransactionsData.get(i).getCreatedBy());
					resp.setCreatedOn(payOutTransactionsData.get(i).getCreatedOn());
					resp.setDevice(payOutTransactionsData.get(i).getDevice());
//					resp.setId(payOutTransactionsData.get(i).getId());
					resp.setIfscCode(payOutTransactionsData.get(i).getIfscCode());
					resp.setOrderId(payOutTransactionsData.get(i).getOrderId());
					resp.setPayment(payOutTransactionsData.get(i).getPayment());
					resp.setPaymentReason(payOutTransactionsData.get(i).getPaymentReason());
					resp.setPaymentStatus(payOutTransactionsData.get(i).getPaymentStatus());
					resp.setPayMethod(payOutTransactionsData.get(i).getPayMethod());
					resp.setReceipt(payOutTransactionsData.get(i).getReceipt());
					resp.setSegment(payOutTransactionsData.get(i).getSegment());
//					resp.setUpdatedBy(payOutTransactionsData.get(i).getUpdatedBy());
					resp.setUpdatedOn(payOutTransactionsData.get(i).getUpdatedOn());
					resp.setUpiId(payOutTransactionsData.get(i).getUpiId());
					resp.setUserId(payOutTransactionsData.get(i).getUserId());
					list.add(resp);
				}

				return prepareResponse.prepareSuccessResponseObject(list);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Get pay in transaction -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

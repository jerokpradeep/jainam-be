package in.codifi.funds.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.controller.spec.PaymentControllerSpec;
import in.codifi.funds.model.request.FormDataModel;
import in.codifi.funds.model.request.PaymentReqModel;
import in.codifi.funds.model.request.UPIReqModel;
import in.codifi.funds.model.request.VerifyPaymentReqModel;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.service.spec.PaymentServiceSpec;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/payment")
public class PaymentController implements PaymentControllerSpec {
	@Inject
	PaymentServiceSpec paymentService;
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * method to create new payment details
	 * 
	 * @author Gowthaman M
	 * @param limitOrderEntity
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createNewPayment(PaymentReqModel limitOrderEntity) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("108055");
		return paymentService.createNewPayment(limitOrderEntity, info);
	}

	/**
	 * Method to get UPI ID
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getUPIId() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return paymentService.getUPIId(info);
	}

	/**
	 * Method to set UPI ID
	 * 
	 * @author Gowthaman M
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> setUPIId(UPIReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return paymentService.setUPIId(info, model);
	}

	/**
	 * method to get payment details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getPaymentDetails() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("108055");
		return paymentService.getPaymentDetails(info);
	}

	/**
	 * Method to verify payments
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> verifyPayments(VerifyPaymentReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return paymentService.verifyPayments(info, model);
	}

	/**
	 * method to get pay out check bank details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> payOutCheckBalance() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return paymentService.payOutCheckBalance(info);
	}

	/**
	 * method to get pay out details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPayOutDetails() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return paymentService.getPayOutDetails(info);
	}

	/**
	 * method to get pay out details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> payOut(PaymentReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("117995");
		return paymentService.payOut(info, model);
	}

	/**
	 * method to cancel pay out
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> cancelPayout(PaymentReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return paymentService.cancelPayout(info, model);
	}

	/**
	 * method to update payout
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> updatePayout(PaymentReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return paymentService.updatePayout(info, model);
	}

	/**
	 * Method to upload bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> uploadBankDetails(FormDataModel file) {
		if (file == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return paymentService.uploadBankDetails(file);
	}

	/**
	 * Method to get last five payin transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getpayInTransactions() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return paymentService.getpayInTransactions(info);
	}

	/**
	 * Method to get last five payout transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getpayOutTransactions() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return paymentService.getpayOutTransactions(info);
	}

}

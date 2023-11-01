package in.codifi.orders.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.resteasy.reactive.RestResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.model.request.MarginReqModel;
import in.codifi.orders.model.request.OrderDetails;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.model.response.PositionResponse;
import in.codifi.orders.service.spec.OrderExecutionServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.OrdersRestService;
import in.codifi.orders.ws.model.BoMainLeg;
import in.codifi.orders.ws.model.BoProfitLeg;
import in.codifi.orders.ws.model.BoStoplossLeg;
import in.codifi.orders.ws.model.BoTrail;
import in.codifi.orders.ws.model.BracketOrderLeg;
import in.codifi.orders.ws.model.BracketOrderReq;
import in.codifi.orders.ws.model.CoLeg;
import in.codifi.orders.ws.model.CoMainLeg;
import in.codifi.orders.ws.model.CoStoplossLeg;
import in.codifi.orders.ws.model.CoverOrderReqModel;
import in.codifi.orders.ws.model.Leg;
import in.codifi.orders.ws.model.LegDetail;
import in.codifi.orders.ws.model.ModifyCoverOrderReqModel;
import in.codifi.orders.ws.model.OrderMarginReqModel;
import in.codifi.orders.ws.model.PlaceOrderReqModel;
import in.codifi.orders.ws.model.ScripInfo;
import in.codifi.ws.model.odin.ModifyOrderReqModel;
import io.quarkus.logging.Log;

@Component
public class OrderExecutionService implements OrderExecutionServiceSpec {

	@Inject
	AppUtil appUtil;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	OrdersRestService restService;

	@Inject
	RestServiceProperties restProp;

	/**
	 * @author Dinesh
	 * @modified author Nesan
	 * 
	 * @desc Method to place single and multiple order
	 */
	@Override
	public RestResponse<List<GenericResponse>> placeOrder(List<OrderDetails> orderDetails, ClinetInfoModel info) {

		try {
			/** Verify session **/
			Log.info("Orders  userId - " + info.getUserId());
//			String userSession = AppUtil.getUserSession(info.getUserId());
			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDE3ODA0Mjg2MDZFOUQxQjMzNTE0MDBFNTE2NkExIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODg2MzM5OSwiaWF0IjoxNjk4ODQ2OTg3fQ.b4h-TQEYuXDr7_ay7D081e0DpCM-rfB_wz2z0dacNqo";
			Log.info("Orders  userSession - " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponseForList();

			List<GenericResponse> response = executeOrdersSimultaneously(orderDetails, userSession, info);
			return prepareResponse.prepareSuccessRespForList(response);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method execute place order simultaneously
	 * 
	 * @author Nesan
	 *
	 * @param ordersReq
	 * @param userSession
	 * @param info
	 * @return
	 */
	private List<GenericResponse> executeOrdersSimultaneously(List<OrderDetails> ordersReq, String userSession,
			ClinetInfoModel info) {

		List<Callable<GenericResponse>> tasks = new ArrayList<>();

		List<OrderDetails> newordersReq = new ArrayList<>();
		newordersReq = sliceOrder(ordersReq);
//		for (OrderDetails orderDetails : ordersReq) {
		for (OrderDetails orderDetails : newordersReq) {
			Callable<GenericResponse> task = () -> {
				/** Validate Request **/
				if (!validateExecuteOrderReq(orderDetails))
					return prepareResponse.prepareFailedResponseBody(AppConstants.INVALID_PARAMETER);
//				if (orderDetails.getOrderType().equalsIgnoreCase(AppConstants.BRACKET)) {
//					String req = prepareBracketOrderReq(orderDetails, userSession, info);
//					return restService.executeBracketOrder(req, userSession, info.getUserId());
//				} else
				if (orderDetails.getOrderType().equalsIgnoreCase(AppConstants.COVER)) {
					String req = prepareCoverOrderReq(orderDetails, userSession, info);
					if (StringUtil.isNullOrEmpty(req))
						return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
					return restService.executeCoverOrder(req, userSession, info.getUserId());
				} else {
					String req = preparePlaceOrderReq(orderDetails, userSession, info);
					Log.error("executeOrdersSimultaneously" + req);
					if (StringUtil.isNullOrEmpty(req))
						return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);

					/** Execute Place Order **/
					return restService.executePlaceOrder(req, userSession, orderDetails.getReqId(), info.getUserId());
				}
//				return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
			};
			tasks.add(task);
		}
		return tasks.stream().map(callable -> {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Method to slice NFO order based on freeze Qty
	 * 
	 * @author Dinesh Kumar
	 * @updated Gowthaman M
	 * @param ordersReq
	 * @return
	 */
	private List<OrderDetails> sliceOrder(List<OrderDetails> ordersReq) {
		List<OrderDetails> orderDetailsList = new ArrayList<>();
		try {
			for (int i = 0; i < ordersReq.size(); i++) {
				OrderDetails orderDetails = new OrderDetails();
				orderDetails = ordersReq.get(i);
				if (orderDetails.getExchange().equalsIgnoreCase("NFO")) {
					String token = orderDetails.getToken();
					String exchange = orderDetails.getExchange();
					if (HazelcastConfig.getInstance().getContractMaster().get(exchange + "_" + token) != null) {
						int qty = Integer.parseInt(orderDetails.getQty());
						ContractMasterModel masterModel = HazelcastConfig.getInstance().getContractMaster()
								.get(exchange + "_" + token);
						int freezeQty = Integer.parseInt(masterModel.getFreezQty());
						if (freezeQty > 0) {
							if (qty > freezeQty) {
								int tempQty = qty / freezeQty;
								int balanceQty = qty;
								if (tempQty > 0) {
									List<OrderDetails> detailsList = new ArrayList<>();
									for (int j = 0; j < tempQty; j++) {
										OrderDetails orderDetails1 = new OrderDetails();
										BeanUtils.copyProperties(orderDetails1, orderDetails);
										if (balanceQty > freezeQty) {
											orderDetails1.setQty(String.valueOf(freezeQty));
											detailsList.add(orderDetails1);
											balanceQty = balanceQty - freezeQty;
										} else {
											orderDetails1.setQty(String.valueOf(balanceQty));
											detailsList.add(orderDetails1);
											balanceQty = balanceQty - balanceQty;
										}
									}
									if (balanceQty > 0) {
										OrderDetails orderDetails2 = new OrderDetails();
										BeanUtils.copyProperties(orderDetails2, orderDetails);
										orderDetails2.setQty(String.valueOf(balanceQty));
										detailsList.add(orderDetails2);
									}
									orderDetailsList.addAll(detailsList);
								} else {
									orderDetailsList.add(orderDetails);
								}
							} else {
								orderDetailsList.add(orderDetails);
							}
						} else {
							orderDetailsList.add(orderDetails);
						}
					} else {
						orderDetailsList.add(orderDetails);
					}
				} else {
					orderDetailsList.add(orderDetails);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return orderDetailsList;
	}

	/**
	 * Method to Prepare Cover order for ODIN
	 * 
	 * @author Nesan
	 * 
	 * @param orderDetails
	 * @param userSession
	 * @param info
	 * @return
	 */
	private String prepareCoverOrderReq(OrderDetails orderDetails, String userSession, ClinetInfoModel info) {
		CoverOrderReqModel coverOrderReqModel = new CoverOrderReqModel();
		ObjectMapper mapper = new ObjectMapper();
		String placeOrderReq = null;
		try {

			if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				coverOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				coverOrderReqModel.getScripInfo().setExchange(AppConstants.BSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				coverOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_FO);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
//				coverOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_CUR);
				return null;
			} else {
				return null;
			}

//			placeOrderReqModel.getScripInfo().setSymbol(orderDetails.getTradingSymbol());//TODO
			coverOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderDetails.getToken()));

			coverOrderReqModel.setTransactionType(orderDetails.getTransType());

			coverOrderReqModel.getMainLeg().setOrderType(AppConstants.RL_MKT);
			coverOrderReqModel.getMainLeg().setQuantity(Integer.parseInt(orderDetails.getQty()));
			coverOrderReqModel.getMainLeg().setPrice(0);

			List<Leg> legs = new ArrayList<>();
			Leg leg = new Leg();
//			leg.setPrice(Float.parseFloat(orderDetails.getPrice()));
			leg.setPrice(Float.parseFloat(orderDetails.getTriggerPrice()));
			leg.setQuantity(0);
			leg.setTriggerPrice(Float.parseFloat(orderDetails.getTriggerPrice()));
			legs.add(leg);
			coverOrderReqModel.getStoplossLeg().setLegs(legs);
			// Swap value start

			coverOrderReqModel.setOrderIdentifier("");
			coverOrderReqModel.setPartCode("");
			coverOrderReqModel.setAlgoId("");
			coverOrderReqModel.setStrategyId("");
			coverOrderReqModel.setVenderCode("");

			placeOrderReq = mapper.writeValueAsString(coverOrderReqModel);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return placeOrderReq;
	}

	/**
	 * Method to check numeric
	 * 
	 * @author Gowthaman
	 * @param strNum
	 * @return
	 */
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Method to prepare Bracket Order request for ODIN
	 * 
	 * @author Nesan
	 * 
	 * @param orderDetails
	 * @param userSession
	 * @param info
	 * @return
	 */
//	private String prepareBracketOrderReq(OrderDetails orderDetails, String userSession, ClinetInfoModel info) {
//		BracketOrderReq boOrderReqModel = new BracketOrderReq();
//		ObjectMapper mapper = new ObjectMapper();
//		String placeOrderReq = null;
//		try {
//
//			if (orderDetails.getExchange().equalsIgnoreCase("NSE")) {
//				boOrderReqModel.getScripInfo().setExchange("NSE_EQ");
//			} else if (orderDetails.getExchange().equalsIgnoreCase("BSE")) {
//				boOrderReqModel.getScripInfo().setExchange("BSE_EQ");
//			} else if (orderDetails.getExchange().equalsIgnoreCase("NFO")) {
//				boOrderReqModel.getScripInfo().setExchange("NSE_FO");
//			} else {
//				return null;
//			}
//
//			boOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderDetails.getToken()));
//			boOrderReqModel.getScripInfo().setSymbol(orderDetails.getTradingSymbol());
//
//			boOrderReqModel.setTransType(orderDetails.getTransType());
//			
//			boOrderReqModel.getBoMainLeg().setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderDetails.getPriceType()));
//			boOrderReqModel.getBoMainLeg().setQty(orderDetails.getQty());
//			boOrderReqModel.getBoMainLeg().setPrice("0");
//			boOrderReqModel.getBoMainLeg().setTriggerPrice(orderDetails.getTriggerPrice());
//			boOrderReqModel.getBoMainLeg().setTradedQty("0");
//
//			List<Leg> legs = new ArrayList<>();
//			Leg leg = new Leg();
//			leg.setPrice(Integer.parseInt(orderDetails.getPrice()));
//			leg.setQuantity(0);
//			leg.setTriggerPrice(Integer.parseInt(orderDetails.getTriggerPrice()));
//			legs.add(leg);
//			boOrderReqModel.getStopLossLeg().setLegs(legs);
//			
//			BoTrail boTrail = new BoTrail();
//			boTrail.setLtpJumpPrice(Float.parseFloat(orderDetails.getStopLoss()));
//			boTrail.setStopLossJumpPrice(Float.parseFloat(orderDetails.getStopLoss()));
//			boOrderReqModel.getStopLossLeg().setTrail(boTrail);
//
//			List<Leg> plLegs = new ArrayList<>();
//			Leg plLeg = new Leg();
//			plLeg.setPrice(Integer.parseInt(orderDetails.getPrice()));
//			plLeg.setQuantity(Integer.parseInt(orderDetails.getQty()));
//			plLegs.add(plLeg);
//			boOrderReqModel.getBoProfitLeg().setLegs(plLegs);
//			
//			boOrderReqModel.setOrderIdentifier("");
//			boOrderReqModel.setPartCode("");
//			boOrderReqModel.setAlgoId("");
//			boOrderReqModel.setStrategyId("");
//			boOrderReqModel.setVenderCode("");
//
//			placeOrderReq = mapper.writeValueAsString(boOrderReqModel);
//
//		} catch (Exception e) {
//			Log.error(e.getMessage());
//		}
//		return placeOrderReq;
//	}

	/**
	 * Method to prepare Bracket Order Request
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	private String prepareBracketOrderReq(OrderDetails orderDetails, String userSession, ClinetInfoModel info) {
		BracketOrderReq boOrderReqModel = new BracketOrderReq();
		ScripInfo scripInfo = new ScripInfo();
		BoMainLeg boMainLeg = new BoMainLeg();
		BoStoplossLeg boStoplossLeg = new BoStoplossLeg();
		BoProfitLeg boProfitLeg = new BoProfitLeg();
		ObjectMapper mapper = new ObjectMapper();
		String placeOrderReq = null;
		try {

			if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				scripInfo.setExchange(AppConstants.NSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				scripInfo.setExchange(AppConstants.BSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				scripInfo.setExchange(AppConstants.NSE_FO);
			} else {
				return null;
			}

			scripInfo.setScripToken(Integer.parseInt(orderDetails.getToken()));
			scripInfo.setSymbol(orderDetails.getTradingSymbol());
			boOrderReqModel.setScripInfo(scripInfo);

//			boOrderReqModel.getScripInfo().setSymbol(orderDetails.getTradingSymbol());
//			boOrderReqModel.getScripInfo().setSeries(placeOrderReq);// TODO
//			boOrderReqModel.getScripInfo().setExpiryDate(placeOrderReq);// TODO
//			boOrderReqModel.getScripInfo().setStrikePrice(placeOrderReq);// TODO
//			boOrderReqModel.getScripInfo().setOptionType(placeOrderReq);// TODO

			boOrderReqModel.setTransType(orderDetails.getTransType());
			boMainLeg.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderDetails.getPriceType()));
			boMainLeg.setQty(orderDetails.getQty());
			boMainLeg.setPrice("0");
			boMainLeg.setTriggerPrice(orderDetails.getTriggerPrice());
			boMainLeg.setTradedQty("0");// TODO

			boOrderReqModel.setBoMainLeg(boMainLeg);

//			boOrderReqModel.getBoMainLeg()
//					.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderDetails.getPriceType()));
//			boOrderReqModel.getBoMainLeg().setQty(orderDetails.getQty());
//			boOrderReqModel.getBoMainLeg().setPrice("0");
//			boOrderReqModel.getBoMainLeg().setTriggerPrice(orderDetails.getTriggerPrice());
//			boOrderReqModel.getBoMainLeg().setTradedQty(placeOrderReq);

//			List<Leg> slLegList = new ArrayList<>();
//			Leg slLeg = new Leg();
//			slLeg.setPrice(Integer.parseInt(orderDetails.getPrice()));
//			slLeg.setQuantity(0);
//			slLeg.setTriggerPrice(Integer.parseInt(orderDetails.getTriggerPrice()));
//			slLegList.add(slLeg);
//			boStoplossLeg.setLegs(slLegList);

			List<BracketOrderLeg> slLegList = new ArrayList<>();
			BracketOrderLeg slLeg = new BracketOrderLeg();
			slLeg.setPrice(Integer.parseInt(orderDetails.getPrice()));
			slLeg.setQuantity(0);
			slLeg.setTriggerPrice(Integer.parseInt(orderDetails.getTriggerPrice()));
			slLegList.add(slLeg);
			boStoplossLeg.setLegs(slLegList);

			BoTrail boTrail = new BoTrail();
			boTrail.setLtpJumpPrice(Float.parseFloat(orderDetails.getStopLoss()));
			boTrail.setStopLossJumpPrice(Float.parseFloat(orderDetails.getStopLoss()));
			boStoplossLeg.setTrail(boTrail);

			boOrderReqModel.setStopLossLeg(boStoplossLeg);

			List<Leg> plLegList = new ArrayList<>();
			Leg plLeg = new Leg();
			plLeg.setPrice(Integer.parseInt(orderDetails.getPrice()));
			plLeg.setQuantity(Integer.parseInt(orderDetails.getQty()));
			plLegList.add(plLeg);
			boProfitLeg.setLegs(plLegList);

			boOrderReqModel.setBoProfitLeg(boProfitLeg);

			boOrderReqModel.setOrderIdentifier("");
			boOrderReqModel.setPartCode("");
			boOrderReqModel.setAlgoId("");
			boOrderReqModel.setStrategyId("");
			boOrderReqModel.setVenderCode("");

			placeOrderReq = mapper.writeValueAsString(boOrderReqModel);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeOrderReq;
	}

	/**
	 * Method to prepare place order req
	 * 
	 * @param orderDetails
	 * @param userSession
	 * @param info
	 * @return
	 */
	private String preparePlaceOrderReq(OrderDetails orderDetails, String userSession, ClinetInfoModel info) {
		PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
		ObjectMapper mapper = new ObjectMapper();
		String placeOrderReq = null;
		try {

			if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				placeOrderReqModel.getScripInfo().setExchange(AppConstants.BSE_EQ);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_FO);
			} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
//				placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_CUR);
				return null;
			} else {
				return null;
			}

//			placeOrderReqModel.getScripInfo().setSymbol(orderDetails.getTradingSymbol());//TODO
			placeOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderDetails.getToken()));

			placeOrderReqModel.setTransactionType(orderDetails.getTransType());

			// Swap value start
			// Added on 24-06-23 //TODO consult with Dinesh bro
			if (placeOrderReqModel.getScripInfo().getExchange().equalsIgnoreCase(AppConstants.NSE_FO)
					&& orderDetails.getProduct().equalsIgnoreCase(AppConstants.NRML)) {
				placeOrderReqModel.setProductType(AppConstants.DELIVERY);
			} else {
				placeOrderReqModel
						.setProductType(HazelcastConfig.getInstance().getProductTypes().get(orderDetails.getProduct()));
			}

			placeOrderReqModel
					.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderDetails.getPriceType()));

			placeOrderReqModel.setQuantity(orderDetails.getQty());
			placeOrderReqModel.setPrice(Float.parseFloat(orderDetails.getPrice()));
			if (StringUtil.isNotNullOrEmpty(orderDetails.getTriggerPrice())) {
				placeOrderReqModel.setTriggerPrice(Float.parseFloat(orderDetails.getTriggerPrice()));
			}
			if (StringUtil.isNotNullOrEmpty(orderDetails.getDisclosedQty())) {
				placeOrderReqModel.setDisclosedQuantity(Integer.parseInt(orderDetails.getDisclosedQty()));
			}
			if (StringUtil.isNotNullOrEmpty(orderDetails.getRet())) {
				boolean ret = isNumeric(orderDetails.getRet());
				if (ret) {
					placeOrderReqModel.setValidity(AppConstants.GTD);
					placeOrderReqModel.setValidityDays(Integer.parseInt(orderDetails.getRet()));
				} else {
					placeOrderReqModel.setValidity(orderDetails.getRet());
				}
			}
			if (StringUtil.isNotNullOrEmpty(orderDetails.getOrderType())
					&& orderDetails.getOrderType().equalsIgnoreCase(AppConstants.AMO)) {
				placeOrderReqModel.setIsAmo(true);
			}

			// TODO placeOrderReqModel.setOrderIdentifier(null); //
			placeOrderReqModel.setPartCode(null); // placeOrderReqModel.setAlgoId(null);
			// placeOrderReqModel.setStrategyId(null); //
			placeOrderReqModel.setVenderCode(null);

			placeOrderReq = mapper.writeValueAsString(placeOrderReqModel);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeOrderReq;
	}

	/**
	 * Method to prepare square off position req
	 * 
	 * @param orderDetails
	 * @param userSession
	 * @param info
	 * @return
	 */
	private String prepareSquareOffReq(List<OrderDetails> orderDetails, ClinetInfoModel info) {
		PlaceOrderReqModel placeOrderReqModel = null;
		List<PlaceOrderReqModel> placeOrderReqModelList = new ArrayList<PlaceOrderReqModel>();
		ObjectMapper mapper = new ObjectMapper();
		String placeOrderReq = null;
		try {
			for (OrderDetails orderDetail : orderDetails) {
				placeOrderReqModel = new PlaceOrderReqModel();

				if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_EQ);
				} else if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.BSE_EQ);
				} else if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_FO);
				} else {
					return null;
				}

//				placeOrderReqModel.getScripInfo().setSymbol(orderReqModel.getTradingSymbol());
				placeOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderDetail.getToken()));

				placeOrderReqModel.setTransactionType(orderDetail.getTransType());

				// Swap value start
				placeOrderReqModel
						.setProductType(HazelcastConfig.getInstance().getProductTypes().get(orderDetail.getProduct()));
				placeOrderReqModel
						.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderDetail.getPriceType()));

				placeOrderReqModel.setQuantity(orderDetail.getQty());
				placeOrderReqModel.setPrice(Float.parseFloat(orderDetail.getPrice()));
				if (StringUtil.isNotNullOrEmpty(orderDetail.getTriggerPrice())) {
					placeOrderReqModel.setTriggerPrice(Integer.parseInt(orderDetail.getTriggerPrice()));
				}
				if (StringUtil.isNotNullOrEmpty(orderDetail.getDisclosedQty())) {
					placeOrderReqModel.setDisclosedQuantity(Integer.parseInt(orderDetail.getDisclosedQty()));
				}
				if (StringUtil.isNotNullOrEmpty(orderDetail.getRet())) {
					placeOrderReqModel.setValidity(orderDetail.getRet());
				}
				// TODO if(StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
				// placeOrderReqModel.setValidityDays(null);
				// }
				if (StringUtil.isNotNullOrEmpty(orderDetail.getOrderType())
						&& orderDetail.getOrderType() == AppConstants.AMO) {
					placeOrderReqModel.setIsAmo(true);
				}
				placeOrderReqModel.setOrderIdentifier("");

				// TODO placeOrderReqModel.setOrderIdentifier(null); //
				// TODO placeOrderReqModel.setPartCode(null); //
				// placeOrderReqModel.setAlgoId(null);
				// placeOrderReqModel.setStrategyId(null); //
				// TODO placeOrderReqModel.setVenderCode(null);
				placeOrderReqModelList.add(placeOrderReqModel);
			}

			placeOrderReq = mapper.writeValueAsString(placeOrderReqModelList);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeOrderReq;
	}

	/**
	 * Method to validate place order req
	 * 
	 * @param orderDetails
	 * @return
	 */
	private boolean validateExecuteOrderReq(OrderDetails orderDetails) {

		if (StringUtil.isNotNullOrEmpty(orderDetails.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getTradingSymbol())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getQty())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getPrice())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getProduct())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getTransType())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getPriceType())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getRet())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getToken())) {
			return true;

		}

		return false;

	}

	/**
	 * Method to modify an order
	 * 
	 * @author Nesan
	 *
	 * @param ordersReq
	 * @param userSession
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> modifyOrder(OrderDetails orderReqModel, ClinetInfoModel info) {
		try {
			/** Validate Request **/
			if (!validateModifyOrderReq(orderReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkMwMDAwOCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiOTc2ODY0MTlmOWNlMzc0MSIsIm9jVG9rZW4iOiIweDAxRkMxQzM3QTNEN0EzRERGRTIyRkU5RkRCOTRGRCIsInVzZXJDb2RlIjoiQUNKWVUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NDcxNjE5OSwiaWF0IjoxNjk0NzEwMjY5fQ.ate60HhiPia769m022GWFFKWTPCHIDFTMsQrjr47cZ8";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

//			if (orderReqModel.getOrderType().equalsIgnoreCase("Bracket")) {
//				String req = prepareModifyBracketOrderReq(orderReqModel, info);
//				if (StringUtil.isNullOrEmpty(req))
//					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//
//				/** Execute modify order **/
//				return restService.modifyOrder(req, orderReqModel, userSession, info.getUserId());
//			} else
			if (orderReqModel.getOrderType().equalsIgnoreCase("Cover")) {
				String req = prepareModifyCoverOrderReq(orderReqModel, info);
				if (StringUtil.isNullOrEmpty(req))
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

				/** Execute modify order **/
				return restService.modifyCoverOrder(req, orderReqModel, userSession, info.getUserId());
			} else {
				/** prepare request body */
				String req = prepareModifyOrderReq(orderReqModel, info);
				if (StringUtil.isNullOrEmpty(req))
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

				/** Execute modify order **/
				return restService.modifyOrder(req, orderReqModel, userSession, info.getUserId());
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to modify an order
	 * 
	 * @author Gowthaman M
	 *
	 * @param ordersReq
	 * @param userSession
	 * @param info
	 * @return
	 */
	public String prepareModifyCoverOrderReq(OrderDetails orderReqModel, ClinetInfoModel info) {
		String request = "";
		try {
			ModifyCoverOrderReqModel coReq = new ModifyCoverOrderReqModel();
			CoMainLeg coMainLeg = new CoMainLeg();
			CoStoplossLeg coStoplossLeg = new CoStoplossLeg();
			ObjectMapper mapper = new ObjectMapper();

//			coMainLeg.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderReqModel.getPriceType()));
			coMainLeg.setOrderType(AppConstants.COVER_MARGINPLUS);
			coMainLeg.setPrice(Integer.parseInt(orderReqModel.getPrice()));
			coMainLeg.setQty(Integer.parseInt(orderReqModel.getQty()));
			coMainLeg.setTradedQty(Integer.parseInt(orderReqModel.getFilledQty()));

			coReq.setCoMainLeg(coMainLeg);

			List<CoLeg> listLegs = new ArrayList<>();
			CoLeg leg = new CoLeg();
			leg.setPrice(Integer.parseInt(orderReqModel.getPrice()));
			leg.setQuantity(Integer.parseInt(orderReqModel.getQty()));
			leg.setTriggerPrice(Integer.parseInt(orderReqModel.getTriggerPrice()));
			listLegs.add(leg);
			coStoplossLeg.setLegs(listLegs);

			coReq.setCoStoplossLeg(coStoplossLeg);

			request = mapper.writeValueAsString(coReq);

		} catch (Exception e) {
			Log.error(e);
		}
		return request;
	}

	/**
	 * Method to validate place order req
	 * 
	 * @param orderReqModel
	 * @return
	 */

	private boolean validateModifyOrderReq(OrderDetails orderReqModel) {
		if (StringUtil.isNotNullOrEmpty(orderReqModel.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getOrderNo())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getPriceType())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getQty())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getFilledQty())) {
			return true;
		}
		return false;
	}

	/**
	 * Method to Prepare modify order request for odin
	 * 
	 * @param orderReqModel
	 * @param userSession
	 * @param info
	 * @return
	 */
	private String prepareModifyOrderReq(OrderDetails orderReqModel, ClinetInfoModel info) {
		String request = "";
		try {
			ModifyOrderReqModel model = new ModifyOrderReqModel();
			ObjectMapper mapper = new ObjectMapper();
			// mandatory fields
			model.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderReqModel.getPriceType()));
			model.setQuantity(Integer.parseInt(orderReqModel.getQty()));
			model.setTradedQuantity(Integer.parseInt(orderReqModel.getFilledQty()));

			// non-mandatory fields
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getPrice())) {
				model.setPrice(Float.parseFloat(orderReqModel.getPrice()));
			}

//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
//				model.setTriggerPrice(Integer.parseInt(orderReqModel.getTriggerPrice()));
//			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
				model.setTriggerPrice(Float.parseFloat(orderReqModel.getTriggerPrice()));
			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getDisclosedQty())) {
				model.setDisclosedQuantity(Integer.parseInt(orderReqModel.getDisclosedQty()));
			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
				model.setValidity(orderReqModel.getRet());
			}

			request = mapper.writeValueAsString(model);

		} catch (Exception e) {
			Log.error(e);
		}
		return request;

	}

	/**
	 * Method to cancel multiple orders or single orders
	 * 
	 * @author DINESH KUMAR implemented by Nesan
	 * @param orderDetails
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> cancelOrder(List<OrderDetails> orderDetails, ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDE4Mjk4Q0I0MUZCOEUwNTNDNEM4OEUwMjczM0UyIiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjg5MTAwMTk5LCJpYXQiOjE2ODkwODMwODd9.RFjL8dFEZlx7lmhiFvWy1UUJnQCmaEsfk_JwHydTGYQ";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponseForList();

			List<GenericResponse> response = cancelOrdersSimultaneously(orderDetails, userSession, info);
			return prepareResponse.prepareSuccessRespForList(response);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to execute cancel order simultaneously
	 * 
	 * @author DINESH KUMAR
	 * @ co-@author Implemented by Nesan
	 * @updated Gowthaman M
	 * @param ordersReq
	 * @param userSession
	 * @param info
	 * @return
	 */
	private List<GenericResponse> cancelOrdersSimultaneously(List<OrderDetails> ordersReq, String userSession,
			ClinetInfoModel info) {
		List<Callable<GenericResponse>> tasks = new ArrayList<>();
		for (OrderDetails orderDetails : ordersReq) {
			Callable<GenericResponse> task = () -> {
				/** Validate Request **/
				if (!validateCancelReq(orderDetails))
					return prepareResponse.prepareFailedResponseBody(AppConstants.INVALID_PARAMETER);

				if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
					orderDetails.setExchange(AppConstants.NSE_EQ);
				} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
					orderDetails.setExchange(AppConstants.BSE_EQ);
				} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
					orderDetails.setExchange(AppConstants.NSE_FO);
				} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
					orderDetails.setExchange(AppConstants.NSE_CUR);
				} else {
					return prepareResponse.prepareFailedResponseBody(AppConstants.INVALID_PARAMETER);
				}

//				if (orderDetails.getOrderType().equalsIgnoreCase("Bracket")) {
//
//				} else 
				if (orderDetails.getOrderType().equalsIgnoreCase("Cover")) {
					/** Execute cancel order **/
					return restService.executeCancelCoverOrder(orderDetails, userSession, info.getUserId());
				} else {
					/** Execute cancel order **/
					return restService.executeCancelOrder(orderDetails, userSession, info.getUserId());
				}

			};
			tasks.add(task);
		}
		return tasks.stream().map(callable -> {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Method to validate cancel order request
	 * 
	 * @author Nesan
	 *
	 * @param orderDetails
	 * @return
	 */
	private boolean validateCancelReq(OrderDetails orderDetails) {
		if (StringUtil.isNotNullOrEmpty(orderDetails.getOrderNo())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getOrderType())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Method to execute basket orders
	 * 
	 * @author Dinesh Kumar
	 * @modified author Nesan
	 * @param orderDetails
	 * @param info
	 * @return
	 */
	@Override
	public List<GenericResponse> executeBasketOrder(List<OrderDetails> orderDetails, ClinetInfoModel info) {
		List<GenericResponse> responseList = new ArrayList<>();
		GenericResponse response = new GenericResponse();
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession)) {
				response = prepareResponse.prepareUnauthorizedResponseBody();
				responseList.add(response);
				return responseList;
			}

			List<GenericResponse> genericResponses = executeOrdersSimultaneously(orderDetails, userSession, info);
			return genericResponses;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		response = prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
		responseList.add(response);
		return responseList;
	}

	/**
	 * Method to square off positions
	 * 
	 * @author DINESH KUMAR
	 * @modified author Nesan
	 *
	 * @param orderDetails
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails, ClinetInfoModel info,
			String reqId) {
		try {

			Log.info("Square Off position started");
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkFTRDAwNSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiNGRkNzhlOTdmZmEyZDUyZiIsIm9jVG9rZW4iOiIweDAxOTU4MUQ5OEE0RTc5RTlFNEFDQTJCNEQwNzMyMyIsInVzZXJDb2RlIjoiQUNETFYiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NzI4NTc5OSwiaWF0IjoxNjg3MjY4NTg0fQ.WYj6wDh-n-TcsqAfQpPRpAOCpO_8UzTNadBi8IBodBg";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			if (!validatePositionSquareOffReq(orderDetails))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			String exch = "";
			if (orderDetails.get(0).getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				exch = AppConstants.NSE_EQ;
			} else if (orderDetails.get(0).getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				exch = AppConstants.BSE_EQ;
			} else if (orderDetails.get(0).getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				exch = AppConstants.NSE_FO;
			} else if (orderDetails.get(0).getExchange().equalsIgnoreCase(AppConstants.CDS)) {
				exch = AppConstants.NSE_CUR;
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
			}

			String req = prepareSquareOffReq(orderDetails, info);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			return restService.executePositionSquareOff(req, userSession, reqId, info);
		} catch (Exception e) {
			Log.error(e);
		}
		Log.info("Square Off position Ended");
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate Position SquareOff Request
	 * 
	 * @author Gowthaman
	 * @param orderDetails
	 * @return
	 */
	private boolean validatePositionSquareOffReq(List<OrderDetails> orderDetails) {
		boolean status = false;
		for (OrderDetails orderDetail : orderDetails) {
			if (StringUtil.isNotNullOrEmpty(orderDetail.getExchange())
					&& StringUtil.isNotNullOrEmpty(orderDetail.getToken())
					&& StringUtil.isNotNullOrEmpty(orderDetail.getProduct())
					&& StringUtil.isNotNullOrEmpty(orderDetail.getTransType())
					&& StringUtil.isNotNullOrEmpty(orderDetail.getOrderType())
					&& StringUtil.isNotNullOrEmpty(orderDetail.getQty())) {
				status = true;
			} else {
				status = false;
			}
		}
		return status;
	}

	/**
	 * Method to square off All positions
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionSquareOffAll(ClinetInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkMwMDAwOCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiOTc2ODY0MTlmOWNlMzc0MSIsIm9jVG9rZW4iOiIweDAxNEMxRDQ1MkY5QUUxQTVGQzUwNUM0MDU3NEE4NSIsInVzZXJDb2RlIjoiQUNKWVUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MjY0MjU5OSwiaWF0IjoxNjkyNjA5NjkwfQ.iAmuUhTbf_IlSRA8poDwsEER6Q-sDsZCqOkamF7DxyE";
		RestResponse<GenericResponse> position = restService.getPositionBook(userSession, info);
		String reqId = "";
		String req = prepareSquareOffAllReq(position, info);
		if (StringUtil.isNullOrEmpty(req))
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		return restService.executePositionSquareOff(req, userSession, reqId, info);

	}

	/**
	 * Method to prepare Square Off All Request
	 * 
	 * @author Gowthaman
	 * @param position
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String prepareSquareOffAllReq(RestResponse<GenericResponse> position, ClinetInfoModel info) {

//		List<OrderDetails> orderDetails = new ArrayList<>();

		PlaceOrderReqModel placeOrderReqModel = null;
		List<PlaceOrderReqModel> placeOrderReqModelList = new ArrayList<PlaceOrderReqModel>();
		ObjectMapper mapper = new ObjectMapper();
		String placeOrderReq = null;

		List<PositionResponse> listOfPositions = (List<PositionResponse>) position.getEntity().getResult();

		try {
			for (PositionResponse orderDetail : listOfPositions) {
//			for (OrderDetails orderDetail : orderDetails) {
				placeOrderReqModel = new PlaceOrderReqModel();

				if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_EQ);
				} else if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.BSE_EQ);
				} else if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_FO);
				} else if (orderDetail.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
					placeOrderReqModel.getScripInfo().setExchange(AppConstants.NSE_CUR);
				}

//				placeOrderReqModel.getScripInfo().setSymbol(orderReqModel.getTradingSymbol());
				placeOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderDetail.getToken()));

//				placeOrderReqModel.setTransactionType(orderDetail.getTransType());

				// Swap value start
				placeOrderReqModel
						.setProductType(HazelcastConfig.getInstance().getProductTypes().get(orderDetail.getProduct()));
				placeOrderReqModel.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get("MKT"));

				placeOrderReqModel.setQuantity(orderDetail.getNetQty());

				if (orderDetail.getNetQty().contains("0.0")) {
					placeOrderReqModel.setPrice(Float.parseFloat(orderDetail.getSellPrice()));
				} else {
					placeOrderReqModel.setPrice(Float.parseFloat(orderDetail.getBuyPrice()));

				}

				if (orderDetail.getNetQty().contains("-")) {
					placeOrderReqModel.setTransactionType("BUY");
					String qty = orderDetail.getNetQty().substring(1, orderDetail.getNetQty().length() - 1);
					placeOrderReqModel.setQuantity(qty);
				} else {
					placeOrderReqModel.setTransactionType("SELL");
					placeOrderReqModel.setQuantity(orderDetail.getNetQty());
				}

//				if (StringUtil.isNotNullOrEmpty(orderDetail.getTriggerPrice())) {
//					placeOrderReqModel.setTriggerPrice(Integer.parseInt(orderDetail.getTriggerPrice()));
//				}
//				if (StringUtil.isNotNullOrEmpty(orderDetail.getDisclosedQty())) {
//					placeOrderReqModel.setDisclosedQuantity(Integer.parseInt(orderDetail.getDisclosedQty()));
//				}
//				if (StringUtil.isNotNullOrEmpty(orderDetail.getRet())) {
				placeOrderReqModel.setValidity("DAY");
//				}
				// TODO if(StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
				// placeOrderReqModel.setValidityDays(null);
				// }
				if (StringUtil.isNotNullOrEmpty(orderDetail.getOrderType())
						&& orderDetail.getOrderType() == AppConstants.AMO) {
					placeOrderReqModel.setIsAmo(true);
				}
				placeOrderReqModel.setOrderIdentifier("");

				// TODO placeOrderReqModel.setOrderIdentifier(null); //
				// TODO placeOrderReqModel.setPartCode(null); //
				// placeOrderReqModel.setAlgoId(null);
				// placeOrderReqModel.setStrategyId(null); //
				// TODO placeOrderReqModel.setVenderCode(null);
				placeOrderReqModelList.add(placeOrderReqModel);
			}

			placeOrderReq = mapper.writeValueAsString(placeOrderReqModelList);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeOrderReq;
	}

	/**
	 * Method to get order margin
	 * 
	 * @author DINESH KUMAR
	 * @modified author Nesan
	 *
	 * @param marginReqModel
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> getOrderMargin(MarginReqModel marginReqModel, ClinetInfoModel info) {
		String transType = "";
		try {
			/** Validate Request **/
			if (!validateOrderMarginReq(marginReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			if (marginReqModel.getTransType().equalsIgnoreCase(AppConstants.TRANS_TYPE_BUY)) {
				transType = AppConstants.TRANS_TYPE_BUY;
			} else if (marginReqModel.getTransType().equalsIgnoreCase(AppConstants.TRANS_TYPE_SELL)) {
				transType = AppConstants.TRANS_TYPE_SELL;
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkMwMDAwOCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiOTc2ODY0MTlmOWNlMzc0MSIsIm9jVG9rZW4iOiIweDAxOUMyRkEwQTZCMzk2REQ1ODdBREQ4OEUyQjIxOCIsInVzZXJDb2RlIjoiQUNKWVUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MjI5Njk5OSwiaWF0IjoxNjkyMjczODExfQ.5dlt7w5JmW_bU4EuUSfgAR5cLSq8RJJTE3DwGeaRMxs";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** prepare request body */
			String req = prepareOrderMarginReq(marginReqModel, userSession, info);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Get order margin **/
			return restService.getOrderMargin(req, userSession, info);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate get margin req
	 * 
	 * @author Nesan
	 * 
	 * @param marginReqModel
	 * @return
	 */
	private boolean validateOrderMarginReq(MarginReqModel marginReqModel) {
		if (StringUtil.isNotNullOrEmpty(marginReqModel.getTradingSymbol())
				&& StringUtil.isNotNullOrEmpty(marginReqModel.getQty())
				&& StringUtil.isNotNullOrEmpty(marginReqModel.getPrice())
				&& StringUtil.isNotNullOrEmpty(marginReqModel.getProduct())
				&& StringUtil.isNotNullOrEmpty(marginReqModel.getTransType())
				&& StringUtil.isNotNullOrEmpty(marginReqModel.getPriceType())) {
			return true;
		}

		return false;
	}

	/**
	 * Method to validate get margin req
	 * 
	 * @author Nesan
	 * 
	 * @param marginReqModel
	 * @return
	 */
	private String prepareOrderMarginReq(MarginReqModel marginReqModel, String userSession, ClinetInfoModel info) {
		String request = "";
		try {
			OrderMarginReqModel model = new OrderMarginReqModel();
			ObjectMapper mapper = new ObjectMapper();
			LegDetail legDetail = new LegDetail();
			// mandatory fields

			model.setNoOfLegs(AppConstants.PAYMENT_PRIORITY);
			model.setFETraceId(AppConstants.FE_TRACE_ID);
//			model.setMode(marginReqModel.getOrderFlag().equalsIgnoreCase(AppConstants.NEW_ORDER) ? AppConstants.CHAR_N
//					: AppConstants.CHAR_M);// TODO
			if (marginReqModel.getOrderFlag().equalsIgnoreCase(AppConstants.NEW_ORDER)) {
				model.setMode("N");
			} else {
				model.setMode("M");
			}

			legDetail.setLegNo(AppConstants.PAYMENT_PRIORITY);

			if (marginReqModel.getTransType().equalsIgnoreCase(AppConstants.TRANS_TYPE_BUY)) {
				legDetail.setBuyOrSell(AppConstants.PAYMENT_PRIORITY);
			} else if (marginReqModel.getTransType().equalsIgnoreCase(AppConstants.TRANS_TYPE_SELL)) {
				legDetail.setBuyOrSell(AppConstants.ORDER_PRIORITY);
			}

			legDetail.setMarketSegmentId(AppConstants.PAYMENT_PRIORITY);// TODO
			legDetail.setToken(Integer.parseInt(marginReqModel.getToken()));
			legDetail.setQuantity(Integer.parseInt(marginReqModel.getQty()));
			
//			String price = "";
//			if (marginReqModel.getPrice().contains(".")) {
//				price = marginReqModel.getPrice().replace(".", "");
//				legDetail.setPrice(Float.parseFloat(price));
//			} else {
//				legDetail.setPrice(Float.parseFloat(marginReqModel.getPrice()));
//			}
			
			String price = marginReqModel.getPrice();
			float floatValue = Float.parseFloat(price);
			float priceValue = floatValue *= 100;
			legDetail.setPrice(priceValue);
			
			legDetail.setMktFlag(AppConstants.PAYMENT_PRIORITY);// TODO

			if (marginReqModel.getProduct().equalsIgnoreCase(AppConstants.PRODUCT_MIS)) {
				legDetail.setProductType(AppConstants.STR_M);
			} else if (marginReqModel.getProduct().equalsIgnoreCase(AppConstants.PRODUCT_CNC)) {
				legDetail.setProductType(AppConstants.STR_D);
			} else if (marginReqModel.getProduct().equalsIgnoreCase(AppConstants.PRODUCT_MTF)) {
				legDetail.setProductType(AppConstants.STR_MF);
			} else if (marginReqModel.getProduct().equalsIgnoreCase(AppConstants.PRODUCT_NRML)) {
				legDetail.setProductType(AppConstants.STR_PT);
			} else {
				legDetail.setProductType(AppConstants.STR_D);
			}
			model.getLegDetails().add(legDetail);

			request = mapper.writeValueAsString(model);
		} catch (Exception e) {
			Log.error(e);
		}
		return request;
	}

}

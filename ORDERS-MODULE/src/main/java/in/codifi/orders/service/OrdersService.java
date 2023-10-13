package in.codifi.orders.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.OrdersServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.OrdersRestService;
import in.codifi.orders.ws.model.PlaceOrderReqModel;
import in.codifi.orders.ws.service.OrdersRestServiceOdin;
import in.codifi.ws.model.odin.ModifyOrderReqModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OrdersService implements OrdersServiceSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	OrdersRestService ordersRestService;

	@Inject
	OrdersRestServiceOdin odinRestservice;

	@Inject
	RestServiceProperties restProp;

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Method to place order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> placeOrder(OrderReqModel orderReqModel, ClinetInfoModel info) {
		try {

			/** Validate Request **/
			if (!validatePlaceOrderReq(orderReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			/** prepare request body */
			String reqModel = preparePlaceOrderReq(orderReqModel);
//			String req = preparePlaceOrderReq(orderReqModel, userSession, info);
			if (reqModel == null)
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Execute Place Order **/
			RestResponse<GenericResponse> orderResponse = ordersRestService.placeOrder(reqModel, userSession,
					orderReqModel.getReqId(),info.getUserId());
			if (orderResponse != null)
				return orderResponse;

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private String preparePlaceOrderReq(OrderReqModel orderReqModel) {
		JsonObject placeOrderVal = null;
		String placeOrderVaasl = null;
//		PlaceOrderReqModelJson placeOrderReqModelJson = new PlaceOrderReqModelJson();
//
//		placeOrderReqModelJson.getPlaceOrderReqModel().getScripInfo().setExchange(orderReqModel.getExchange());
//		placeOrderReqModelJson.getPlaceOrderReqModel().getScripInfo().setSymbol(orderReqModel.getTradingSymbol());
//
//		placeOrderReqModelJson.getPlaceOrderReqModel().setTransactionType(orderReqModel.getTranType());
//		placeOrderReqModelJson.getPlaceOrderReqModel().setProductType(orderReqModel.getProduct());
//		placeOrderReqModelJson.getPlaceOrderReqModel().setOrderType(orderReqModel.getPriceType());
//		placeOrderReqModelJson.getPlaceOrderReqModel().setQuantity(Integer.parseInt(orderReqModel.getQty()));
//		placeOrderReqModelJson.getPlaceOrderReqModel().setPrice(Float.parseFloat(orderReqModel.getPrice()));
//		if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
//			placeOrderReqModelJson.getPlaceOrderReqModel().setTriggerPrice(null);
//		}
//		if (StringUtil.isNotNullOrEmpty(orderReqModel.getDisclosedQty())) {
//			placeOrderReqModelJson.getPlaceOrderReqModel().setDisclosedQuantity(null);
//		}
//		if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
//			placeOrderReqModelJson.getPlaceOrderReqModel().setValidity(orderReqModel.getRet());
//		}
////TODO			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
////			placeOrderReqModel.setValidityDays(null);
////		}
//		if (StringUtil.isNotNullOrEmpty(orderReqModel.getAmo()) && orderReqModel.getAmo() == AppConstants.TRUE) {
//			placeOrderReqModelJson.getPlaceOrderReqModel().setIsAmo(true);
//		}
//
////TODO			placeOrderReqModel.setOrderIdentifier(null);
////		placeOrderReqModel.setPartCode(null);
////		placeOrderReqModel.setAlgoId(null);
////		placeOrderReqModel.setStrategyId(null);
////		placeOrderReqModel.setVenderCode(null);
//
//		return placeOrderReqModelJson;

		PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();

		try {

			placeOrderReqModel.getScripInfo().setExchange(orderReqModel.getExchange());
//			placeOrderReqModel.getScripInfo().setSymbol(orderReqModel.getTradingSymbol());
			placeOrderReqModel.getScripInfo().setScripToken(Integer.parseInt(orderReqModel.getTradingSymbol()));

			placeOrderReqModel.setTransactionType(orderReqModel.getTranType());

			// Swap value start
			placeOrderReqModel
					.setProductType(HazelcastConfig.getInstance().getProductTypes().get(orderReqModel.getProduct()));
			placeOrderReqModel
					.setOrderType(HazelcastConfig.getInstance().getPriceTypes().get(orderReqModel.getPriceType()));

			placeOrderReqModel.setQuantity(orderReqModel.getQty());
//			placeOrderReqModel.setPrice(orderReqModel.getPrice());
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
//				placeOrderReqModel.setTriggerPrice(null);
			}
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getDisclosedQty())) {
				placeOrderReqModel.setDisclosedQuantity(Integer.parseInt(orderReqModel.getDisclosedQty()));
			}
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
				placeOrderReqModel.setValidity(orderReqModel.getRet());
			}
			// TODO if(StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
			// placeOrderReqModel.setValidityDays(null);
			// }
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getAmo()) && orderReqModel.getAmo() == AppConstants.TRUE) {
				placeOrderReqModel.setIsAmo(true);
			}

			// TODO placeOrderReqModel.setOrderIdentifier(null); //
			placeOrderReqModel.setPartCode(null); // placeOrderReqModel.setAlgoId(null);
			// placeOrderReqModel.setStrategyId(null); //
			placeOrderReqModel.setVenderCode(null);

			placeOrderVaasl = mapper.writeValueAsString(placeOrderReqModel);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeOrderVaasl;

	}

	/**
	 * Method to validate place order request
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	private boolean validatePlaceOrderReq(OrderReqModel orderReqModel) {
		if (StringUtil.isNotNullOrEmpty(orderReqModel.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getTradingSymbol())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getQty())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getPrice())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getProduct())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getTranType())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getPriceType())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
			return true;

		}

		return false;
	}

	/**
	 * Method to prepare request to execute place order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @param session
	 * @return
	 */
	private String preparePlaceOrderReq(OrderReqModel orderReqModel, String session, ClinetInfoModel info) {
		String request = "";
		try {
			PlaceOrderReqModel model = new PlaceOrderReqModel();
			ObjectMapper mapper = new ObjectMapper();
			// mandatory fields
//			model.setUid(info.getUserId());
//			model.setActid(info.getUserId());
//			model.setExch(orderReqModel.getExchange());
//			model.setTsym(orderReqModel.getTradingSymbol());
//			model.setQty(orderReqModel.getQty());
//			model.setPrc(orderReqModel.getPrice());
//			model.setPrd(orderReqModel.getProduct());
//			model.setTrantype(orderReqModel.getTranType());
//			model.setPrctyp(orderReqModel.getPriceType());
//			model.setRet(orderReqModel.getRet());
//
//			// non - mandatory filed
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
//				model.setTrgprc(orderReqModel.getTriggerPrice());
//			}
//
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getDisclosedQty())) {
//				model.setDscqty(orderReqModel.getDisclosedQty());
//			}
//
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getMktProtection())) {
//				model.setMktProtection(orderReqModel.getMktProtection());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRemarks())) {
//				model.setRemarks(orderReqModel.getRemarks());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getOrderSource())) {
//				model.setOrdersource(orderReqModel.getOrderSource());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getBookProfitPrice())) {
//				model.setBpprc(orderReqModel.getBookProfitPrice());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getBookLossPrice())) {
//				model.setBlprc(orderReqModel.getBookLossPrice());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTrailingPrice())) {
//				model.setTrailprc(orderReqModel.getTrailingPrice());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getAmo())) {
//				model.setAmo(orderReqModel.getAmo());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTsym2Leg())) {
//				model.setTsym2(orderReqModel.getTsym2Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTranType2Leg())) {
//				model.setTrantype2(orderReqModel.getTranType2Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getQty2Leg())) {
//				model.setQty2(orderReqModel.getQty2Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getPrice2Leg())) {
//				model.setPrc2(orderReqModel.getPrice2Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTsym3Leg())) {
//				model.setTsym3(orderReqModel.getTsym3Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTranType3Leg())) {
//				model.setTrantype3(orderReqModel.getTranType3Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getQty3Leg())) {
//				model.setQty3(orderReqModel.getQty3Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getPrice3Leg())) {
//				model.setPrc3(orderReqModel.getPrice3Leg());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getAlgoId())) {
//				model.setAlgoId(orderReqModel.getAlgoId());
//			}
//			if (StringUtil.isNotNullOrEmpty(orderReqModel.getNaicCode())) {
//				model.setNaicCode(orderReqModel.getNaicCode());
//			}

			String json = mapper.writeValueAsString(model);
			request = AppConstants.JDATA + AppConstants.SYMBOL_EQUAL + json + AppConstants.SYMBOL_AND
					+ AppConstants.JKEY + AppConstants.SYMBOL_EQUAL + session;
		} catch (Exception e) {
			Log.error(e);
		}
		return request;
	}

	/**
	 * Method to modify an order in odin
	 * 
	 * @author Nesan
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> modifyOrder(OrderReqModel orderReqModel, ClinetInfoModel info) {
		try {
			/** Validate Request **/
			if (!validateModifyOrderReq(orderReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Verify session **/
//			String userSession = restProp.getAccessToken();
			String userSession = AppUtil.getUserSession("APITEST");
//			String userSession = AppUtil.getUserSession(info.getUserId());
//			if (StringUtil.isNullOrEmpty(userSession))
//				return prepareResponse.prepareUnauthorizedResponse();

			/** prepare request body */
			String req = prepareModifyOrderReq(orderReqModel, info);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Execute modify order **/
//			return ordersRestService.modifyOrder(req, orderReqModel, userSession,info.getUserId());

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate place order req
	 * 
	 * @param orderReqModel
	 * @return
	 */

	private boolean validateModifyOrderReq(OrderReqModel orderReqModel) {
		if (StringUtil.isNotNullOrEmpty(orderReqModel.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getOrderNo())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getPriceType())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getQty())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getTradedQty())) {
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
	private String prepareModifyOrderReq(OrderReqModel orderReqModel, ClinetInfoModel info) {
		String request = "";
		try {
			ModifyOrderReqModel model = new ModifyOrderReqModel();
			ObjectMapper mapper = new ObjectMapper();
			// mandatory fields
			model.setOrderType(orderReqModel.getPriceType());
			model.setQuantity(Integer.parseInt(orderReqModel.getQty()));
			model.setTradedQuantity(Integer.parseInt(orderReqModel.getTradedQty()));

			// non-mandatory fields
			if (StringUtil.isNotNullOrEmpty(orderReqModel.getPrice())) {
				model.setPrice(Float.parseFloat(orderReqModel.getPrice()));
			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getTriggerPrice())) {
				model.setTriggerPrice(Integer.parseInt(orderReqModel.getTriggerPrice()));
			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getDisclosedQty())) {
				model.setDisclosedQuantity(Integer.parseInt(orderReqModel.getDisclosedQty()));
			}

			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) { // TODO confirm RET is the validity
				model.setValidity(orderReqModel.getRet());
			}
			/*
			 * Number of days order needs to be carried ahead for validity GTD orders -
			 * logic need to be incorporated
			 */
//TODO			if (StringUtil.isNotNullOrEmpty(orderReqModel.getRet())) {
//				model.setValidityDays(null);
//			}

			request = mapper.writeValueAsString(model);

		} catch (Exception e) {
			Log.error(e);
		}
		return request;

	}

	/**
	 * Method to cancel order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> cancelOrder(OrderReqModel orderReqModel, ClinetInfoModel info) {
		try {

			/** Validate Request **/
			if (!validateCancelReq(orderReqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Verify session **/
//			String userSession = restProp.getAccessToken();
			String userSession = AppUtil.getUserSession("APITEST");
//			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Execute cancel order **/
			return ordersRestService.cancelOrder(orderReqModel, userSession,info.getUserId());
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate cancel ordere request in Odin
	 * 
	 * @author Nesan
	 * @param orderReqModel
	 * @return
	 */
	private boolean validateCancelReq(OrderReqModel orderReqModel) {
		if (StringUtil.isNotNullOrEmpty(orderReqModel.getOrderNo())
				&& StringUtil.isNotNullOrEmpty(orderReqModel.getExchange())) {
			return true;
		}
		return false;
	}

}

package in.codifi.position.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.position.config.HazelcastConfig;
import in.codifi.position.config.RestServiceProperties;
import in.codifi.position.model.request.OrderDetails;
import in.codifi.position.model.request.PositionConversionReq;
import in.codifi.position.model.request.UserSession;
import in.codifi.position.model.response.GenericResponse;
import in.codifi.position.service.spec.IPositionService;
import in.codifi.position.utility.AppConstants;
import in.codifi.position.utility.AppUtil;
import in.codifi.position.utility.PrepareResponse;
import in.codifi.position.utility.StringUtil;
import in.codifi.position.ws.model.PlaceOrderReqModel;
import in.codifi.position.ws.model.RestConversionReq;
import in.codifi.position.ws.service.PositionRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PositionService implements IPositionService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	PositionRestService positionRestService;
	@Inject
	RestServiceProperties props;

	/**
	 * Get position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getposition(ClinetInfoModel info) {
		try {
			String type = "all";
			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDE1RDFGOEFFREVDMENGRDkxQkMyRTAxRkNFNTUzIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODc3Njk5OSwiaWF0IjoxNjk4NzUxMjU3fQ.JYrO8M2bnLLs27sRVWqwX_XfIRkpObThBOnEJl6IbsM";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return positionRestService.getpositionBook(userSession, info.getUserId(), type);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Get position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDailyPosition(ClinetInfoModel info) {
		try {
			String type = "daily";

			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return positionRestService.getpositionBook(userSession, info.getUserId(), type);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Get position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getExpiryPosition(ClinetInfoModel info) {
		try {
			String type = "expiry";

			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return positionRestService.getpositionBook(userSession, info.getUserId(), type);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * position conversion
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionConversion(PositionConversionReq model, ClinetInfoModel info) {
		try {
			/** Validate Request **/
			if (!validatePositionConversionReq(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFFQ0IxQjJEMzg5QjM2QjA0Q0M4M0VDRUZDODE5IiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjg5NjE4NTk5LCJpYXQiOjE2ODk1NzIwNDl9.l-KaI7MRu0WwaR_Necs3Iw2u_d071pX6QtSCMPyebYw";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** prepare request body */
			String req = preparePositionConversionReq(model, userSession, info);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			return positionRestService.positionConversionOdin(userSession, req, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate position conversion request
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private boolean validatePositionConversionReq(PositionConversionReq model) {
		if (StringUtil.isNotNullOrEmpty(model.getExchange()) && StringUtil.isNotNullOrEmpty(model.getToken())
				&& StringUtil.isNotNullOrEmpty(model.getQty()) && StringUtil.isNotNullOrEmpty(model.getTransType())
				&& StringUtil.isNotNullOrEmpty(model.getPrevProduct())
				&& StringUtil.isNotNullOrEmpty(model.getProduct())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	private String preparePositionConversionReq(PositionConversionReq model, String session, ClinetInfoModel info) {
		String request = "";
		RestConversionReq reqModel = new RestConversionReq();
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (model.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				reqModel.setExch(AppConstants.NSE_EQ);
			} else if (model.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				reqModel.setExch(AppConstants.BSE_EQ);
			} else if (model.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				reqModel.setExch(AppConstants.NSE_FO);
			} else if (model.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
				reqModel.setExch(AppConstants.NSE_CUR);
			}
			reqModel.setScripToken(Integer.parseInt(model.getToken()));
			if (model.getTransType().equalsIgnoreCase("B")) {
				reqModel.setTransType("BUY");
			} else {
				reqModel.setTransType("SELL");
			}
			reqModel.setQty(Integer.parseInt(model.getQty()));
			System.out.println(
					"old pro-- " + HazelcastConfig.getInstance().getProductTypes().get(model.getPrevProduct()));
			reqModel.setOldPrdType(HazelcastConfig.getInstance().getProductTypes().get(model.getPrevProduct()));
			reqModel.setNewPrdType(HazelcastConfig.getInstance().getProductTypes().get(model.getProduct()));
			reqModel.setBoOrderId("");

			request = mapper.writeValueAsString(reqModel);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return request;
	}

	/**
	 * Method to user session in cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> putUserSession(UserSession session) {
		try {
			HazelcastConfig.getInstance().getRestUserSession().clear();
			String hzUserSessionKey = "APITEST" + AppConstants.HAZEL_KEY_REST_SESSION;
			HazelcastConfig.getInstance().getRestUserSession().put(hzUserSessionKey, session.getSession());
			String userSession = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
			return prepareResponse.prepareSuccessResponseObject(userSession);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
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
	@Override
	public RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails, ClinetInfoModel info) {
		try {
			Log.info("Square Off position started");
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkFTRDAwNSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiNGRkNzhlOTdmZmEyZDUyZiIsIm9jVG9rZW4iOiIweDAxOTU4MUQ5OEE0RTc5RTlFNEFDQTJCNEQwNzMyMyIsInVzZXJDb2RlIjoiQUNETFYiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NzI4NTc5OSwiaWF0IjoxNjg3MjY4NTg0fQ.WYj6wDh-n-TcsqAfQpPRpAOCpO_8UzTNadBi8IBodBg";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			if (!validatePositionSquareOffReq(orderDetails))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			String req = prepareSquareOffReq(orderDetails, info);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			return positionRestService.executePositionSquareOff(req, userSession, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		Log.info("Square Off position Ended");
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

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

				if (orderDetail.getExchange().equalsIgnoreCase("NSE")) {
					placeOrderReqModel.getScripInfo().setExchange("NSE_EQ");
				} else if (orderDetail.getExchange().equalsIgnoreCase("BSE")) {
					placeOrderReqModel.getScripInfo().setExchange("BSE_EQ");
				} else if (orderDetail.getExchange().equalsIgnoreCase("NFO")) {
					placeOrderReqModel.getScripInfo().setExchange("NSE_FO");
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
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return placeOrderReq;
	}

}

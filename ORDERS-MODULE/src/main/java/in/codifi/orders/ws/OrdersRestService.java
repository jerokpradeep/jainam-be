package in.codifi.orders.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
//import java.net.HttpURLConnection;
//import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.entity.logs.RestAccessLogModel;
import in.codifi.orders.model.request.OrderDetails;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.model.response.PositionResponse;
import in.codifi.orders.model.transformation.GenericOrderBookResp;
import in.codifi.orders.model.transformation.GenericOrderMariginRespModel;
import in.codifi.orders.model.transformation.GenericOrderResp;
import in.codifi.orders.model.transformation.GenericPositionSqrOffResp;
import in.codifi.orders.model.transformation.GenericTradeBookResp;
import in.codifi.orders.model.transformation.GtdOrderBookResp;
import in.codifi.orders.model.transformation.OrderHisRespModel;
import in.codifi.orders.model.transformation.PositionsRemodeling;
import in.codifi.orders.reposirory.AccessLogManager;
import in.codifi.orders.utility.AccessLogHelper;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.CodifiUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.model.GtdOrderBookRespData;
import in.codifi.orders.ws.model.GtdOrderBookResponse;
import in.codifi.orders.ws.model.OrderBookSuccess;
import in.codifi.orders.ws.model.OrderHistoryRespData;
import in.codifi.orders.ws.model.OrderHistoryRespModel;
import in.codifi.orders.ws.model.PositionRespModel;
import in.codifi.orders.ws.model.RestPositionFailResp;
import in.codifi.orders.ws.model.TradeBookSuccess;
import in.codifi.ws.model.odin.MarginRespModel;
import in.codifi.ws.model.odin.OrderBookRespModel;
import in.codifi.ws.model.odin.PlaceOrderRespModel;
import in.codifi.ws.model.odin.SquareOffResModel;
import in.codifi.ws.model.odin.TradeBookRespModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OrdersRestService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;
	@Inject
	PositionsRemodeling positionsRemodeling;
	@Inject
	AccessLogHelper logHelper;

//	String reqId, tpUri, method, reqBody, resBody, contentType;
//	Timestamp inTime;

	/**
	 * 
	 * Method to execute place order
	 * 
	 * @author nesan
	 *
	 * @param request
	 * @return
	 */
	public RestResponse<GenericResponse> placeOrder(String request, String session, String requestId, String userId) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		PlaceOrderRespModel placeOrderRespModel = new PlaceOrderRespModel();
		Log.info("Place Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {

			accessLogModel.setMethod("placeOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getPlaceOrderUrl());
			accessLogModel.setUrl(url.toString());
//			tpUri = url.toString();
//			method = AppConstants.POST_METHOD;
//			reqBody = request;
//			inTime = new Timestamp(System.currentTimeMillis());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(placeOrderRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(placeOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage())
										? placeOrderRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in place Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(placeOrderRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			Log.error(e);
		}
//		finally {
//			try {
//				logHelper.saveTpLogs(Thread.currentThread().getName(), tpUri, inTime, method, reqBody, output,
//						AppConstants.APPLICATION_JSON);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to execute place order
	 * 
	 * @author Dinesh Kumar Modified by Nesan
	 * @param request
	 * @return
	 */
	public GenericResponse executePlaceOrder(String request, String session, String requestId, String userId) {
//		reqId = requestId;
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		PlaceOrderRespModel placeOrderRespModel = new PlaceOrderRespModel();
		Log.info("Place Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("executePlaceOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getPlaceOrderUrl());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponseBody();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(placeOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
					} else {
						return prepareResponse
								.prepareFailedResponseBody(StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage())
										? placeOrderRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in place Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind place order data from ODIN service to Generic format
	 * 
	 * The same method is also used for Modify order
	 * 
	 * @author Nesan
	 * @param placeOrderRespModel
	 * @return
	 */
	private GenericOrderResp bindPlaceOrderData(PlaceOrderRespModel placeOrderRespModel) {
		GenericOrderResp genericOrderResp = new GenericOrderResp();
		try {
			genericOrderResp.setOrderNo(placeOrderRespModel.getData().getOrderId());
			genericOrderResp.setRequestTime("");// TODO still time is not comming from odin
		} catch (Exception e) {
			Log.error(e);
		}
		// TODO Auto-generated method stub
		return genericOrderResp;
	}

	/**
	 * Method to modify an order in Odin
	 * 
	 * @author Nesan
	 * @param userSession
	 * 
	 * @param req
	 * @return
	 */
	public RestResponse<GenericResponse> modifyOrder(String request, OrderDetails reqModel, String userSession,
			String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		PlaceOrderRespModel modifyOrderRespModel = new PlaceOrderRespModel();
		Log.info("Modify Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("modifyOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String exch = "";
			if (reqModel.getExchange().equalsIgnoreCase("NSE")) {
				exch = "NSE_EQ";
			} else if (reqModel.getExchange().equalsIgnoreCase("BSE")) {
				exch = "BSE_EQ";
			} else if (reqModel.getExchange().equalsIgnoreCase("NFO")) {
				exch = "NSE_FO";
			} else if (reqModel.getExchange().equalsIgnoreCase("CDS")) {
				exch = "NSE_CUR";
			} else if (reqModel.getExchange().equalsIgnoreCase("MCX")) {
				exch = "MCX_FO";
			} else if (reqModel.getExchange().equalsIgnoreCase("BCD")) {
				exch = "BSE_CUR";
			}

//			URL url = new URL(props.getModifyOrderurl() + AppConstants.OPERATOR_SLASH + exch
//					+ AppConstants.OPERATOR_SLASH + eqModel.getOrderNo(), AppConstants.UTF_8);
//			accessLogModel.setUrl(url.toString());
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod(AppConstants.PUT_METHOD);
//			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
//			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
//			conn.setDoOutput(true);
//			try (OutputStream os = conn.getOutputStream()) {
//				byte[] input = request.getBytes(AppConstants.UTF_8);
//				os.write(input, 0, input.length);
//			}
//			int responseCode = conn.getResponseCode();
			URL url = new URL(props.getModifyOrderurl() + AppConstants.OPERATOR_SLASH + exch
					+ AppConstants.OPERATOR_SLASH + URLEncoder.encode(reqModel.getOrderNo(), AppConstants.UTF_8));
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			System.out.println("Method " + AppConstants.PUT_METHOD);
			System.out.println("Authorization " + "Bearer " + userSession);
			System.out.println("x-api-key " + props.getXApiKey());
			System.out.println("request -- " + request.toString());
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			System.out.println("Modify order responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;

			if (responseCode == 401) {
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(modifyOrderRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}

			} else {
				System.out.println("Error Connection in modify Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("modify order output -- " + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(modifyOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get order book
	 * 
	 * @author Gowthaman M
	 * @param req
	 * @return
	 **/
	public RestResponse<GenericResponse> getOrderBookInfo(String request, String userSession, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		OrderBookRespModel orderBookRespModel = new OrderBookRespModel();
		String output = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("orderbook");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String urlRequest = AppConstants.QUESTION_MARK + AppConstants.OFFSET + AppConstants.SYMBOL_EQUAL
					+ AppConstants.ONE + AppConstants.SYMBOL_AND + AppConstants.LIMIT + AppConstants.SYMBOL_EQUAL
					+ AppConstants.LIMIT_1000 + AppConstants.SYMBOL_AND + AppConstants.ORDER_STATUS
					+ AppConstants.SYMBOL_EQUAL + AppConstants.NEGATIVE_ONE;
			URL url = new URL(props.getOrderBookUrl() + urlRequest);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			System.out.println(
					"orderbook " + AppConstants.AUTHORIZATION + "  " + AppConstants.BEARER_WITH_SPACE + userSession);
			System.out.println("orderbook " + AppConstants.X_API_KEY_NAME + "  " + props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					orderBookRespModel = mapper.readValue(output, OrderBookRespModel.class);
					/** Bind the response to generic response **/
					if (orderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						System.out.println(orderBookRespModel.getData());
						List<GenericOrderBookResp> extract = bindOrderBookData(orderBookRespModel.getData(),
								info.getUserId());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (orderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(orderBookRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(orderBookRespModel.getMessage())
										? orderBookRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in Order Book api. Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					orderBookRespModel = mapper.readValue(output, OrderBookRespModel.class);
					if (StringUtil.isNotNullOrEmpty(orderBookRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(orderBookRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("OrderBook -- " + e.getMessage());
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to map Generic OrderBook Response with Odin core API values response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<GenericOrderBookResp> bindOrderBookData(List<OrderBookSuccess> success, String userId) {
		List<GenericOrderBookResp> responseList = new ArrayList<>();
		try {
			for (OrderBookSuccess model : success) {
				GenericOrderBookResp extract = new GenericOrderBookResp();
				String restExch = model.getExch();
				String exch = "";
				ObjectMapper mapper = new ObjectMapper();
				System.out.println("model -- "+mapper.writeValueAsString(model));
				if (restExch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
					exch = AppConstants.NSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
					exch = AppConstants.BSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_FO)) {
					exch = AppConstants.NFO;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
					exch = AppConstants.CDS;
				} else if (restExch.equalsIgnoreCase(AppConstants.MCX_FO)) {
					exch = AppConstants.MCX;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_CUR)) {
					exch = AppConstants.BCD;
				}

				String token = model.getScripToken();
				ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
				if (coModel != null) {
					String scripName = StringUtil.isNotNullOrEmpty(coModel.getFormattedInsName())
							? coModel.getFormattedInsName()
							: "";
					extract.setFormattedInsName(scripName);
				}

				extract.setOrderNo(model.getOrderId());
				extract.setUserId(userId);
				extract.setActId(userId);
				extract.setExchange(exch);
				extract.setCompanyName(coModel.getCompanyName());
				extract.setTradingSymbol(model.getSymbol());
				extract.setQty(model.getTotalQty());
				extract.setTransType(model.getTransType());
				extract.setRet(model.getValidity());
				extract.setToken(model.getScripToken());
				extract.setLotSize(model.getMktLot());
//				extract.setTickSize(model.getTi()); //TODO
				extract.setPrice(model.getOrderPrice());
//				extract.setRPrice(model.getRprc()); //TODO
//				extract.setAvgTradePrice(model.getAvgprc()); //TODO
				extract.setDisclosedQty(model.getDisclosedQty());
				extract.setOrderStatus(model.getStatus());
				extract.setFillShares(model.getTradedQty());
				extract.setExchUpdateTime(model.getExchTime());
				extract.setExchOrderId(model.getExchOrderNo()); // TODO
//				extract.setRQty(model.getRqty()); //TODO
				extract.setRejectedReason(model.getErrorReason());
				extract.setTriggerPrice(model.getTriggerPrice());
//				extract.setMktProtection(model.getMktProtection()); //TODO
//				extract.setTarget(model.getBlPrc()); //TODO
//				extract.setStopLoss(model.getBpPrc()); //TODO
//				extract.setTrailingPrice(model.getTrailPrc()); //TODO
				extract.setOrderTime(model.getOrderTime());
				boolean isAmo = model.isAmo();
				if (StringUtil.isNotNullOrEmpty(model.getProductType())) {
					extract.setProduct(HazelcastConfig.getInstance().getProductTypes().get(model.getProductType()));
				}
				if (StringUtil.isNotNullOrEmpty(model.getOrderType())) {
					extract.setPriceType(HazelcastConfig.getInstance().getPriceTypes().get(model.getOrderType()));
				}
				if (isAmo) {
					extract.setOrderType(AppConstants.AMO);
				} else {
					extract.setOrderType(model.getProductType());
				}
				responseList.add(extract);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return responseList;
	}

	/**
	 * Method to get order book
	 * 
	 * @author Gowthaman M
	 * @param req
	 * @return
	 **/
	public RestResponse<GenericResponse> getTradeBookInfo(String userSession, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		TradeBookRespModel orderBookRespModel = new TradeBookRespModel();
		String output = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("tradebook");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String urlRequest = AppConstants.QUESTION_MARK + AppConstants.OFFSET + AppConstants.SYMBOL_EQUAL
					+ AppConstants.ONE + AppConstants.SYMBOL_AND + AppConstants.LIMIT + AppConstants.SYMBOL_EQUAL
					+ AppConstants.HUNDRED;

			URL url = new URL(props.getTradeBookUrl() + urlRequest);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("tradebook ResponseCode--- " + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					orderBookRespModel = mapper.readValue(output, TradeBookRespModel.class);
					/** Bind the response to generic response **/
					if (orderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<GenericTradeBookResp> extract = bindTradeBookData(orderBookRespModel, info.getUserId());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (orderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(orderBookRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(orderBookRespModel.getMessage())
										? orderBookRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in Trade Book api. Rsponse code - " + responseCode);

				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					orderBookRespModel = mapper.readValue(output, TradeBookRespModel.class);
					if (StringUtil.isNotNullOrEmpty(orderBookRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(orderBookRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private List<GenericTradeBookResp> bindTradeBookData(TradeBookRespModel datas, String userId) {
		List<TradeBookSuccess> success = datas.getData();
		List<GenericTradeBookResp> responseList = new ArrayList<>();
		try {
			for (TradeBookSuccess model : success) {
				GenericTradeBookResp extract = new GenericTradeBookResp();
				String token = model.getScripToken();
				String exch = "";
				String restExch = model.getExch();

				if (restExch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
					exch = AppConstants.NSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
					exch = AppConstants.BSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_FO)) {
					exch = AppConstants.NFO;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
					exch = AppConstants.CDS;
				} else if (restExch.equalsIgnoreCase(AppConstants.MCX_FO)) {
					exch = AppConstants.MCX;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_CUR)) {
					exch = AppConstants.BCD;
				}
				ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
				if (coModel != null) {
					String scripName = StringUtil.isNotNullOrEmpty(coModel.getFormattedInsName())
							? coModel.getFormattedInsName()
							: "";
					extract.setFormattedInsName(scripName);
				}

				extract.setOrderNo(model.getOrderId());
				extract.setUserId(userId);
				extract.setActId(userId);
				extract.setExchange(exch);
				extract.setCompanyName(coModel.getCompanyName());
//				extract.setRet(model.getRet());//TODO
//				extract.setFillId(model.getFlid());//TODO
//				extract.setFillTime(model.getFltm());//TODO
				extract.setTransType(model.getTransType());
				extract.setTradingSymbol(model.getSymbol());
				extract.setQty(model.getTradeQty());
				extract.setToken(model.getScripToken());
//				extract.setFillshares(model.getTradeQty());
				extract.setFillQty(model.getTradeQty());
//				extract.setPricePrecision(model.getPp());//TODO
//				extract.setLotSize(model.getLs());//TODO
//				extract.setTickSize(model.getTi());//TODO
				extract.setPrice(model.getTradePrice());
//				extract.setPrcftr(model.getPrcftr());//TODO
//				extract.setFillprc(model.getFlprc());//TODO
//				extract.setExchUpdateTime(model.getExchTm());//TODO
				extract.setExchOrderId(model.getExchOrderNo());
				extract.setOrderTime(model.getTradeTime());

				extract.setProduct(HazelcastConfig.getInstance().getProductTypes().get(model.getProductType()));
				extract.setPriceType(HazelcastConfig.getInstance().getPriceTypes().get(model.getOrderType()));
				responseList.add(extract);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return responseList;
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param accessLogModel
	 */
	public void insertRestAccessLogs(RestAccessLogModel accessLogModel) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					accessLogManager.insertRestAccessLog(accessLogModel);
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	/*
	 * Method to cancel an order in odin
	 * 
	 * @author Nesan
	 * 
	 * @param orderReqModel
	 * 
	 * @return
	 */
	public RestResponse<GenericResponse> cancelOrder(OrderReqModel reqModel, String userSession, String userId) {
		ObjectMapper mapper = new ObjectMapper();
//		reqId = reqModel.getReqId();
		String output = null;
		PlaceOrderRespModel modifyOrderRespModel = new PlaceOrderRespModel();
		Log.info("Modify Order Request - " + reqModel.getOrderNo() + " " + reqModel.getExchange());
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {

			accessLogModel.setMethod("executePlaceOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody("");
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getCancelOrderUrl() + AppConstants.OPERATOR_SLASH + reqModel.getExchange()
					+ AppConstants.OPERATOR_SLASH + reqModel.getOrderNo());
			accessLogModel.setUrl(url.toString());
//			tpUri = url.toString();
//			method = AppConstants.DELETE_METHOD;
//			reqBody = null;
//			inTime = new Timestamp(System.currentTimeMillis());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.DELETE_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, AppConstants.X_API_KEY);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());

			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(modifyOrderRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}

			} else {
				System.out.println("Error Connection in cancel Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(modifyOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
				}

			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to cancel an order in odin
	 * 
	 * @author Nesan
	 * 
	 * @param OrderDetails
	 * 
	 * @return
	 */
	public GenericResponse executeCancelOrder(OrderDetails reqModel, String userSession, String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		PlaceOrderRespModel modifyOrderRespModel = new PlaceOrderRespModel();
		Log.info("Cancel Order Request - " + reqModel.getOrderNo() + " " + reqModel.getExchange());
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("executeCancelOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getCancelOrderUrl() + AppConstants.OPERATOR_SLASH + reqModel.getExchange()
					+ AppConstants.OPERATOR_SLASH + reqModel.getOrderNo());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.DELETE_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponseBody();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(modifyOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(modifyOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
					}
				}

			} else {
				System.out.println("Error Connection in cancel Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(modifyOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(modifyOrderRespModel.getMessage());
				}

			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
	}

	public RestResponse<GenericResponse> executePositionSquareOff(String request, String userSession, String reqId,
			ClinetInfoModel info) {
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		SquareOffResModel squareOffResModel = new SquareOffResModel();
		Log.info("Position square off - " + request);
		try {

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getSquareOffPostionUrl());

			accessLogModel.setMethod("executePositionSquareOff");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setUrl(url.toString());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(request);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401 || responseCode == 404) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					squareOffResModel = mapper.readValue(output, SquareOffResModel.class);
					/** Bind the response to generic response **/
					if (squareOffResModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
//						List<GenericResponse> extract = bindSquareOffData(squareOffResModel);
						List<GenericPositionSqrOffResp> extract = bindSquareOffData(squareOffResModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (squareOffResModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponse(squareOffResModel.getMessageObj().getMessage());
					} else {
						return prepareResponse.prepareFailedResponse(
								StringUtil.isNotNullOrEmpty(squareOffResModel.getMessageObj().getMessage())
										? squareOffResModel.getMessageObj().getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in position square off api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					squareOffResModel = mapper.readValue(output, SquareOffResModel.class);
					if (StringUtil.isNotNullOrEmpty(squareOffResModel.getMessageObj().getMessage()))
						return prepareResponse.prepareFailedResponse(squareOffResModel.getMessageObj().getMessage());
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			Log.error(e);
			logHelper.saveErrorLog(Thread.currentThread().getName(),
					Thread.currentThread().getStackTrace()[1].getClassName(),
					Thread.currentThread().getStackTrace()[1].getMethodName(), e.toString());
		} finally {
			try {
				insertRestAccessLogs(accessLogModel);
			} catch (Exception e) {
				Log.error(e);
			}

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare generic response from Odin square off response
	 * 
	 * @author Nesan
	 * @param squareOffResModel
	 * @return
	 */
	private List<GenericPositionSqrOffResp> bindSquareOffData(SquareOffResModel squareOffResModel) {
		List<GenericPositionSqrOffResp> genericPositionSqrOffRespList = new ArrayList<>();
//		List<GenericResponse> sentResponse = new ArrayList<GenericResponse>();
		try {
			for (PlaceOrderRespModel model : squareOffResModel.getSquareOffResModel()) {
				GenericPositionSqrOffResp genericPositionSqrOffResp = new GenericPositionSqrOffResp();

				if (model.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
					genericPositionSqrOffResp.setMessage("");
					genericPositionSqrOffResp.setOrderNo(model.getData().getOrderId());
					genericPositionSqrOffResp.setRequestTime("");// TODO still time is not comming from odin
					genericPositionSqrOffRespList.add(genericPositionSqrOffResp);
				} else {
					genericPositionSqrOffResp.setMessage(model.getMessage());
					genericPositionSqrOffResp.setRequestTime("");// TODO still time is not comming from odin
					genericPositionSqrOffRespList.add(genericPositionSqrOffResp);
				}

//				GenericResponse extract = prepareResponse.prepareSuccessResponseBody(genericOrderResp);
//				sentResponse.add(extract);
//				genericOrderResp = new GenericOrderResp();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("bind Square Off Data -- " + e.getMessage());
		} finally {

		}
		return genericPositionSqrOffRespList;
	}

	/**
	 * Method to get Margin from Odin service
	 * 
	 * @author Nesan
	 * @param req
	 * @return
	 */
	public RestResponse<GenericResponse> getOrderMargin(String request, String userSession, ClinetInfoModel info) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		MarginRespModel marginRespModel = new MarginRespModel();
		Log.info("order margin req - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getMarginUrl());

			accessLogModel.setMethod("getOrderMargin");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(request);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("OrderMargin responseCode--" + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				System.out.println("output--" + output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					marginRespModel = mapper.readValue(output, MarginRespModel.class);
					/** Bind the response to generic response **/
					if (marginRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)
							&& marginRespModel.getData().getStatus() == 200) {
						GenericOrderMariginRespModel extract = bindMarginData(marginRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (marginRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareSuccessResponseObject(marginRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(marginRespModel.getMessage()) ? marginRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in  Order  margin api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					marginRespModel = mapper.readValue(output, MarginRespModel.class);
					if (StringUtil.isNotNullOrEmpty(marginRespModel.getMessage()))
						return prepareResponse.prepareFailedResponse(marginRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			Log.error(e);
			logHelper.saveErrorLog(Thread.currentThread().getName(),
					Thread.currentThread().getStackTrace()[1].getClassName(),
					Thread.currentThread().getStackTrace()[1].getMethodName(), e.toString());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method bind ODIN margin response to Generic response
	 * 
	 * @author Nesan
	 * @param marginRespModel
	 * @return
	 */
	private GenericOrderMariginRespModel bindMarginData(MarginRespModel marginRespModel) {
		GenericOrderMariginRespModel respModel = new GenericOrderMariginRespModel();

		if (StringUtil.isNotNullOrEmpty(marginRespModel.getData().getResult().getApproxMargin())) {
			respModel.setRequiredMargin(marginRespModel.getData().getResult().getApproxMargin());
		} else {
			respModel.setRequiredMargin("0.00");
		}
		if (StringUtil.isNotNullOrEmpty(marginRespModel.getData().getResult().getShortFall())) {
			respModel.setMarginShortfall(marginRespModel.getData().getResult().getShortFall());
		} else {
			respModel.setMarginShortfall("0.00");
		}
		if (StringUtil.isNotNullOrEmpty(marginRespModel.getData().getResult().getAvailableMargin())) {
			respModel.setAvailableMargin(marginRespModel.getData().getResult().getAvailableMargin());
		} else {
			respModel.setAvailableMargin("0.00");
		}

		if (StringUtil.isNotNullOrEmpty(marginRespModel.getData().getResult().getBrokerage())) {
			respModel.setBrokerage(marginRespModel.getData().getResult().getBrokerage());
		} else {
			respModel.setBrokerage("0.00");
		}

		return respModel;

	}

	/**
	 * Method to get Order History
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public RestResponse<GenericResponse> getOrderHistory(OrderReqModel req, String userSession, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		OrderHistoryRespModel orderHistoryResp = new OrderHistoryRespModel();
		String output = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("orderHistory");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
//			String urlRequest = props.getOrderHistoryUrl() + req.getOrderNo();
			String urlRequest = props.getOrderHistoryUrl() + URLEncoder.encode(req.getOrderNo(), AppConstants.UTF_8);
			URL url = new URL(urlRequest);
			System.out.println("orderHisUrl -- " + url);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("orderHis responseCode--- " + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					orderHistoryResp = mapper.readValue(output, OrderHistoryRespModel.class);
					if (orderHistoryResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)
							&& !orderHistoryResp.getData().contains("[]")) {
						List<OrderHisRespModel> extract = bindOrderHistoryData(orderHistoryResp, info.getUserId());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else {
						return prepareResponse.prepareSuccessMessage(AppConstants.NO_RECORD_FOUND);
					}
				}
			} else {

				System.out.println("Error Connection in Order History api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					orderHistoryResp = mapper.readValue(output, OrderHistoryRespModel.class);
					if (StringUtil.isNotNullOrEmpty(orderHistoryResp.getMessage()))
						return prepareResponse.prepareFailedResponse(orderHistoryResp.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind order history
	 * 
	 * @author Gowthaman
	 * @param orderHistoryResp
	 * @param userId
	 * @return
	 */
	public List<OrderHisRespModel> bindOrderHistoryData(OrderHistoryRespModel orderHistoryResp, String userId) {
		List<OrderHisRespModel> response = new ArrayList<>();

		for (OrderHistoryRespData rSet : orderHistoryResp.getData()) {
			OrderHisRespModel result = new OrderHisRespModel();
			String token = rSet.getScripToken();
			String exch = "";
			String restExch = rSet.getExchange();

			if (restExch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
				exch = AppConstants.NSE;
			} else if (restExch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
				exch = AppConstants.BSE;
			} else if (restExch.equalsIgnoreCase(AppConstants.NSE_FO)) {
				exch = AppConstants.NFO;
			} else if (restExch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
				exch = AppConstants.CDS;
			} else if (restExch.equalsIgnoreCase(AppConstants.MCX_FO)) {
				exch = AppConstants.MCX;
			} else if (restExch.equalsIgnoreCase(AppConstants.BSE_CUR)) {
				exch = AppConstants.BCD;
			}
			ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
			if (coModel != null) {
				if (StringUtil.isNotNullOrEmpty(coModel.getCompanyName())) {
					result.setCompanyName(coModel.getCompanyName());
				}
			}
			result.setActId("");
			result.setAvgPrice("");
			result.setDisclosedQty("");
//			result.setExchange(rSet.getExchange());
			result.setExchange(exch);
			result.setExchOrderNo(rSet.getExcOrderNo());
			result.setExchTime(rSet.getExchangeTimestamp());
			result.setFillshares(rSet.getPendingQty());
			result.setLotSize("");
			result.setOrderNo(rSet.getOrderId());
			result.setOrderType(rSet.getOrderType());
			result.setPrice(rSet.getOrderPrice());
			result.setPricePrecision("");
			result.setPriceType("");
			result.setProduct(rSet.getProductType());
			result.setQuantity(rSet.getTotalQty());
			result.setRemarks("");
			result.setReport("");
			result.setRet("");
			result.setStatus(rSet.getStatus());
			result.setTickSize("");
			result.setTime(rSet.getOrderTimestamp());
			result.setToken("");
			result.setTradingSymbol(rSet.getSymbol());
			result.setTransType(rSet.getTransType());
			result.setUserId(userId);
			response.add(result);
		}

		return response;
	}

	/**
	 * Method to place Cover order in ODIN
	 * 
	 * @author Nesan
	 * 
	 * @param req
	 * @param userSession
	 * @param userId
	 * @return
	 */
	public GenericResponse executeCoverOrder(String request, String session, String userId) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		PlaceOrderRespModel placeOrderRespModel = new PlaceOrderRespModel();
		Log.info("Cover Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("executeCoverOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getCoverOrderUrl());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponseBody();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(placeOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
					} else {
						return prepareResponse
								.prepareFailedResponseBody(StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage())
										? placeOrderRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in place Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to Execute Bracket Order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public GenericResponse executeBracketOrder(String request, String userSession, String userId) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		PlaceOrderRespModel placeOrderRespModel = new PlaceOrderRespModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			Log.info("Bracket Order Request - " + request);
			accessLogModel.setMethod("bracketCoverOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(mapper.writeValueAsString(request));
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getBracketOrderUrl());
			System.out.println("url--" + url);
			System.out.println("request--" + request);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponseBody();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(placeOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (placeOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
					} else {
						return prepareResponse
								.prepareFailedResponseBody(StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage())
										? placeOrderRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in Bracket Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					placeOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(placeOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(placeOrderRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to modify cover order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> modifyCoverOrder(String request, OrderDetails reqModel, String userSession,
			String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		PlaceOrderRespModel modifyOrderRespModel = new PlaceOrderRespModel();
		Log.info("Modify Cover Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("modifyCoverOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String exch = "";
			if (reqModel.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				exch = AppConstants.NSE_EQ;
			} else if (reqModel.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				exch = AppConstants.BSE_EQ;
			} else if (reqModel.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				exch = AppConstants.NSE_FO;
			} else if (reqModel.getExchange().equalsIgnoreCase(AppConstants.CDS)) {
				exch = AppConstants.NSE_CUR;
			} else if (reqModel.getExchange().equalsIgnoreCase(AppConstants.MCX)) {
				exch = AppConstants.MCX_FO;
			} else if (reqModel.getExchange().equalsIgnoreCase(AppConstants.BCD)) {
				exch = AppConstants.BSE_CUR;
			}
			URL url = new URL(
					props.getModifyCoverOrderurl() + exch + AppConstants.OPERATOR_SLASH + reqModel.getOrderNo());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;

			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(modifyOrderRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}

			} else {
				System.out.println("Error Connection in modify cover Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(modifyOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(modifyOrderRespModel.getMessage());
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to execute Cancel Cove rOrder
	 * 
	 * @author Gowthaman M
	 * @param reqModel
	 * @param userSession
	 * @param userId
	 * @return
	 */
	public GenericResponse executeCancelCoverOrder(OrderDetails reqModel, String userSession, String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		PlaceOrderRespModel modifyOrderRespModel = new PlaceOrderRespModel();
		Log.info("Cancel Order Request - " + reqModel.getOrderNo() + " " + reqModel.getExchange());
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("executeCancelCoverOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getCancelOrderUrl() + AppConstants.OPERATOR_SLASH + reqModel.getExchange()
					+ AppConstants.OPERATOR_SLASH + reqModel.getOrderNo());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.DELETE_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponseBody();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					/** Bind the response to generic response **/
					if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						GenericOrderResp extract = bindPlaceOrderData(modifyOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (modifyOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(modifyOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);
					}
				}

			} else {

				System.out.println("Error Connection in cancel cover Order api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					modifyOrderRespModel = mapper.readValue(output, PlaceOrderRespModel.class);
					if (StringUtil.isNotNullOrEmpty(modifyOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(modifyOrderRespModel.getMessage());
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}

	/**
	 * Method to get Brokerage Calculation
	 * 
	 * @author Gowthaman M
	 * @param pUserSession
	 * @param info
	 * @param encodedUrl
	 * @return
	 */
	@SuppressWarnings("unused")
	public RestResponse<GenericResponse> getBrokerageCalculation(ClinetInfoModel pInfo, String pEncodedUrl,
			String pUserSession) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("getBrokerageCalculation");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(pInfo.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getBrokerageAndChargesPage() + AppConstants.BROKERAGE_QUERY_PARAMS + pEncodedUrl);
			System.out.println("url -- " + url);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Brokerage calculation ResponseCode -- " + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				StringBuilder htmlContent = new StringBuilder();
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					htmlContent.append(line);
				}
				System.out.println("HTML Content -- " + htmlContent);
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(htmlContent.toString())) {
					return prepareResponse.prepareSuccessResponseObject(htmlContent.toString());
				}

			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output -- " + output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error("getBrokerageCalculation" + e.getMessage());
			e.printStackTrace();
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to get OrderMarginInfo Brokerage
	 * 
	 * @param orderMarginInfoReq
	 * @param userSession
	 * @return
	 */
	public String getOrderMarginInfoBrokerage(String request, String pUserSession, ClinetInfoModel pInfo) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		MarginRespModel marginRespModel = new MarginRespModel();
		Log.info("order margin req - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getMarginUrl());

			accessLogModel.setMethod("getOrderMargin");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(pInfo.getUserId());
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(request);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + pUserSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			System.out.println("OrderMargin responseCode--" + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return AppConstants.UNAUTHORIZED;
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				System.out.println("output--" + output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					marginRespModel = mapper.readValue(output, MarginRespModel.class);
					/** Bind the response to generic response **/
					if (marginRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)
							&& marginRespModel.getData().getStatus() == 200) {
						GenericOrderMariginRespModel extract = bindMarginData(marginRespModel);
						return extract.getBrokerage();
					} else if (marginRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return marginRespModel.getMessage();
					} else {
						return AppConstants.FAILED_STATUS;
					}
				}
			} else {
				System.out.println("Error Connection in  Order  margin api. Rsponse code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					marginRespModel = mapper.readValue(output, MarginRespModel.class);
					if (StringUtil.isNotNullOrEmpty(marginRespModel.getMessage()))
						return marginRespModel.getMessage();
				} else {
					return AppConstants.FAILED_STATUS;
				}

			}
		} catch (Exception e) {
			Log.error(e);
			logHelper.saveErrorLog(Thread.currentThread().getName(),
					Thread.currentThread().getStackTrace()[1].getClassName(),
					Thread.currentThread().getStackTrace()[1].getMethodName(), e.toString());
		}
		return AppConstants.FAILED_STATUS;
	}

	public RestResponse<GenericResponse> getPositionBook(String pUserSession, ClinetInfoModel info) {
		String type = "expiry";
		PositionRespModel positionRespModel = new PositionRespModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("ODIN position request" + pUserSession);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("getpositionBookForSqrOff");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String urlreq = type + AppConstants.QUESTION_MARK + AppConstants.INTROPSTATUS + AppConstants.SYMBOL_EQUAL
					+ AppConstants.ONE;

			URL url = new URL(props.getPositionUrl() + urlreq);
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setUrl(props.getPositionUrl() + urlreq);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + pUserSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in ODIN position api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("ODIN position response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					positionRespModel = mapper.readValue(output, PositionRespModel.class);
					/** Bind the response to generic response **/
					if (positionRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<PositionResponse> extract = positionsRemodeling
								.bindPostitionResponseData(positionRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (positionRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(positionRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(positionRespModel.getMessage())
										? positionRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				} else {
					return prepareResponse.prepareFailedResponseForRestService(
							StringUtil.isNotNullOrEmpty(positionRespModel.getMessage()) ? positionRespModel.getMessage()
									: AppConstants.FAILED_STATUS);
				}
			} else {
				System.out.println("Error Connection ODIN position api. Rsponse code - " + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					RestPositionFailResp fail = mapper.readValue(output, RestPositionFailResp.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						System.out.println("Error Connection in position api. Rsponse code -" + fail.getEmsg());
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		accessLogModel.setResBody("Failed");
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Gtd order book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public RestResponse<GenericResponse> getGtdOrderBookInfo(String pUserSession, ClinetInfoModel pInfo) {
		GtdOrderBookResponse gtdOrderBookRespModel = new GtdOrderBookResponse();
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		String output = null;
		try {
			accessLogModel.setMethod("getGtdOrderBookInfo");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setUserId(pInfo.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			String urlRequest = AppConstants.QUESTION_MARK + AppConstants.OFFSET + AppConstants.SYMBOL_EQUAL
					+ AppConstants.ONE + AppConstants.SYMBOL_AND + AppConstants.LIMIT + AppConstants.SYMBOL_EQUAL
					+ AppConstants.LIMIT_1000 + AppConstants.SYMBOL_AND + AppConstants.ORDER_STATUS
					+ AppConstants.SYMBOL_EQUAL + AppConstants.NEGATIVE_ONE;
			URL url = new URL(props.getGtdOrderbookUrl() + urlRequest);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + pUserSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("get Gtd Order Book Info responseCode -- " + responseCode);
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				System.out.println("output -- " + output);
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					gtdOrderBookRespModel = mapper.readValue(output, GtdOrderBookResponse.class);
					/** Bind the response to generic response **/
					if (gtdOrderBookRespModel.getData().contains("[]")
							&& gtdOrderBookRespModel.getMessage().equalsIgnoreCase("Orders fetched successfully")) {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
					} else if (gtdOrderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<GtdOrderBookResp> extract = bindGtdOrderBookData(gtdOrderBookRespModel.getData(),
								pInfo.getUserId());
						return prepareResponse.prepareSuccessResponseObject(extract);
//						return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
					} else if (gtdOrderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(gtdOrderBookRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(gtdOrderBookRespModel.getMessage())
										? gtdOrderBookRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in GTD Order Book api. Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				if (StringUtil.isNotNullOrEmpty(output)) {
					gtdOrderBookRespModel = mapper.readValue(output, GtdOrderBookResponse.class);
					if (StringUtil.isNotNullOrEmpty(gtdOrderBookRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(gtdOrderBookRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("get GTD Order Book Info -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to Bind Gtd Order Book Data
	 * 
	 * @author Gowthaman
	 * @param data
	 * @param userId
	 * @return
	 */
	public List<GtdOrderBookResp> bindGtdOrderBookData(List<GtdOrderBookRespData> data, String userId) {
		List<GtdOrderBookResp> responseList = new ArrayList<>();
		try {
			for (GtdOrderBookRespData model : data) {
				GtdOrderBookResp extract = new GtdOrderBookResp();
				String restExch = model.getExchange();
				String exch = "";

				if (restExch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
					exch = AppConstants.NSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
					exch = AppConstants.BSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_FO)) {
					exch = AppConstants.NFO;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
					exch = AppConstants.CDS;
				} else if (restExch.equalsIgnoreCase(AppConstants.MCX_FO)) {
					exch = AppConstants.MCX;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_CUR)) {
					exch = AppConstants.BCD;
				}

				String token = model.getScripToken().toString();
				ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
				if (coModel != null) {
					String scripName = StringUtil.isNotNullOrEmpty(coModel.getFormattedInsName())
							? coModel.getFormattedInsName()
							: "";
					extract.setFormattedInsName(scripName);
				}

				extract.setOrderNo(model.getOrderId());
				extract.setUserId(userId);
				extract.setActId(userId);
				extract.setExchange(exch);
				extract.setCompanyName(coModel.getCompanyName());
				extract.setTradingSymbol(model.getSymbol());
				extract.setQty(model.getTotalQty().toString());
				extract.setTransType(model.getTranType());
				extract.setRet(model.getValidity());
				extract.setToken(model.getScripToken().toString());
				extract.setLotSize(model.getMktLot().toString());
//				extract.setTickSize(model.getTi()); //TODO
				extract.setPrice(model.getOrderPrice());
//				extract.setRPrice(model.getRprc()); //TODO
//				extract.setAvgTradePrice(model.getAvgprc()); //TODO
				extract.setDisclosedQty(model.getDisclosedQty().toString());
				extract.setOrderStatus(model.getStatus());
				extract.setFillShares(model.getTradedQty().toString());
				extract.setExchUpdateTime("");
				extract.setExchOrderId(model.getExchOrderNo()); // TODO
//				extract.setRQty(model.getRqty()); //TODO
				extract.setRejectedReason(model.getErrorReason());
				extract.setTriggerPrice(model.getTriggerPrice());
//				extract.setMktProtection(model.getMktProtection()); //TODO
//				extract.setTarget(model.getBlPrc()); //TODO
//				extract.setStopLoss(model.getBpPrc()); //TODO
//				extract.setTrailingPrice(model.getTrailPrc()); //TODO
				extract.setOrderTime(model.getOrder_timestamp());
				boolean isAmo = model.getIsAmoOrder();
				if (StringUtil.isNotNullOrEmpty(model.getProductType())) {
					extract.setProduct(HazelcastConfig.getInstance().getProductTypes().get(model.getProductType()));
				}
				if (StringUtil.isNotNullOrEmpty(model.getOrderType())) {
					extract.setPriceType(HazelcastConfig.getInstance().getPriceTypes().get(model.getOrderType()));
				}
				if (isAmo) {
					extract.setOrderType(AppConstants.AMO);
				} else {
					extract.setOrderType(model.getProductType());
				}
				responseList.add(extract);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return responseList;
	}

}

package in.codifi.orders.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
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
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.entity.logs.RestAccessLogModel;
import in.codifi.orders.model.request.SipOrderDetails;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.model.transformation.SipOrderBookResponse;
import in.codifi.orders.model.transformation.SipOrderResponse;
import in.codifi.orders.reposirory.AccessLogManager;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.CodifiUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.model.SipCancelOrderRestResp;
import in.codifi.orders.ws.model.SipOrderBookRespData;
import in.codifi.orders.ws.model.SipOrderBookRestResp;
import in.codifi.orders.ws.model.SipOrderRestResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SipOrderRestServiceOdin {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;

	public GenericResponse executeSipOrder(String request, String session, String userId) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		SipOrderRestResp sipPlaceOrderRespModel = new SipOrderRestResp();
		Log.info("Sip Place Order Request - " + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("executeSipPlaceOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getPlaceSipOrderUrl());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
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
			System.out.println("sip order responseCode -- " + responseCode);
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
					sipPlaceOrderRespModel = mapper.readValue(output, SipOrderRestResp.class);
					/** Bind the response to generic response **/
					if (sipPlaceOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						SipOrderResponse extract = bindSipPlaceOrderData(sipPlaceOrderRespModel);
						return prepareResponse.prepareSuccessResponseBody(extract);
					} else if (sipPlaceOrderRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseBody(sipPlaceOrderRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseBody(
								StringUtil.isNotNullOrEmpty(sipPlaceOrderRespModel.getMessage())
										? sipPlaceOrderRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in place sip Order api. Rsponse code - " + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					sipPlaceOrderRespModel = mapper.readValue(output, SipOrderRestResp.class);
					if (StringUtil.isNotNullOrEmpty(sipPlaceOrderRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseBody(sipPlaceOrderRespModel.getMessage());
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
	 * Method to bind sip place order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public SipOrderResponse bindSipPlaceOrderData(SipOrderRestResp sipPlaceOrderRespModel) {
		SipOrderResponse response = new SipOrderResponse();
		response.setMessage(sipPlaceOrderRespModel.getMessage());
		return response;
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

	/**
	 * Method to bind sip place order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> executeSipOrderBook(String session,
			ClinetInfoModel info) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		SipOrderBookRestResp sipOrderBookRespModel = new SipOrderBookRestResp();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
//		String request = props.getSipOrderBookUrl() + "?offset=" + sipOrderBookReq.getOffset() + "&limit="
//				+ sipOrderBookReq.getLimit() + "&orderStatus=" + sipOrderBookReq.getOrderStatus();
		String request = props.getSipOrderBookUrl() + props.getSipOrderBookRequest();
		Log.info("Sip Place Order Request - " + request);
		try {
			accessLogModel.setMethod("executeSipOrderBook");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(request);
			accessLogModel.setUrl(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("sip order responseCode -- " + responseCode);
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
					sipOrderBookRespModel = mapper.readValue(output, SipOrderBookRestResp.class);
					/** Bind the response to generic response **/
					if (sipOrderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<SipOrderBookResponse> extract = bindSipOrderBookData(sipOrderBookRespModel);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (sipOrderBookRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponse(sipOrderBookRespModel.getMessage());
					} else {
						return prepareResponse
								.prepareFailedResponse(StringUtil.isNotNullOrEmpty(sipOrderBookRespModel.getMessage())
										? sipOrderBookRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in place sip Order book api. Rsponse code - " + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains("<!DOCTYPE")) {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					sipOrderBookRespModel = mapper.readValue(output, SipOrderBookRestResp.class);
					if (StringUtil.isNotNullOrEmpty(sipOrderBookRespModel.getMessage()))
						return prepareResponse.prepareFailedResponse(sipOrderBookRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind sip order book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<SipOrderBookResponse> bindSipOrderBookData(SipOrderBookRestResp sipPlaceOrderRespModel) {
		List<SipOrderBookResponse> response = new ArrayList<>();
		try {
			for (SipOrderBookRespData rSet : sipPlaceOrderRespModel.getData()) {
				SipOrderBookResponse resp = new SipOrderBookResponse();

				String createdDate = rSet.getCreatedDate().substring(0, 10);
				String installmentDate = rSet.getInstallmentDate().substring(0, 10);
				String exchange = "";
				if (rSet.getExchange().equalsIgnoreCase(AppConstants.NSE_EQ)) {
					exchange = AppConstants.NSE;
				} else if (rSet.getExchange().equalsIgnoreCase(AppConstants.BSE_EQ)) {
					exchange = AppConstants.BSE;
				} else if (rSet.getExchange().equalsIgnoreCase(AppConstants.NSE_FO)) {
					exchange = AppConstants.NFO;
				}

				resp.setAssestType(rSet.getAssestType());
				resp.setBDetail(rSet.getBDetail());
				resp.setBModify(rSet.getBModify());
				resp.setBStop(rSet.getBStop());
				resp.setBView(rSet.getBView());
				resp.setCaaRemark(rSet.getCaaRemark());
				resp.setCapPrice(rSet.getCapPrice());
				resp.setCreatedBy(rSet.getCreatedBy());
				resp.setCreatedDate(createdDate);
				resp.setDisplayTransType(rSet.getDisplayTransType());
				resp.setExchange(exchange);
				resp.setExecutedInstallments(rSet.getExecutedInstallments());
				resp.setFrequencyDetails(rSet.getFrequencyDetails());
				resp.setFrequencyStartDate(rSet.getFrequencyStartDate());
				resp.setFrequencyType(rSet.getFrequencyType());
				resp.setInstallmentDate(installmentDate);
				resp.setInvestedQtyOrVal(rSet.getInvestedQtyOrVal());
				resp.setInvestmentType(rSet.getInvestmentType());
				resp.setInvestmentValue(rSet.getInvestmentValue());
				resp.setModifiedBy(rSet.getModifiedBy());
				resp.setNoOfInstallments(rSet.getNoOfInstallments());
				resp.setSeries(rSet.getSeries());
				resp.setSipId(rSet.getSipId());
				resp.setStatus(rSet.getStatus());
				resp.setSymbol(rSet.getSymbol());
				resp.setToken(rSet.getToken());
				resp.setTradedQty(rSet.getTradedQty());
				resp.setTransTtype(rSet.getTransTtype());
				resp.setUpdatedStatus(rSet.getUpdatedStatus());
				response.add(resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return response;
	}

	/**
	 * Method to bind sip place order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> cancelSipOrder(SipOrderDetails sipOrderBookReq, String session,
			ClinetInfoModel info) {
		String output = null;
		ObjectMapper mapper = new ObjectMapper();
		SipCancelOrderRestResp cancelSipOrderResp = new SipCancelOrderRestResp();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		String request = props.getCancleSipOrderUrl() + sipOrderBookReq.getSipId() + "/" + sipOrderBookReq.getStatus()
				+ "/" + sipOrderBookReq.getSymbol() + "/" + sipOrderBookReq.getTransType() + "/"
				+ sipOrderBookReq.getInvestmentType() + "/" + sipOrderBookReq.getQtyOrValue()
				+ "?product_source=WAVEAPI";
		Log.info("Cancel Sip Order Request - " + request);
		try {
			accessLogModel.setMethod("cancelSipOrder");
			accessLogModel.setModule(AppConstants.MODULE_ORDER);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			accessLogModel.setUrl(request);
			CodifiUtil.trustedManagement();
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("cancel sip order responseCode -- " + responseCode);
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
					cancelSipOrderResp = mapper.readValue(output, SipCancelOrderRestResp.class);
					/** Bind the response to generic response **/
					if (cancelSipOrderResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse.prepareSuccessResponseObject(cancelSipOrderResp.getMessage());
					} else if (cancelSipOrderResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponse(cancelSipOrderResp.getMessage());
					} else {
						return prepareResponse
								.prepareFailedResponse(StringUtil.isNotNullOrEmpty(cancelSipOrderResp.getMessage())
										? cancelSipOrderResp.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in cancel sip Order book api. Rsponse code - " + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				System.out.println("output -- " + output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains("<!DOCTYPE")) {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println(output);
					cancelSipOrderResp = mapper.readValue(output, SipCancelOrderRestResp.class);
					if (StringUtil.isNotNullOrEmpty(cancelSipOrderResp.getMessage()))
						return prepareResponse.prepareFailedResponse(cancelSipOrderResp.getMessage());
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind sip order book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
//	public List<SipOrderBookResponse> bindSipOrderBookData(SipOrderBookRestResp sipPlaceOrderRespModel) {
//		List<SipOrderBookResponse> response = new ArrayList<>();
//		try {
//			for (SipOrderBookRespData rSet : sipPlaceOrderRespModel.getData()) {
//				SipOrderBookResponse resp = new SipOrderBookResponse();
//				resp.setAssestType(rSet.getAssestType());
//				resp.setBDetail(rSet.getBDetail());
//				resp.setBModify(rSet.getBModify());
//				resp.setBStop(rSet.getBStop());
//				resp.setBView(rSet.getBView());
//				resp.setCaaRemark(rSet.getCaaRemark());
//				resp.setCapPrice(rSet.getCapPrice());
//				resp.setCreatedBy(rSet.getCreatedBy());
//				resp.setCreatedDate(rSet.getCreatedDate());
//				resp.setDisplayTransType(rSet.getDisplayTransType());
//				resp.setExchange(rSet.getExchange());
//				resp.setExecutedInstallments(rSet.getExecutedInstallments());
//				resp.setFrequencyDetails(rSet.getFrequencyDetails());
//				resp.setFrequencyStartDate(rSet.getFrequencyStartDate());
//				resp.setFrequencyType(rSet.getFrequencyType());
//				resp.setInstallmentDate(rSet.getInstallmentDate());
//				resp.setInvestedQtyOrVal(rSet.getInvestedQtyOrVal());
//				resp.setInvestmentType(rSet.getInvestmentType());
//				resp.setInvestmentValue(rSet.getInvestmentValue());
//				resp.setModifiedBy(rSet.getModifiedBy());
//				resp.setNoOfInstallments(rSet.getNoOfInstallments());
//				resp.setSeries(rSet.getSeries());
//				resp.setSipId(rSet.getSipId());
//				resp.setStatus(rSet.getStatus());
//				resp.setSymbol(rSet.getSymbol());
//				resp.setToken(rSet.getToken());
//				resp.setTradedQty(rSet.getTradedQty());
//				resp.setTransTtype(rSet.getTransTtype());
//				resp.setUpdatedStatus(rSet.getUpdatedStatus());
//				response.add(resp);
//
//			}
//		} catch (Exception e) {
//			Log.error(e);
//		}
//
//		return response;
//	}

}

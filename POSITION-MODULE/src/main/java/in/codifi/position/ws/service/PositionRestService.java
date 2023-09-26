package in.codifi.position.ws.service;

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
import in.codifi.position.config.RestServiceProperties;
import in.codifi.position.entity.logs.RestAccessLogModel;
import in.codifi.position.model.response.GenericOrderResp;
import in.codifi.position.model.response.GenericResponse;
import in.codifi.position.model.response.PositionResponse;
import in.codifi.position.repository.AccessLogManager;
import in.codifi.position.utility.AppConstants;
import in.codifi.position.utility.CodifiUtil;
import in.codifi.position.utility.PrepareResponse;
import in.codifi.position.utility.StringUtil;
import in.codifi.position.ws.model.PositionConRespModel;
import in.codifi.position.ws.model.PositionRespModel;
import in.codifi.position.ws.model.RestPositionFailResp;
import in.codifi.position.ws.remodeling.PlaceOrderRespModel;
import in.codifi.position.ws.remodeling.PositionsRemodeling;
import in.codifi.position.ws.remodeling.SquareOffResModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PositionRestService {

	@Inject
	RestServiceProperties props;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	PositionsRemodeling positionsRemodeling;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * positionMTM form odin api
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getpositionBook(String userSession, String userId, String type) {
		PositionRespModel positionRespModel = new PositionRespModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("ODIN position request" + userSession);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("getpositionBook");
			accessLogModel.setModule(AppConstants.MODULE_POSITIONS);
			accessLogModel.setUserId(userId);
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
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in ODIN position api");
				accessLogModel.setResBody("Unauthorized");
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
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		accessLogModel.setResBody("Failed");
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * position conversion form odin api
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> positionConversionOdin(String userSession, String request, String userId) {
		PositionConRespModel positionConRespModel = new PositionConRespModel();
		Log.info("ODIN position Conversion request " + request);
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			accessLogModel.setMethod("positionConversionOdin");
			accessLogModel.setModule(AppConstants.MODULE_POSITIONS);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getConversionUrl());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("ODIN position response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("position conversion outout -- "+output);
					positionConRespModel = mapper.readValue(output, PositionConRespModel.class);
					if (positionConRespModel != null && StringUtil.isNotNullOrEmpty(positionConRespModel.getStatus())) {
						return positionConRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)
								? prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY)
								: prepareResponse.prepareFailedResponseForRestService("error Response");
					}
				} else {
					return prepareResponse.prepareFailedResponseForRestService(
							StringUtil.isNotNullOrEmpty(positionConRespModel.getMessage())
									? positionConRespModel.getMessage()
									: AppConstants.FAILED_STATUS);
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.startsWith("<!D")) {
						return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
					}
					RestPositionFailResp fail = mapper.readValue(output, RestPositionFailResp.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						System.out.println(
								"Error Connection in position conversion api. Rsponse code -" + fail.getEmsg());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		accessLogModel.setResBody("Failed");
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gowthaman M
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
					e.printStackTrace();
					Log.error(e.getMessage());
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * Method to Square Off position
	 * 
	 * @author Dinesh
	 * @modified author Nesan
	 * @param orderDetails
	 * @return
	 */
	public RestResponse<GenericResponse> executePositionSquareOff(String request, String userSession,
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
			accessLogModel.setModule(AppConstants.MODULE_POSITIONS);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, AppConstants.X_API_KEY);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(request);
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			if (responseCode == 401 || responseCode == 404) {
				accessLogModel.setResBody("Unauthorized");
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					squareOffResModel = mapper.readValue(output, SquareOffResModel.class);
					/** Bind the response to generic response **/
					if (squareOffResModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<GenericResponse> extract = bindSquareOffData(squareOffResModel);
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
			e.printStackTrace();
			Log.error(e.getMessage());
		} finally {
			try {
				insertRestAccessLogs(accessLogModel);
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
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
	private List<GenericResponse> bindSquareOffData(SquareOffResModel squareOffResModel) {
//		List<GenericOrderResp> genericOrderRespList = new ArrayList<GenericOrderResp>();
		GenericOrderResp genericOrderResp = new GenericOrderResp();
		List<GenericResponse> sentResponse = new ArrayList<GenericResponse>();
		try {
			for (PlaceOrderRespModel model : squareOffResModel.getSquareOffResModel()) {

				genericOrderResp.setOrderNo(model.getData().getOrderId());
				genericOrderResp.setRequestTime("");// TODO still time is not comming from odin
				GenericResponse extract = prepareResponse.prepareSuccessResponseBody(genericOrderResp);
				sentResponse.add(extract);
				genericOrderResp = new GenericOrderResp();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		} finally {

		}
		return sentResponse;
	}

}

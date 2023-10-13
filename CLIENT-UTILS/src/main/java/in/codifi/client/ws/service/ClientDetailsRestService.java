package in.codifi.client.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.config.RestServiceProperties;
import in.codifi.client.entity.logs.RestAccessLogModel;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.repository.AccessLogManager;
import in.codifi.client.transformation.ClientDetailsRespModel;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.CodifiUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import in.codifi.client.ws.model.ResultModel;
import in.codifi.client.ws.model.UserProfileModel;
import in.codifi.client.ws.model.WSRestRespModel;
import in.codifi.client.ws.remodeling.ClientDetailsRemodeling;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ClientDetailsRestService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	ClientDetailsRemodeling clientDetailsRemodeling;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to invalidate WS session
	 * 
	 * @author dinesh
	 * @param request
	 * @return
	 */
	public RestResponse<GenericResponse> invalidateWsSession(String request) {
		ObjectMapper mapper = new ObjectMapper();
		WSRestRespModel object = new WSRestRespModel();
		try {
			Log.info("invalidateWsSession request - " + request);
			Log.info("invalidateWsSession URL - " + props.getWsInvalidateSession());
			System.out.println("invalidateWsSession request - " + request + "\n createWsSession URL - "
					+ props.getWsInvalidateSession());
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getWsInvalidateSession());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", "application/json");
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() == 401) {
				Log.error("Unauthorized error in client details");
				return prepareResponse.prepareUnauthorizedResponse();
			}
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br2 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br2.readLine()) != null) {
				object = mapper.readValue(output, WSRestRespModel.class);
			}
			for (ResultModel result : object.getResult()) {
				if (result.getStatus().contains("OK")) {
					return prepareResponse.prepareSuccessResponseObject(result.getStatus());
				} else {
					return prepareResponse.prepareFailedResponse(result.getStatus());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to create Web socket Session
	 * 
	 * @author dinesh
	 * @param request
	 * @return
	 */
	public RestResponse<GenericResponse> createWsSession(String request) {
		ObjectMapper mapper = new ObjectMapper();
		WSRestRespModel object = new WSRestRespModel();
		try {
			Log.info("createWsSession request - " + request);
			Log.info("createWsSession URL - " + props.getWsCreateSession());
			System.out.println(
					"createWsSession request - " + request + "\n createWsSession URL - " + props.getWsCreateSession());
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getWsCreateSession());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() == 401) {
				Log.error("Unauthorized error in client details");
				return prepareResponse.prepareUnauthorizedResponse();
			}
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br2 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br2.readLine()) != null) {
				object = mapper.readValue(output, WSRestRespModel.class);
			}
			for (ResultModel result : object.getResult()) {
				if (result.getStatus().contains("OK")) {
					return prepareResponse.prepareSuccessResponseObject(result.getStatus());
				} else {
					return prepareResponse.prepareFailedResponse(result.getStatus());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
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
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	public RestResponse<GenericResponse> getUserProfile(String userSession, ClinetInfoModel info) {
		UserProfileModel userProfileModel = new UserProfileModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin client request" + userSession);
		try {

			accessLogModel.setMethod("getClientDetails");
			accessLogModel.setModule(AppConstants.MODULE_CLIENT);
			accessLogModel.setUrl(props.getUserProfile());
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getUserProfile());
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
				Log.error("Unauthorized error in Odin client api");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin client response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.contains("Success Message")) {
						userProfileModel = mapper.readValue(output, UserProfileModel.class);
						ClientDetailsRespModel extract = clientDetailsRemodeling.bindProfileData(userProfileModel,
								info.getUserId());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (output.contains("[]")) {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.REST_NO_DATA);
					} else {
						Log.error("getClientDetails output -- " + output);
						return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("getClientDetails output -- " + output);
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
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

}

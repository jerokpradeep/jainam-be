package in.codifi.auth.ws.service;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.config.RestProperties;
import in.codifi.auth.entity.logs.RestAccessLogModel;
import in.codifi.auth.model.request.LoginOTPReq;
import in.codifi.auth.model.request.LoginRestReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.model.response.LogOutResponseModel;
import in.codifi.auth.model.response.OtpRestSuccessRespModel;
import in.codifi.auth.model.response.SessionRestRespModel;
import in.codifi.auth.repository.AccessLogManager;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.CodifiUtil;
import in.codifi.auth.utility.PrepareResponse;
import in.codifi.auth.utility.StringUtil;
import in.codifi.auth.ws.model.login.LoginRestResp;
import in.codifi.cache.model.ClinetInfoModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class LoginRestService {

	@Inject
	AccessLogManager accessLogManager;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestProperties props;

	public LoginRestResp ssoLogin(LoginRestReq req) {

		LoginRestResp loginRestResp = new LoginRestResp();
		try {
			ObjectMapper mapper = new ObjectMapper();
			String request = mapper.writeValueAsString(req);
			Log.info("login request" + request);
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("login");
			accessLogModel.setModule(AppConstants.MODULE_LOGIN);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(req.getUser_id());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getLogin());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			System.out.println("req--" + mapper.writeValueAsString(req));
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("loginResponseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("client details response" + output);
				loginRestResp = mapper.readValue(output, LoginRestResp.class);
				return loginRestResp;
			} else {
				Log.info("Error Connection in Login Rest Service. sso Login Response Code - " + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					loginRestResp = mapper.readValue(output, LoginRestResp.class);
					return loginRestResp;
				}
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return loginRestResp;
	}

	/**
	 * method to login
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	public RestResponse<GenericResponse> login(LoginRestReq req) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String request = mapper.writeValueAsString(req);
			Log.info("login request" + request);
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("login");
			accessLogModel.setModule(AppConstants.MODULE_LOGIN);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(req.getUser_id());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getLogin());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("loginResponseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in client details");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("client details response" + output);
				LoginRestResp loginRestResp = mapper.readValue(output, LoginRestResp.class);
				if (loginRestResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_NOT_OK)) {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				} else {
					SessionRestRespModel response = bindSession(loginRestResp);
					HazelcastConfig.getInstance().getRestUserSession().clear();
					String hzUserSessionKey = "APITEST" + AppConstants.HAZEL_KEY_REST_SESSION;
					HazelcastConfig.getInstance().getRestUserSession().put(hzUserSessionKey, response.getSession());
					String userSession = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
					return prepareResponse.prepareSuccessResponseObject(userSession);
				}
			} else {
				Log.info("Error Connection in client details. Response Code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					LoginRestResp failResp = mapper.readValue(output, LoginRestResp.class);
					return prepareResponse.prepareFailedResponse(failResp.getMessage());
				}
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to login
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	public SessionRestRespModel bindSession(LoginRestResp loginRestResp) {

		SessionRestRespModel response = new SessionRestRespModel();
		response.setSession(loginRestResp.getData().getAccessToken());

		return response;
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gowthaman M
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
	 * Method to sent otp
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> loginOTP(LoginOTPReq loginOTP) {
		ObjectMapper mapper = new ObjectMapper();
		Log.info("client details request" + loginOTP.toString());
		try {
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("loginOTP");
			accessLogModel.setModule(AppConstants.MODULE_LOGIN);
			accessLogModel.setReqBody(loginOTP.toString());
			accessLogModel.setUserId(loginOTP.getUser_id());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getLoginOTP());
			accessLogModel.setUrl(url.toString());
			String request = mapper.writeValueAsString(loginOTP);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("loginOTPResponseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in client details");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("client details response" + output);
				OtpRestSuccessRespModel clientDetailsSuccess = mapper.readValue(output, OtpRestSuccessRespModel.class);
				if (clientDetailsSuccess.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_NOT_OK)) {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
				}

			} else {
				Log.info("Error Connection in client details. Response Code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to Log Out From Odin
	 * 
	 * @author LOKESH
	 * @return
	 */
	public RestResponse<GenericResponse> deleteLogOut(String userSession, ClinetInfoModel info) {
		LogOutResponseModel logOutRespModel = new LogOutResponseModel();
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		String output = null;
		try {
			accessLogModel.setMethod("Logout");
			accessLogModel.setModule(AppConstants.MODULE_LOGIN);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getLogout());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.DELETE_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("Rest Log Out Info responseCode -- " + responseCode);
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
					logOutRespModel = mapper.readValue(output, LogOutResponseModel.class);
					/** Bind the response to generic response **/
//					if ((logOutRespModel.getStatus().equalsIgnoreCase("error"))) {
//						return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
//				} else
					if (logOutRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse.prepareSuccessResponseObject(AppConstants.LOG_OUT_SUCCESS);
//						return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
					} else if (logOutRespModel.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
						return prepareResponse.prepareFailedResponseForRestService(logOutRespModel.getMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(
								StringUtil.isNotNullOrEmpty(logOutRespModel.getMessage()) ? logOutRespModel.getMessage()
										: AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection Rest Log Out API. Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
				if (StringUtil.isNotNullOrEmpty(output)) {
					logOutRespModel = mapper.readValue(output, LogOutResponseModel.class);
					if (StringUtil.isNotNullOrEmpty(logOutRespModel.getMessage()))
						return prepareResponse.prepareFailedResponseForRestService(logOutRespModel.getMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Rest Log Out Info -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

}

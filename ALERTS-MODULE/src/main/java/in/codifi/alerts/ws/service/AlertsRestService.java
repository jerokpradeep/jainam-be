package in.codifi.alerts.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONValue;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.alerts.config.ApplicationProperties;
import in.codifi.alerts.entity.logs.RestAccessLogModel;
import in.codifi.alerts.entity.primary.AlertsEntity;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.repository.AccessLogManager;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.CodifiUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertRespModel;
import in.codifi.alerts.ws.model.AndroidNotification;
import in.codifi.alerts.ws.model.GetAlertsRespModel;
import in.codifi.alerts.ws.model.PushNotifyAndroid;
import in.codifi.alerts.ws.model.PushNotifyData;
import in.codifi.alerts.ws.model.PushNotifyNotification;
import in.codifi.alerts.ws.model.PushNotifyReq;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AlertsRestService {
	@Inject
	ApplicationProperties props;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * method to send recommendation message for users
	 * 
	 * @author SOWMIYA
	 * @param deviceId
	 * @param symbol
	 * @param message
	 */
	public void sendRecommendationMessage(String deviceId, String symbol, String message) {
		try {

			ExecutorService pool = Executors.newSingleThreadExecutor();
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL(props.getFcmBaseUrl());
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setUseCaches(false);
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setRequestMethod(AppConstants.POST);
						conn.setRequestProperty(AppConstants.AUTHORIZATION,
								AppConstants.KEY_EQUAL + props.getFcmApiKey());
						conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);

						PushNotifyReq requestNew = new PushNotifyReq();
						requestNew.setClickAction(AppConstants.FLUTTER_NOTIFICATION_CLICK);
						requestNew.setCollapseKey(AppConstants.COLLAPSE_KEY);

						PushNotifyAndroid androidNew = new PushNotifyAndroid();
						AndroidNotification notificationAndroidNew = new AndroidNotification();
						notificationAndroidNew.setChannelId(AppConstants.CUSTOM_NOTIFICATION_CODIFI);
						androidNew.setNotification(notificationAndroidNew);
						requestNew.setAndroid(androidNew);

						PushNotifyNotification noramlNotification = new PushNotifyNotification();
						noramlNotification.setAndroidChannelId(AppConstants.CUSTOM_NOTIFICATION_CODIFI);
						noramlNotification.setChannelId(AppConstants.CUSTOM_NOTIFICATION_CODIFI);
						noramlNotification.setTitleColor(AppConstants.TITLE_COLOR);
						noramlNotification.setTitle(AppConstants.ALERT_TRIGGERED + symbol);
						noramlNotification.setBody(message);
						requestNew.setNotification(noramlNotification);
						requestNew.setTo(deviceId);

						PushNotifyData data = new PushNotifyData();
						data.setType(AppConstants.INFO);
						data.setMessage(message);
						data.setTitle(AppConstants.ALERT_TRIGGERED + symbol);
						requestNew.setData(data);

						OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
						ObjectMapper om = new ObjectMapper();
						wr.write(om.writeValueAsString(requestNew));
						wr.flush();
						wr.close();
						int responseCode = conn.getResponseCode();
						System.out.println("Request : " + om.writeValueAsString(requestNew));
						System.out.println("Response Code : " + responseCode);
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
					} catch (Exception e) {
						e.printStackTrace();
						Log.error(e.getMessage());
					}
				}
			});
			pool.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * method to set Alerts
	 * 
	 * @author SOWMIYA
	 * @param request
	 * @param info
	 * @param reqModel
	 * @return
	 */

	public Object setAlert(AlertsEntity alertEntity, String userId) {
		long triggerId = alertEntity.getId();
		Object object = new Object();
		URL url = null;
		try {
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("setAlert");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String exch = "";
			if (alertEntity.getExch().equalsIgnoreCase("NSE")) {
				exch = "NSEEQ";
			} else if (alertEntity.getExch().equalsIgnoreCase("NFO")) {
				exch = "NSEFO";
			} else if (alertEntity.getExch().equalsIgnoreCase("CDS")) {
				exch = "NSECD";
			} else if (alertEntity.getExch().equalsIgnoreCase("MCX")) {
				exch = "MCX";
			} else if (alertEntity.getExch().equalsIgnoreCase("BSE")) {
				exch = "BSEEQ";
			} else if (alertEntity.getExch().equalsIgnoreCase("BFO")) {
				exch = "BSEFO";
			} else if (alertEntity.getExch().equalsIgnoreCase("BCD")) {
				exch = "BSECD";
			}
			String urls = props.getAlertBaseUrl() + "alertId=" + triggerId + "&param=LTP&operator="
					+ alertEntity.getOperator() + "&token=" + alertEntity.getToken() + "&value="
					+ alertEntity.getValue() + "&exch=" + exch + "&vendorname=" + props.getAlertVendorName();
			url = new URL(urls);

			Log.info("Alerts set-"+urls);
			accessLogModel.setUrl(props.getAlertBaseUrl());
			accessLogModel.setReqBody(urls);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET);
			conn.setDoOutput(true);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			if (conn.getResponseCode() != 200) {
				accessLogModel.setResBody("Failed : HTTP error code : ");
				insertRestAccessLogs(accessLogModel);
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
		
			while ((output = br1.readLine()) != null) {
				Log.info("Alerts set response-" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				object = JSONValue.parse(output);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return object;
	}

	/**
	 * method to insert rest access logs
	 * 
	 * @author SOWMIYA
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
					Log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * method to get alerts
	 * 
	 * @author SOWMIYA
	 * @param userId
	 * @return
	 */
	public RestResponse<GenericResponse> getAlerts(String userId) {
		GetAlertsRespModel respModel = new GetAlertsRespModel();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getAlert");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setReqBody(userId);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getGetAlertUrl() + userId);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("get alerts response" + output);
				respModel = mapper.readValue(output, GetAlertsRespModel.class);
				if (respModel != null) {
					return prepareResponse.prepareSuccessResponseObject(respModel);
				}
			} else if (responseCode == 401) {
				Log.error("Unauthorized error in get alert");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else {
				Log.info("Error Connection in get alerts. Response Code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					respModel = mapper.readValue(output, GetAlertsRespModel.class);
					return prepareResponse.prepareFailedResponse(respModel.getMessage());
				}
			}

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to delete alerts
	 * 
	 * @author SOWMIYA
	 * @param userId
	 * @param serverAlertId
	 * @return
	 */
	public RestResponse<GenericResponse> deleteAlerts(String userId, String serverAlertId) {
		AlertRespModel respModel = new AlertRespModel();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("deleteAlerts");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setReqBody(userId + "/" + serverAlertId);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getDeleteAlertUrl() + userId + "/" + serverAlertId);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("delete alerts response" + output);
				respModel = mapper.readValue(output, AlertRespModel.class);
				if (respModel != null) {
					return prepareResponse.prepareSuccessResponseObject(respModel);
				}
			} else if (responseCode == 401) {
				Log.error("Unauthorized error in delete alert");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else {
				Log.info("Error Connection in delete alerts. Response Code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					respModel = mapper.readValue(output, AlertRespModel.class);
					return prepareResponse.prepareFailedResponse(respModel.getMessage());
				}
			}

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method modify alerts
	 * 
	 * @author SOWMIYA
	 * @param request
	 * @param userId
	 * @return
	 */
	public RestResponse<GenericResponse> modifyAlerts(String request, String userId) {
		AlertRespModel respModel = new AlertRespModel();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("modifyAlerts");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setReqBody(request);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getModifyAlertUrl());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("modify alerts response" + output);
				respModel = mapper.readValue(output, AlertRespModel.class);
			} else if (responseCode == 401) {
				Log.error("Unauthorized error in modify alerts api");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else {
				Log.info("Error Connection in modify alerts. Response Code -" + responseCode);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					respModel = mapper.readValue(output, AlertRespModel.class);
					return prepareResponse.prepareFailedResponse(respModel.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

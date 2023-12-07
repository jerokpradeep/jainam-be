package in.codifi.alerts.ws.service;

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

import in.codifi.alerts.config.ApplicationProperties;
import in.codifi.alerts.entity.logs.RestAccessLogModel;
import in.codifi.alerts.model.response.AlertsResponse;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.repository.AccessLogManager;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.CodifiUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertRespModel;
import in.codifi.alerts.ws.model.AlertResponse;
import in.codifi.alerts.ws.model.AlertResponseData;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.model.ModifyAlertsReqModel;
import in.codifi.cache.model.ClientInfoModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AlertsOdinRestService {

	@Inject
	ApplicationProperties props;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @param userSession
	 * @return
	 */
	public RestResponse<GenericResponse> getAlerts(String userSession, ClientInfoModel info) {
		AlertResponse alertRespModel = new AlertResponse();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin get Alerts request" + userSession);
		try {
			accessLogModel.setMethod("getAlerts");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getOdinAlert() + info.getUserId());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("getAlerts responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin get Alerts api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin get Alerts response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output -- " + output);
					if (output.contains("Alerts fetched successfully.") && output.contains("[]")) {
						return prepareResponse.prepareFailedResponse(AppConstants.No_RECORDS_FOUND);
					} else {
						alertRespModel = mapper.readValue(output, AlertResponse.class);
						List<AlertsResponse> getAlert = bindGetAlert(alertRespModel.getData(), info);
						return prepareResponse.prepareSuccessResponseObject(getAlert);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("getAlert output -- " + output);
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to bind Get Alert
	 * 
	 * @author Gowthaman
	 * @param data
	 * @return
	 */
	public List<AlertsResponse> bindGetAlert(List<AlertResponseData> data, ClientInfoModel info) {
		List<AlertsResponse> response = new ArrayList<>();
		try {
			for (AlertResponseData rSet : data) {
				AlertsResponse result = new AlertsResponse();
				result.setAlertId(rSet.getServerAlertId());
				result.setId(rSet.getTriggered());
				result.setAlertName(null);
				result.setCreatedBy(null);
				result.setCreatedOn(null);
//			for(AlertsCondition alertsCondition : rSet.getCondition()) {
				result.setAlertType(rSet.getCondition().get(0).getOperandOne().getField());

				if (rSet.getCondition().get(0).getOperandOne().getMarketsegment().toString().equalsIgnoreCase("1")) {
					result.setExch(AppConstants.NSE);
				} else if (rSet.getCondition().get(0).getOperandOne().getMarketsegment().toString()
						.equalsIgnoreCase("8")) {
					result.setExch(AppConstants.BSE);
				} else if (rSet.getCondition().get(0).getOperandOne().getMarketsegment().toString()
						.equalsIgnoreCase("2")) {
					result.setExch(AppConstants.NFO);
				}
				result.setExch(rSet.getCondition().get(0).getOperandOne().getMarketsegment().toString());
				result.setExchSeg(rSet.getCondition().get(0).getOperandOne().getMarketsegment().toString());

				if (rSet.getCondition().get(0).getOperator().equalsIgnoreCase(">")) {
					result.setOperator("greater");
				} else if (rSet.getCondition().get(0).getOperator().equalsIgnoreCase(">= or <=")) {
					result.setOperator("equal");
				} else if (rSet.getCondition().get(0).getOperator().equalsIgnoreCase("<")) {
					result.setOperator("lesser");
				} else if (rSet.getCondition().get(0).getOperator().equalsIgnoreCase(">=")) {
					result.setOperator("greaterequal");
				} else if (rSet.getCondition().get(0).getOperator().equalsIgnoreCase("<=")) {
					result.setOperator("lesserequal");
				}
//				result.setOperator(rSet.getCondition().get(0).getOperator());

				result.setScripName(rSet.getCondition().get(0).getOperandOne().getToken().toString());
				result.setToken(rSet.getCondition().get(0).getOperandOne().getToken().toString());
				result.setValue(rSet.getCondition().get(0).getOperandTwo().getValue());
//			}
				result.setTriggeredTime(rSet.getCreateDate());
				result.setTriggerStatus(null);
				result.setUpdatedBy(null);
				result.setUpdatedOn(null);
				result.setUserId(info.getUserId());

				response.add(result);

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("bindGetAlert -- " + e.getMessage());
		}

		return response;
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
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unused")
	public RestResponse<GenericResponse> createAlerts(AlertsReqModel req, String userSession, ClientInfoModel info) {
		AlertRespModel alertRespModel = new AlertRespModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin create Alerts request" + userSession);
		try {
			String request = mapper.writeValueAsString(req);
			accessLogModel.setMethod("createAlerts");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setReqBody(request);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getOdinCreateAlert());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			System.out.println("createAlerts responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin create Alerts api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin create Alerts response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output -- " + output);
					if (output.contains("create Alerts fetched successfully.") && output.contains("[]")) {
						return prepareResponse.prepareFailedResponse(AppConstants.No_RECORDS_FOUND);
					} else {
						alertRespModel = mapper.readValue(output, AlertRespModel.class);
						return prepareResponse.prepareSuccessResponseMessage(alertRespModel.getData().getAlertId());
//						return prepareResponse.prepareSuccessResponseMessage(AppConstants.SUCCESS_STATUS);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("createAlert output -- " + output);
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @param userSession
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> updateAlerts(ModifyAlertsReqModel req, String userSession,
			ClientInfoModel info) {
		AlertRespModel alertRespModel = new AlertRespModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin update Alerts request" + userSession);
		try {
			accessLogModel.setMethod("updateAlerts");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getOdinCreateAlert());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.PUT_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = mapper.writeValueAsString(req).getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			System.out.println("updateAlerts responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin update Alerts api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin update Alerts response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output -- " + output);
					alertRespModel = mapper.readValue(output, AlertRespModel.class);
					return prepareResponse.prepareSuccessResponseMessage(alertRespModel.getData().getAlertId());
//					return prepareResponse.prepareSuccessResponseMessage(AppConstants.SUCCESS_STATUS);
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("update Alert output -- " + output);
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("update alert -- " + e.getMessage());
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to delete Alert
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param alertId
	 * @param userSession
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> deleteAlert(String alertId,
			String userSession, ClientInfoModel info) {
		AlertRespModel alertRespModel = new AlertRespModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin update Alerts request" + userSession);
		try {
			accessLogModel.setMethod("deleteAlert");
			accessLogModel.setModule(AppConstants.MODULE_ALERTS);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String str = alertId.substring(1, alertId.length() - 1);

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getDeleteAlertUrl() + "/" + info.getUserId() + "/" + alertId);
//			URL url = new URL(props.getOdinCreateAlert() + "/" + info.getUserId() + "/" + str);
			System.out.println("delete Alert URL -- " + url);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.DELETE_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
//			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("deleteAlert responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin delete Alerts api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin delete Alerts response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output -- " + output);
					alertRespModel = mapper.readValue(output, AlertRespModel.class);
					return prepareResponse.prepareSuccessResponseObject(alertRespModel.getData());
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("deleteAlert output -- " + output);
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("delete Alert -- " + e.getMessage());
		}
		accessLogModel.setResBody(AppConstants.FAILED_STATUS);
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

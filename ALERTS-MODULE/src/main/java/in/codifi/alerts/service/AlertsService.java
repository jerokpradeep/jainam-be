package in.codifi.alerts.service;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.alerts.entity.primary.AlertsEntity;
import in.codifi.alerts.entity.primary.DeviceEntity;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.repository.AlertsRepository;
import in.codifi.alerts.repository.DeviceRepository;
import in.codifi.alerts.service.spec.AlertsServiceSpec;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.AppUtil;
import in.codifi.alerts.utility.EmailUtils;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.service.AlertsRestService;
import in.codifi.cache.model.ClientInfoModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AlertsService implements AlertsServiceSpec {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AlertsRepository alertsRepository;
	@Inject
	AppUtil appUtil;
	@Inject
	EmailUtils emailUtils;
	@Inject
	DeviceRepository deviceRepository;
	@Inject
	AlertsRestService alertsRestService;

	/**
	 * method to create alerts
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> createAlert(AlertsReqModel reqModel, ClientInfoModel info) {
		try {
			if (reqModel == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			ObjectMapper mapper = new ObjectMapper();
			String request = mapper.writeValueAsString(reqModel);
			alertsRestService.setAlert(request, info);

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to update trigger status
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> updateTriggerStatus(int alertId, ClientInfoModel info) {
		try {
			/** method to update trigger status **/
			updatetoDataBase(alertId);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to update to trigger status
	 * 
	 * @author SOWMIYA
	 * @param alertId
	 */
	private void updatetoDataBase(int alertId) {
		try {
			AlertsEntity alertDetails = alertsRepository.getTriggerIdDetails(alertId);
			if (alertDetails != null) {
				String pUserId = alertDetails.getUserId();
				String operatorString = alertDetails.getOperator();
				String scripName = alertDetails.getScripName().toUpperCase();
				double value = alertDetails.getValue();
				String operator = "";
				if (operatorString.equalsIgnoreCase("greater")) {
					operator = ">";
				} else if (operatorString.equalsIgnoreCase("equal")) {
					operator = ">= or <=";
				} else if (operatorString.equalsIgnoreCase("lesser")) {
					operator = "<";
				} else if (operatorString.equalsIgnoreCase("greaterequal")) {
					operator = ">=";
				} else if (operatorString.equalsIgnoreCase("lesserequal")) {
					operator = "<=";
				}
				java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
				/** Form the message sent to the user */
				String message = scripName + " " + operator + " " + value + " triggered at  " + timestamp;
				/** Send Alert to user via mail */
				sendEmailAlert(pUserId, scripName, message);
				/** Send Alert to user via Push Notification */
				sendPushNotificationAlert(pUserId, scripName, message);

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

	}

	/**
	 * method to send push notification alerts
	 * 
	 * @author SOWMIYA
	 * @param pUserId
	 * @param scripName
	 * @param message
	 */
	private void sendPushNotificationAlert(String pUserId, String scripName, String message) {
		try {
			/** Fetch the device id for the given User id */
			List<DeviceEntity> deviceIdList = deviceRepository.getDeviceIdForUser(pUserId);
			ExecutorService pool = Executors.newSingleThreadExecutor();
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						for (int i = 0; i < deviceIdList.size(); i++) {
							/** Method to send push notification */
							alertsRestService.sendRecommendationMessage(deviceIdList.get(i).getDeviceId(), scripName,
									message);
						}
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
	 * Method to send the email alert when the price is triggered
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	private void sendEmailAlert(String pUserId, String scripName, String message) {
		/** Get User Details from the Data base */
		ClientInfoModel info = appUtil.getClientInfo();
		try {
			if (StringUtil.isNotNullOrEmpty(info.getEmail()) && StringUtil.isNotNullOrEmpty(info.getName())) {
				/** Method to send email **/
//						commonMethod.mailUpdate(message, info.getEmail(), info.getName(), scripName);
				emailUtils.sendAlert(info.getName(), message, scripName, info.getEmail());
			} else {
				Log.error(AppConstants.EMAIL_EXCEPTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

	/**
	 * method to get alerts
	 * 
	 * @author SOWMIYA
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> getAlerts(ClientInfoModel info) {
		try {
			if (StringUtil.isNullOrEmpty(info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			return alertsRestService.getAlerts(info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to delete alerts
	 * 
	 * @author SOWMIYA
	 * @param info
	 * @param serverAlertId
	 * @return
	 */
	public RestResponse<GenericResponse> deleteAlerts(ClientInfoModel info, String serverAlertId) {
		try {
			if (StringUtil.isNullOrEmpty(serverAlertId))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			return alertsRestService.deleteAlerts(info.getUserId(), serverAlertId);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to modify alerts
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> modifyAlerts(AlertsReqModel reqModel, ClientInfoModel model) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (reqModel == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			String request = mapper.writeValueAsString(reqModel);
			return alertsRestService.modifyAlerts(request,model.getUserId());

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

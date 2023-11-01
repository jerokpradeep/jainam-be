package in.codifi.alerts.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.controller.spec.AlertsOdinControllerSpec;
import in.codifi.alerts.model.request.RequestModel;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.service.spec.AlertsOdinServiceSpec;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.AppUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.cache.model.ClientInfoModel;
import io.quarkus.logging.Log;

@Path("/odinAlerts")
public class AlertsOdinController implements AlertsOdinControllerSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	AlertsOdinServiceSpec alertsService;

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAlerts() {
		ClientInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClientInfoModel info = new ClientInfoModel();
//		info.setUserId("117995");
		return alertsService.getAlerts(info);
	}

	/**
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createAlerts(RequestModel req) {
		ClientInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		if (StringUtil.isNotNullOrEmpty(req.getExch())) {
			String operatorString = req.getOperator();
			if (operatorString.equalsIgnoreCase("greater")) {
				req.setOperator(">");
			} else if (operatorString.equalsIgnoreCase("equal")) {
				req.setOperator(">= or <=");
			} else if (operatorString.equalsIgnoreCase("lesser")) {
				req.setOperator("<");
			} else if (operatorString.equalsIgnoreCase("greaterequal")) {
				req.setOperator(">=");
			} else if (operatorString.equalsIgnoreCase("lesserequal")) {
				req.setOperator("<=");
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
//			ClientInfoModel info = new ClientInfoModel();
//			info.setUserId("117995");
			return alertsService.createAlerts(req, info);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateAlerts(RequestModel req) {
		ClientInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}

		if (StringUtil.isNotNullOrEmpty(req.getExch()) && StringUtil.isNotNullOrEmpty(req.getAlertId())) {
			String operatorString = req.getOperator();
			if (operatorString.equalsIgnoreCase("greater")) {
				req.setOperator(">");
			} else if (operatorString.equalsIgnoreCase("equal")) {
				req.setOperator(">= or <=");
			} else if (operatorString.equalsIgnoreCase("lesser")) {
				req.setOperator("<");
			} else if (operatorString.equalsIgnoreCase("greaterequal")) {
				req.setOperator(">=");
			} else if (operatorString.equalsIgnoreCase("lesserequal")) {
				req.setOperator("<=");
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

//		ClientInfoModel info = new ClientInfoModel();
//		info.setUserId("117995");
			return alertsService.updateAlerts(req, info);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to delete alert
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param alertId
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteAlert(String alertId) {
//		ClientInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		ClientInfoModel info = new ClientInfoModel();
		info.setUserId("117995");
		return alertsService.deleteAlert(alertId, info);
	}

}

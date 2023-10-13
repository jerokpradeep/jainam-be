package in.codifi.alerts.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.controller.spec.AlertsControllerSpec;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.service.spec.AlertsServiceSpec;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.AppUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.cache.model.ClientInfoModel;
import io.quarkus.logging.Log;

@Path("/alerts")
public class AlertsController implements AlertsControllerSpec {

	@Inject
	AlertsServiceSpec alertsService;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;

	/**
	 * method to create Alert
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createAlert(AlertsReqModel reqModel) {
		if (reqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClientInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return alertsService.createAlert(reqModel, info);
	}

	/**
	 * method to update trigger status
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> updateTriggerStatus(int alertId) {
		if (alertId < 0)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClientInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return alertsService.updateTriggerStatus(alertId, info);
	}

}

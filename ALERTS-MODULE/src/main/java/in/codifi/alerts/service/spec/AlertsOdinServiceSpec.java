package in.codifi.alerts.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.model.ModifyAlertsReqModel;
import in.codifi.cache.model.ClientInfoModel;

public interface AlertsOdinServiceSpec {

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getAlerts(ClientInfoModel info);

	/**
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	RestResponse<GenericResponse> createAlerts(AlertsReqModel req, ClientInfoModel info);

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> updateAlerts(ModifyAlertsReqModel req, ClientInfoModel info);

	/**
	 * Method to delete alert
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param alertId
	 * @return
	 */
	RestResponse<GenericResponse> deleteAlert(String alertId, ClientInfoModel info);

}

package in.codifi.alerts.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.request.RequestModel;
import in.codifi.alerts.model.response.GenericResponse;
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
	RestResponse<GenericResponse> createAlerts(RequestModel req, ClientInfoModel info);

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> updateAlerts(RequestModel req, ClientInfoModel info);

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

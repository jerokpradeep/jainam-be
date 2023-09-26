package in.codifi.alerts.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.cache.model.ClientInfoModel;

public interface AlertsServiceSpec {

	/**
	 * method to create alerts
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> createAlert(AlertsReqModel reqModel, ClientInfoModel info);

	/**
	 * method to update trigger status
	 * 
	 * @author SOWMIYA
	 * @param alertId
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> updateTriggerStatus(int alertId, ClientInfoModel info);

}

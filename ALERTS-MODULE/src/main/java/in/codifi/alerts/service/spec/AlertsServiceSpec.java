package in.codifi.alerts.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.entity.primary.AlertsEntity;
import in.codifi.alerts.model.request.RequestModel;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.cache.model.ClientInfoModel;

public interface AlertsServiceSpec {

	/**
	 * method to create Alert
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> createAlert(RequestModel reqModel, ClientInfoModel info);

	/**
	 * method to get alert Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getAlertDetails(ClientInfoModel info);

	/**
	 * method to update Alert
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> updateAlert(AlertsEntity alertsEntity, ClientInfoModel info);

	/**
	 * method to delete Alert
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> deleteAlert(int id, ClientInfoModel info);

	/**
	 * method to update Trigger Status
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> updateTriggerStatus(int id);

}

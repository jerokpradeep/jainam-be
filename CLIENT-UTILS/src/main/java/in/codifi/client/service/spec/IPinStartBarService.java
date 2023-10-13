package in.codifi.client.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.model.request.PinToStartbarModel;
import in.codifi.client.model.response.GenericResponse;

public interface IPinStartBarService {

	/**
	 * method to get pin to start bar details
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	RestResponse<GenericResponse> getPinToStartBar(ClinetInfoModel info);

	/**
	 * Method to add pin to start bar details
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	RestResponse<GenericResponse> addPinToStartBar(PinToStartbarModel model, ClinetInfoModel info);

	/**
	 * Method to reload cache
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	RestResponse<GenericResponse> loadPinToStartBarIntoCache();

}

package in.codifi.mw.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.response.GenericResponse;

public interface ICacheService {

	/**
	 * Method to load user mw data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> loadUserMWData();

}

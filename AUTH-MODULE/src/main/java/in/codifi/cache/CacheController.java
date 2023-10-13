package in.codifi.cache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.response.GenericResponse;

@Path("/cache")
public class CacheController {

	@Inject
	CacheService service;

	/**
	 * 
	 * Method to clear search data from cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Path("/rkbs")
	@GET
	public RestResponse<GenericResponse> clearAllRestSession() {
		return service.clearAllRestSession();
	}

	@Path("/user/rkbs")
	@GET
	public RestResponse<GenericResponse> clearAllRestSession(@QueryParam("userId") String userId) {
		return service.clearRestSessionById(userId);
	}

	@Path("/client/clear")
	@GET
	public RestResponse<GenericResponse> clearAllRestApiClientDetails() {
		return service.clearAllRestApiClientDetails();
	}

	@Path("/client/user/clear")
	@GET
	public RestResponse<GenericResponse> clearRestApiClientDetailsById(@QueryParam("userId") String userId) {
		return service.clearRestApiClientDetailsById(userId);
	}
	
	/**
	 * Method to clear Login Count
	 * 
	 * @author Gowthaman
	 * @created on 25-Sep-2023
	 * @return
	 */
	@Path("/client/loginCount/clear")
	@GET
	public RestResponse<GenericResponse> clearLoginCount() {
		return service.clearLoginCount();
	}
}

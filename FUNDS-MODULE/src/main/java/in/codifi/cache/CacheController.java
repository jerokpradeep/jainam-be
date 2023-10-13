package in.codifi.cache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.funds.model.response.GenericResponse;

@Path("/cache")
public class CacheController {

	@Inject
	CacheService service;

	/**
	 * 
	 * Method to clear all cache
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Path("/clear/all/")
	@GET
	public RestResponse<GenericResponse> clearAllCache() {
		return service.clearAllCache();
	}
}

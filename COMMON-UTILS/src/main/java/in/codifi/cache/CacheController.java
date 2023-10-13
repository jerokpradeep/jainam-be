package in.codifi.cache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.model.response.GenericResponse;

@Path("/cache")
public class CacheController {

	@Inject
	CacheService cacheService;

	/**
	 * Method to clear cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/clearCache")
	@GET
	public RestResponse<GenericResponse> clearCache() {
		return cacheService.clearCache();
	}

	/**
	 * Method to load Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/loadCache")
	@GET
	public RestResponse<GenericResponse> loadCache() {
		return cacheService.loadCache();
	}

	/**
	 * Method to load AppVersion Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/clearAppVersionCache")
	@GET
	public RestResponse<GenericResponse> clearAppVersionCache() {
		return cacheService.clearAppVersionCache();
	}

	/**
	 * Method to load AppVersion Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/errorCount")
	@GET
	public RestResponse<GenericResponse> errorCount() {
		return cacheService.getErrorCount();
	}

}

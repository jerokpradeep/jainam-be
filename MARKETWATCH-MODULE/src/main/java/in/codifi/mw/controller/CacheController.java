package in.codifi.mw.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.controller.spec.ICacheController;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.service.spec.ICacheService;

@Path("/mwcache")
public class CacheController implements ICacheController {

	@Inject
	ICacheService cacheService;

	/**
	 * Method to load user mw data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loadUserMWData() {
		return cacheService.loadUserMWData();
	}

}

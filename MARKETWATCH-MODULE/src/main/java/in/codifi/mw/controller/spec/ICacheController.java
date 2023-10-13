package in.codifi.mw.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.response.GenericResponse;

public interface ICacheController {

	/**
	 * Method to load user mw data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/loadusermwdata")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Load Contract Master in cache from Data base")
	public RestResponse<GenericResponse> loadUserMWData();

}

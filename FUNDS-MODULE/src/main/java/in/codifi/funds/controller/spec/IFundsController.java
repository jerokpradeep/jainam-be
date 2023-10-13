package in.codifi.funds.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.funds.model.response.GenericResponse;


public interface IFundsController {
	
	/**
	 * Method to get limits
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/limits")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getLimits();

}

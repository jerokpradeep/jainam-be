package in.codifi.analytics.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.analytics.model.request.AnalyticsRequest;
import in.codifi.analytics.model.response.GenericResponse;

public interface AnalyticsControllerSpec {
	
	/**
	 * Method to add Research Call
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/researchcall")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> addResearchCall(AnalyticsRequest req);

}

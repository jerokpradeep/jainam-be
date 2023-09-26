package in.codifi.holdings.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.holdings.model.request.EdisHoldModel;
import in.codifi.holdings.model.request.EdisSummaryRequest;
import in.codifi.holdings.model.response.GenericResponse;

public interface EdisControllerSpec {

	/**
	 * method to get edis redirect URL take some variables from cache
	 * 
	 * @author Nesan
	 * 
	 * @return
	 */
	@Path("/redirectUrl")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getRedirectUrl(List<EdisHoldModel> model);
	
	@Path("/edisSummary")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getEdisSummary(EdisSummaryRequest req);

}

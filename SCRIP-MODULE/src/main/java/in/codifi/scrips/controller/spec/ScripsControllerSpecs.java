package in.codifi.scrips.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.scrips.model.request.GetContractInfoReqModel;
import in.codifi.scrips.model.request.SearchScripReqModel;
import in.codifi.scrips.model.request.SecurityInfoReqModel;
import in.codifi.scrips.model.response.GenericResponse;

public interface ScripsControllerSpecs {

	/**
	 * Method to get all scrips by search
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get all the scrips based upon the scrip")
	RestResponse<GenericResponse> getScrips(SearchScripReqModel reqModel);

	/**
	 * Method to get contract info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/contract/info")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get contract info")
	RestResponse<GenericResponse> getContractInfo(GetContractInfoReqModel reqModel);

	/**
	 * Method to get security info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/security/info")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSecurityInfo(SecurityInfoReqModel reqModel);

}

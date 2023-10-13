package in.codifi.mw.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.entity.primary.PredefinedMwEntity;
import in.codifi.mw.model.response.GenericResponse;

public interface IPredefinedMwContoller {

	/**
	 * Method to get all predefined market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/get")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get Scrip details for given user id .")
	RestResponse<GenericResponse> getAllPrefedinedMwScrips();

	/**
	 * Method to add the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/addscrips")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> addscrip(PredefinedMwEntity predefinedMwEntity);

	/**
	 * Method to delete the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/deletescrips")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deletescrip(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to0 sort mw scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/sortscrips")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> sortMwScrips(PredefinedMwEntity predefinedEntity);

}

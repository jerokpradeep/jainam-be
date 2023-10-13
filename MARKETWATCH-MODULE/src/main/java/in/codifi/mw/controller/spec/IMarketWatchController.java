package in.codifi.mw.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.request.MwRequestModel;
import in.codifi.mw.model.response.GenericResponse;

public interface IMarketWatchController {

	/**
	 * Method to get the scrip details for the given User Id *
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getAllMwScrips")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get Scrip details for given user id .")
	public RestResponse<GenericResponse> getAllMwScrips(MwRequestModel reqModel);
	
	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getMWScrips")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get Scrip details for given user id and Market Watch.")
	public RestResponse<GenericResponse> getMWScrips(MwRequestModel reqModel);
	
	/**
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/deletescrip")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To delete symbol in market Watch")
	public RestResponse<GenericResponse> deletescrip(MwRequestModel reqModel);
	
	/**
	 * Method to add the scrip into cache and data base
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/addscrip")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To add symbol in market Watch")
	public RestResponse<GenericResponse> addscrip(MwRequestModel reqModel);
	
	/**
	 * Method to sort the scrip into cache and data base
	 * 
	 * @author Gowthaman M
	 */
	@Path("/sortMwScrips")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To add symbol in market Watch")
	public RestResponse<GenericResponse> sortMwScrips(MwRequestModel reqModel);
	
	/**
	 * Method to create the new marketWatch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/createMW")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To create market Watch")
	public RestResponse<GenericResponse> createMW(MwRequestModel reqModel);
	
	/**
	 * Method to change market watch name
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/renameMW")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To rename market Watch")
	RestResponse<GenericResponse> renameMarketWatch(MwRequestModel reqModel);
	
	/**
	 * 
	 * Method to Delete expired scrips in MW List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/delete/expired/scrip")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteExpiredContract();

}

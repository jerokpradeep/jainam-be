package in.codifi.client.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.client.model.request.ClientDetailsReqModel;
import in.codifi.client.model.response.GenericResponse;

public interface IProfileController {

	/**
	 * Method to invalidate web socket session
	 * 
	 * @author dinesh
	 * @param model
	 * @return
	 */
	@Path("/invalidateWsSess")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> invalidateWsSession(ClientDetailsReqModel reqModel);

	/**
	 * Method to create web socket session
	 * 
	 * @param model
	 * @return
	 */
	@Path("/createWsSess")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createWsSession(ClientDetailsReqModel reqModel);

	/**
	 * Method to get user session for WS
	 * 
	 * @author DINESH KUMAR
	 * @return
	 */
	@Path("/getUser")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getDetails();

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Path("/getclientdetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getUserProfile();

	/**
	 * methdo to create web socket session test
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	@Path("/createWsSess/test")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createWsSessionTest(ClientDetailsReqModel reqModel);

	/**
	 * method to invalidate web socket session test
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	@Path("/invalidateWsSess/test")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> invalidateWsSessionTest(ClientDetailsReqModel reqModel);

}

package in.codifi.position.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.position.model.request.OrderDetails;
import in.codifi.position.model.request.PositionConversionReq;
import in.codifi.position.model.request.UserSession;
import in.codifi.position.model.response.GenericResponse;

public interface IPositionController {

	/**
	 * Method to get the Position book with MTM
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get position")
	RestResponse<GenericResponse> getposition();

	/**
	 * Method to get the day Position book with MTM
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/day")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get position")
	RestResponse<GenericResponse> getDailyPosition();

	/**
	 * Method to get the net Position book with MTM
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/net")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get position")
	RestResponse<GenericResponse> getExpiryPosition();

	/**
	 * Method to get the position conversion
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/conversion")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Position conversion")
	RestResponse<GenericResponse> positionConversion(PositionConversionReq positionConversionReq);

	/**
	 * Method to user session in cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/session")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Put UserSession")
	RestResponse<GenericResponse> putUserSession(UserSession session);

	/**
	 * Method to square off positions
	 * 
	 * @author Dinesh
	 * @Modified author Nesan
	 * @param orderDetails
	 * @return
	 */
	@Path("/positions/sqroff")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails);
	
	

}

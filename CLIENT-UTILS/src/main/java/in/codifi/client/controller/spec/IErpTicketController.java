package in.codifi.client.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.client.model.request.TicketRiseReq;
import in.codifi.client.model.response.GenericResponse;

public interface IErpTicketController {
	
	/**
	 * Method to Raise ticket
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/raiseTicket")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> raiseTicket(TicketRiseReq req);
	
	/**
	 * Method for preferred
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/preferred")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> preferred(@QueryParam(value = "mobileNo")  String mobileNO);

}

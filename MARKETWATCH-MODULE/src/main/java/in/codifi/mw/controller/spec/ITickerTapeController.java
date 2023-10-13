package in.codifi.mw.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.request.ReqModel;
import in.codifi.mw.model.response.GenericResponse;

public interface ITickerTapeController {
	
	/**
	 * Method to get stocks for client
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/get")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getTicketTapeScrips(ReqModel reqModel);

}

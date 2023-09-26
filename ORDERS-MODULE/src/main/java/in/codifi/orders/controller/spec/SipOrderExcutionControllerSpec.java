package in.codifi.orders.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.orders.model.request.SipOrderDetails;
import in.codifi.orders.model.response.GenericResponse;

public interface SipOrderExcutionControllerSpec {

	/**
	 * Method to execute SIP place orders
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/executeSIP")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<List<GenericResponse>> placeSIPOrder(List<SipOrderDetails> orderDetails);
	
	/**
	 * Method to sip Order Book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/sipOrderBook")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> sipOrderBook();
	
	/**
	 * Method to cancel sip Order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/cancelSipOrder")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> cancelSipOrder(SipOrderDetails orderDetails);

}

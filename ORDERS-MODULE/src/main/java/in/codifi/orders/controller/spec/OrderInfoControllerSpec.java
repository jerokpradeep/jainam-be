package in.codifi.orders.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;

public interface OrderInfoControllerSpec {

	/**
	 * Method to get order book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/orderbook")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get order book ")
	public RestResponse<GenericResponse> getOrderBookInfo();

	/**
	 * Method to get trade book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/tradebook")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get trade book")
	public RestResponse<GenericResponse> getTradeBookInfo();
	
	/**
	 * Method to get Order History
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/history")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get order history")
	public RestResponse<GenericResponse> getOrderHistory(OrderReqModel req);
	
	/**
	 * Method to get Gtd order book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/gtdOrderbook")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Get order book ")
	public RestResponse<GenericResponse> getGtdOrderBookInfo();

}

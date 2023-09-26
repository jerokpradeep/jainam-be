package in.codifi.orders.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;

public interface OrdersControllerSpec {

	/**
	 * Method to place order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */

//	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("/execute")
//	@JsonIncludeProperties
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> placeOrder(OrderReqModel orderReqModel);

	/**
	 * Method to place order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Path("/modify")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> modifyOrder(OrderReqModel orderReqModel);

	/**
	 * Method to cancel order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Path("/cancel")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> cancelOrder(OrderReqModel orderReqModel);

}

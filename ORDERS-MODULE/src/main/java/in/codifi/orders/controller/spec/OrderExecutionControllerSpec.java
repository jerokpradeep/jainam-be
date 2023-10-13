package in.codifi.orders.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.orders.model.request.MarginReqModel;
import in.codifi.orders.model.request.OrderDetails;
import in.codifi.orders.model.response.GenericResponse;

public interface OrderExecutionControllerSpec {

	/**
	 * Method to execute place orders
	 * 
	 * @author Nesan
	 *
	 * @param
	 * @return
	 */
	@Path("/execute")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<List<GenericResponse>> placeOrder(List<OrderDetails> orderDetails);

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
	public RestResponse<GenericResponse> modifyOrder(OrderDetails orderReqModel);
//	public RestResponse<GenericResponse> modifyOrder(OrderReqModel orderReqModel);

	/**
	 * Method to cancel order
	 * 
	 * @author Dinesh
	 * 
	 * @modified author Nesan
	 *
	 * @param orderDetails
	 * @return
	 */
	@Path("/cancel")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<List<GenericResponse>> cancelOrder(List<OrderDetails> orderDetails);

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
	
	/**
	 * Method to square off All positions
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/positions/sqroff/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> positionSquareOffAll();

	/**
	 * Method to execute basket orders
	 * 
	 * @author Dinesh
	 * @modified author Nesan
	 *
	 * @param orderDetails
	 * @return
	 */
	@Path("/execute/basket")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	List<GenericResponse> executeBasketOrder(List<OrderDetails> orderDetails);

	/**
	 * 
	 * Method to Get Order Margin
	 * 
	 * @author Dinesh Kumar
	 * @modified author Nesan
	 *
	 * @param marginReqModel
	 * @return
	 */
	@Path("/getmargin")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getOrderMargin(MarginReqModel marginReqModel);

}

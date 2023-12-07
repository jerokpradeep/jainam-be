package in.codifi.holdings.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.holdings.model.response.GenericResponse;

public interface IHoldingsController {

	/**
	 * Method to get CNC holding data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getHoldings();

	/**
	 * Method to update poa status
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/updatePoa")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getPoa();

	/**
	 * Method to get holdings for MTF product
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/mtf")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getMTFHoldings();

}

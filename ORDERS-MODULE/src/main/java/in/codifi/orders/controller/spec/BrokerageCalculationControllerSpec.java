package in.codifi.orders.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.orders.model.request.BrokerageCalculationReq;
import in.codifi.orders.model.response.GenericResponse;

public interface BrokerageCalculationControllerSpec {
	
	/**
	 * Method to calculate Brokerage
	 * 
	 * @author Gowthaman M
	 * @param BrokerageCalculationReq
	 * @return
	 */
	@Path("/calculateBrokerage")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> calculateBrokerage(BrokerageCalculationReq calculationReq);

}

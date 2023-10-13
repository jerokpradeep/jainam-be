package in.codifi.alerts.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.ws.model.AlertsReqModel;

public interface AlertsControllerSpec {

	/**
	 * method to create Alert
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createAlert(AlertsReqModel reqModel);

	/**
	 * method to update Trigger Status
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	@Path("/updateTriggerStatus")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> updateTriggerStatus(@QueryParam("alertId") int alertId);

}

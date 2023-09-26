package in.codifi.alerts.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.model.ModifyAlertsReqModel;

public interface AlertsOdinControllerSpec {

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/get")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getAlerts();

	/**
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createAlerts(AlertsReqModel req);

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @return
	 */
	@Path("/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> updateAlerts(ModifyAlertsReqModel req);

	/**
	 * Method to delete alert
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param alertId
	 * @return
	 */
	@Path("/delete/{alertId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteAlert(@QueryParam("alertId") String alertId);

}

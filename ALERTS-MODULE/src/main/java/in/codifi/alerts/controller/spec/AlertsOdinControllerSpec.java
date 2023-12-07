package in.codifi.alerts.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.request.RequestModel;
import in.codifi.alerts.model.response.GenericResponse;

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
	RestResponse<GenericResponse> createAlerts(RequestModel req);

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
	RestResponse<GenericResponse> updateAlerts(RequestModel req);

	/**
	 * Method to delete alert
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param alertId
	 * @return
	 */
	@Path("/delete/{id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteAlert(@PathParam("id") String alertId);

}

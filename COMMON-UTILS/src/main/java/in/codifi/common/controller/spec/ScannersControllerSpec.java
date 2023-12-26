package in.codifi.common.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.model.response.GenericResponse;

public interface ScannersControllerSpec {
	/**
	 * Method to get Scanners
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/details")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getScannersDetails();

}

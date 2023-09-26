package in.codifi.ameyo.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.ameyo.model.request.AuthReq;
import in.codifi.ameyo.model.request.LogoutReq;
import in.codifi.ameyo.model.response.GenericResponse;

public interface AmeyoAuthControllerSpec {
	
	/**
	 * Method to get token from chola key cloak 
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param authmodel
	 * @return
	 */
	@Path("/empAuthToken")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getEmpAuthToken(AuthReq authmodel);
	
	/**
	 * Method to employee logout 
	 * 
	 * @author Gowthaman
	 * @created on 21-Sep-2023
	 * @param authmodel
	 * @return
	 */
	@Path("/empLogout")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> empLogout(LogoutReq req);
}

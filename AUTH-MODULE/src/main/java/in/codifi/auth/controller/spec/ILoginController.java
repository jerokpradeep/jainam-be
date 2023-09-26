package in.codifi.auth.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.request.LoginReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.orders.ws.model.OdinSsoModel;

public interface ILoginController {

	/**
	 * Method to login
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> login(LoginReq req);

	/**
	 * Method to sent otp
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/otp")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> loginOTP(LoginReq req);

	/**
	 * Service to verfify our token from ODIN
	 * 
	 * @author Nesan
	 * @return
	 */
	@Path("/verifytoken")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<OdinSsoModel> verifyToken(@QueryParam("token") String token);

	/**
	 * Method to login with sso
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	@Path("/login/sso")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> ssoLogin(LoginReq req);

}

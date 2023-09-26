package in.codifi.auth.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.controller.spec.ILoginController;
import in.codifi.auth.model.request.LoginReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.service.spec.ILoginService;
import in.codifi.orders.ws.model.OdinSsoModel;

@Path("/access")
public class LoginController implements ILoginController {

	@Inject
	ILoginService loginService;

	/**
	 * Method to login
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> login(LoginReq req) {
		return loginService.login(req);
	}

	/**
	 * Method to sent otp
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loginOTP(LoginReq req) {
		return loginService.loginOTP(req);
	}

	/**
	 * Service to verify our token from ODIN
	 * 
	 * @author Nesan
	 * @return
	 */
	@Override
	public RestResponse<OdinSsoModel> verifyToken(String token) {

		return loginService.verifyToken(token);
	}
	
	/**
	 * Method to login with sso
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> ssoLogin(LoginReq req) {
		return loginService.ssoLogin(req);
	}

}

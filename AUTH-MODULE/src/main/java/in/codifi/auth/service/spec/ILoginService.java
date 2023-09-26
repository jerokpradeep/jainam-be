package in.codifi.auth.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.request.LoginReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.orders.ws.model.OdinSsoModel;

public interface ILoginService {

	/**
	 * method to login
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	RestResponse<GenericResponse> login(LoginReq req);

	/**
	 * Method to sent otp
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> loginOTP(LoginReq req);

	/**
	 * Method to verfify our token from ODIN req
	 * 
	 * @author Nesan
	 * @return
	 */
	RestResponse<OdinSsoModel> verifyToken(String token);

	/**
	 * method to loginwith sso
	 * 
	 * @param req
	 * @return
	 */
	RestResponse<GenericResponse> ssoLogin(LoginReq req);

}

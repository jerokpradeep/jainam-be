package in.codifi.ameyo.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.ameyo.model.request.AuthReq;

import in.codifi.ameyo.model.request.LogoutReq;

import in.codifi.ameyo.model.response.GenericResponse;

public interface AmeyoAuthServiceSpec {
	
	/**
	 * Method to get token from chola key cloak 
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> getEmpAuthToken(AuthReq authmodel);
	
	/**
	 * Method to employee logout 
	 * 
	 * @author Gowthaman
	 * @created on 21-Sep-2023
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> empLogout(LogoutReq req);

}

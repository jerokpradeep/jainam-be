package in.codifi.ameyo.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.ameyo.controller.spec.AmeyoAuthControllerSpec;
import in.codifi.ameyo.model.request.AuthReq;
import in.codifi.ameyo.model.request.LogoutReq;
import in.codifi.ameyo.model.response.GenericResponse;
import in.codifi.ameyo.service.spec.AmeyoAuthServiceSpec;
import in.codifi.ameyo.utility.AppConstants;
import in.codifi.ameyo.utility.PrepareResponse;
import in.codifi.ameyo.utility.StringUtil;

@Path("/empAuth")
public class AmeyoAuthController implements AmeyoAuthControllerSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AmeyoAuthServiceSpec authServiceSpec;

	/**
	 * Method to get token from chola key cloak
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param authmodel
	 * @return
	 */
	public RestResponse<GenericResponse> getEmpAuthToken(AuthReq authReq) {
		if (StringUtil.isNullOrEmpty(authReq.getUserId()) || StringUtil.isNullOrEmpty(authReq.getPassword())
				|| StringUtil.isNullOrEmpty(authReq.getVendor()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.getEmpAuthToken(authReq);
	}

	/**
	 * Method to employee logout
	 * 
	 * @author Gowthaman
	 * @created on 21-Sep-2023
	 * @param authmodel
	 * @return
	 */
	public RestResponse<GenericResponse> empLogout(LogoutReq req) {
		if (StringUtil.isNullOrEmpty(req.getUserId()) && StringUtil.isNullOrEmpty(req.getAccessToken()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.empLogout(req);
	}

}

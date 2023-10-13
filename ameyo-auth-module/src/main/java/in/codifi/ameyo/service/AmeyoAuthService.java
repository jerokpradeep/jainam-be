package in.codifi.ameyo.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.esotericsoftware.minlog.Log;

import in.codifi.ameyo.config.HazelcastConfig;
import in.codifi.ameyo.model.request.AuthReq;
import in.codifi.ameyo.model.request.LogoutReq;
import in.codifi.ameyo.model.response.EmpAmeyoResponse;
import in.codifi.ameyo.model.response.EmpAuthResponse;
import in.codifi.ameyo.model.response.GenericResponse;
import in.codifi.ameyo.service.spec.AmeyoAuthServiceSpec;
import in.codifi.ameyo.utility.AppConstants;
import in.codifi.ameyo.utility.PrepareResponse;
import in.codifi.ameyo.ws.model.kc.GetTokenResponse;
import in.codifi.ameyo.ws.service.ErpRestService;
import in.codifi.ameyo.ws.service.KcAmeyoTokenRest;

@ApplicationScoped
public class AmeyoAuthService implements AmeyoAuthServiceSpec {

	@Inject
	KcAmeyoTokenRest kcTokenRest;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ErpRestService erpRestService;

	/**
	 * Method to get token from chola key cloak
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getEmpAuthToken(AuthReq authmodel) {
		EmpAuthResponse response = new EmpAuthResponse();
		EmpAmeyoResponse ameyoResponse = new EmpAmeyoResponse();
		try {
			GetTokenResponse kcTokenResp = kcTokenRest.getToken(authmodel);
			if (kcTokenResp != null) {
				String key = authmodel.getUserId();
				HazelcastConfig.getInstance().getKeycloakSession().remove(key);
				HazelcastConfig.getInstance().getKeycloakSession().put(key, kcTokenResp);
				if (authmodel.getVendor().equalsIgnoreCase("AMEYO")) {
					ameyoResponse.setAccessToken(kcTokenResp.getAccessToken());
					return prepareResponse.prepareSuccessResponseObject(ameyoResponse);
				} else if (authmodel.getVendor().equalsIgnoreCase("ERP")) {

					String url = AppConstants.EMP_RE_DIRECT_URL + authmodel.getUserId();

					String erpResp = erpRestService.getErpAuthorization(url, authmodel.getUserId());

					response.setRedirectUrl(AppConstants.EMP_RE_DIRECT_URL + authmodel.getUserId());
					return prepareResponse.prepareSuccessResponseObject(erpResp);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_VENDOR);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Sso get token -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to employee logout
	 * 
	 * @author Gowthaman
	 * @created on 21-Sep-2023
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> empLogout(LogoutReq req) {
		try {
			String key = req.getUserId();
			GetTokenResponse logoutKeys = HazelcastConfig.getInstance().getKeycloakSession().get(key);
			if (logoutKeys != null) {
				if (!req.getAccessToken().equalsIgnoreCase(logoutKeys.getAccessToken())) {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_USER_SESSION);
				}
				String token = logoutKeys.getAccessToken();
				String refreshToken = logoutKeys.getRefreshToken();
				kcTokenRest.empLogout(token, refreshToken, key);
				HazelcastConfig.getInstance().getKeycloakSession().remove(key);
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("emp Logout -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

}

package in.codifi.sso.auth.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.auth.ws.model.kc.GetIntroSpectResponse;
import in.codifi.auth.ws.model.kc.GetTokenResponse;
import in.codifi.auth.ws.model.kc.GetTokenRestReqModel;
import in.codifi.sso.auth.config.HazelcastConfig;
import in.codifi.sso.auth.model.request.GetAccessTokenReqModel;
import in.codifi.sso.auth.service.spec.SsoServiceSpec;
import in.codifi.sso.auth.utility.AppConstants;
import in.codifi.sso.auth.utility.StringUtil;
import in.codifi.sso.auth.ws.service.KcTokenRest;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SsoService implements SsoServiceSpec {

	@Inject
	KcTokenRest kcTokenRest;

	/**
	 * Method to get access token from keycloak
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public Object getAccessToken(String client, String clientSecret, String code, String grantType,
			String redirectUri) {
		GetTokenResponse tokenResponse = new GetTokenResponse();
		if (StringUtil.isNullOrEmpty(code) || StringUtil.isNullOrEmpty(client)) {

			tokenResponse.setError("invalid_request");
			tokenResponse.setErrorDescription("Missing form parameter");
			return tokenResponse;
		}
		System.out.println("AuthCode" + code);

		if (StringUtil.isNotNullOrEmpty(HazelcastConfig.getInstance().getVendorAuthCode().get(code))) {

			String vendoeDetails = HazelcastConfig.getInstance().getVendorAuthCode().get(code);
			if (vendoeDetails == null || vendoeDetails.isBlank() || vendoeDetails.isEmpty()) {
				vendoeDetails = HazelcastConfig.getInstance().getVendorAuthCode().get(code);
			}
			String[] details = vendoeDetails.split("_");
			String clientId = details[0];
			String vendorName = details[1];
			System.out.println("clientId in getUserDetails -" + clientId);
			System.out.println("vendorName in getUserDetails -" + vendorName);
			tokenResponse = getStringUserSessionIdNew(clientId, vendorName);
			if (tokenResponse == null) {
				tokenResponse = new GetTokenResponse();
				tokenResponse.setError("Internal Server Error");
				tokenResponse.setErrorDescription("An error occurred while processing the request.");
				return tokenResponse;
			}
		} else {
			tokenResponse.setError("invalid_request");
			tokenResponse.setErrorDescription("Invalid Auth");
			return tokenResponse;
		}

		return tokenResponse;
	}

	/**
	 * Method to get access token from keycloak
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public Object getUserToken(GetAccessTokenReqModel model) {
		GetTokenResponse tokenResponse = new GetTokenResponse();
		if (model == null || model.getData() == null || StringUtil.isNullOrEmpty(model.getData().getCode())
				|| StringUtil.isNullOrEmpty(model.getData().getClient_id())) {

			tokenResponse.setError("invalid_request");
			tokenResponse.setErrorDescription("Missing form parameter");
			return tokenResponse;
		}
		System.out.println("AuthCode" + model.getData().getCode());

		if (StringUtil
				.isNotNullOrEmpty(HazelcastConfig.getInstance().getVendorAuthCode().get(model.getData().getCode()))) {

			String vendoeDetails = HazelcastConfig.getInstance().getVendorAuthCode().get(model.getData().getCode());
			if (vendoeDetails == null || vendoeDetails.isBlank() || vendoeDetails.isEmpty()) {
				vendoeDetails = HazelcastConfig.getInstance().getVendorAuthCode().get(model.getData().getCode());
			}
			String[] details = vendoeDetails.split("_");
			String clientId = details[0];
			String vendorName = details[1];
			System.out.println("clientId in getUserDetails -" + clientId);
			System.out.println("vendorName in getUserDetails -" + vendorName);
			tokenResponse = getStringUserSessionIdNew(clientId, vendorName);
			if (tokenResponse == null) {
				tokenResponse = new GetTokenResponse();
				tokenResponse.setError("Internal Server Error");
				tokenResponse.setErrorDescription("An error occurred while processing the request.");
				return tokenResponse;
			}
		} else {
			tokenResponse.setError("invalid_request");
			tokenResponse.setErrorDescription("Invalid Auth");
			return tokenResponse;
		}

		return tokenResponse;
	}

	/**
	 * Method to generate the new access token
	 * 
	 * @author Gowrisankar
	 * @param pUserId
	 * @return
	 */
	private GetTokenResponse getStringUserSessionIdNew(String pUserId, String appName) {
		GetTokenResponse accessToken = null;
		try {
			GetTokenRestReqModel authmodel = new GetTokenRestReqModel();
			authmodel.setUserId(pUserId);
			accessToken = keyCloakLoginByRefershToken(authmodel, appName);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return accessToken;
	}

	/**
	 * KC login while logging with vendor
	 * 
	 * @author Gowrisankar
	 * @param authmodel
	 * @return
	 */
	public GetTokenResponse keyCloakLoginByRefershToken(GetTokenRestReqModel authmodel, String appName) {
		GetTokenResponse kcTokenResp = null;
		try {

			String hazelKeySso = authmodel.getUserId() + "_" + AppConstants.SOURCE_SSO;

			if (HazelcastConfig.getInstance().getSsoKeycloakSession().get(hazelKeySso) != null) {
				GetTokenResponse ssoMasterSession = HazelcastConfig.getInstance().getSsoKeycloakSession()
						.get(hazelKeySso);
				if (StringUtil.isNullOrEmpty(ssoMasterSession.getRefreshToken())) {
					Log.error("KC-LoginByRefershToken - Refresh Token is null");
					return null;
				}

				kcTokenResp = kcTokenRest.fetchTokenByRefereshToken(ssoMasterSession.getRefreshToken());

				if (kcTokenResp != null) {

					/** Return if failed to login on key clock **/
					if (StringUtil.isNotNullOrEmpty(kcTokenResp.getError())) {
						Log.error(kcTokenResp.getErrorDescription());
						return null;
					}

					if (StringUtil.isNotNullOrEmpty(kcTokenResp.getAccessToken())) {

						/** To get user roles by requesting user Introspect API **/
						GetIntroSpectResponse introSpectResponse = kcTokenRest.getIntroSpect(authmodel.getUserId(),
								kcTokenResp.getAccessToken());
						if (introSpectResponse != null && introSpectResponse.getClientRoles() != null
								&& introSpectResponse.getActive() != null) {

							if (!introSpectResponse.getActive()) {
								Log.error("KC-Login - " + AppConstants.USER_BLOCKED);
								return null;
							}

							/** Add new session into Distributed Cache **/
							String hazelKeySsoVendor = authmodel.getUserId() + "_" + AppConstants.SOURCE_SSO + appName;
							System.out.println("hazelKeySsoVendor -" + hazelKeySsoVendor);
							HazelcastConfig.getInstance().getKeycloakSession().put(hazelKeySsoVendor, kcTokenResp);
							HazelcastConfig.getInstance().getKeycloakUserInfo().put(hazelKeySsoVendor,
									introSpectResponse);
							System.out.println("New Token -" + kcTokenResp.getAccessToken());
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return kcTokenResp;
	}

	/**
	 * Method to get access user info from keycloak
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public Object getInfo(String userId, String token) {

		Object resp = kcTokenRest.getIntroSpect(userId, token);
		return resp;
	}
}

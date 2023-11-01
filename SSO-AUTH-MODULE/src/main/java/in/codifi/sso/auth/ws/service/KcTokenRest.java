package in.codifi.sso.auth.ws.service;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.auth.ws.model.kc.GetIntroSpectResponse;
import in.codifi.auth.ws.model.kc.GetTokenResponse;
import in.codifi.auth.ws.model.kc.GetTokenRestReqModel;
import in.codifi.sso.auth.config.KeyCloakConfig;
import in.codifi.sso.auth.ws.service.spec.KcTokenRestSpec;

@ApplicationScoped
public class KcTokenRest {

	@Inject
	@RestClient
	KcTokenRestSpec tokenService;

	@Inject
	KeyCloakConfig props;

	/**
	 * 
	 * Method to get token from keycloak
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public GetTokenResponse getToken(GetTokenRestReqModel authmodel) {
		try {
			String clientId = props.getClientId();
			String clientSecret = props.getClientSecret();
			String grantType = props.getGrantType();
			String userId = authmodel.getUserId();
			String password = authmodel.getPassword();

			Response tokenResp = tokenService.fetchToken(clientId, clientSecret, grantType, userId, password);

			if (tokenResp.getStatus() == 200) {
				GetTokenResponse getTokenResponse = tokenResp.readEntity(GetTokenResponse.class);
				return getTokenResponse;
			} else {
				Map<String, Object> errorResponse = tokenResp.readEntity(new GenericType<Map<String, Object>>() {
				});

				GetTokenResponse errorTokenResponse = new GetTokenResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());

				return errorTokenResponse;
			}
		} catch (ClientWebApplicationException ex) {

			String responseBody = ex.getResponse().readEntity(String.class);
			try {
				Map<String, Object> errorResponse = new ObjectMapper().readValue(responseBody,
						new TypeReference<Map<String, Object>>() {
						});

				GetTokenResponse errorTokenResponse = new GetTokenResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());
				return errorTokenResponse;
			} catch (IOException e) {
				// Handle JSON parsing error
			}
			GetTokenResponse errorResponse = new GetTokenResponse();
			errorResponse.setError("Internal Server Error");
			errorResponse.setErrorDescription("An error occurred while processing the request.");
			return errorResponse;
		}
	}

	/**
	 * 
	 * Method to get token from keycloak
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param token
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public GetTokenResponse fetchTokenByRefereshToken(String token) {
		try {
			String clientId = props.getClientId();
			String clientSecret = props.getClientSecret();
			String grantType = props.getGrandTypeRefreshToken();

			Response tokenResp = tokenService.fetchTokenByRefereshToken(clientId, clientSecret, grantType, token);

			if (tokenResp.getStatus() == 200) {
				GetTokenResponse getTokenResponse = tokenResp.readEntity(GetTokenResponse.class);
				return getTokenResponse;
			} else {
				Map<String, Object> errorResponse = tokenResp.readEntity(new GenericType<Map<String, Object>>() {
				});

				GetTokenResponse errorTokenResponse = new GetTokenResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());

				return errorTokenResponse;
			}
		} catch (ClientWebApplicationException ex) {

			String responseBody = ex.getResponse().readEntity(String.class);
			try {
				Map<String, Object> errorResponse = new ObjectMapper().readValue(responseBody,
						new TypeReference<Map<String, Object>>() {
						});

				GetTokenResponse errorTokenResponse = new GetTokenResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());
				return errorTokenResponse;
			} catch (IOException e) {
				// Handle JSON parsing error
			}
			GetTokenResponse errorResponse = new GetTokenResponse();
			errorResponse.setError("Internal Server Error");
			errorResponse.setErrorDescription("An error occurred while processing the request.");
			return errorResponse;
		}
	}

	/**
	 * 
	 * Method to get Introspect for user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @param token
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public GetIntroSpectResponse getIntroSpect(String userId, String token)
			throws ClientWebApplicationException {
		try {
			String clientId = props.getClientId();
			String clientSecret = props.getClientSecret();
			Response resp = tokenService.getIntroSpect(userId, clientId, clientSecret, token);
			if (resp.getStatus() == 200) {
				GetIntroSpectResponse getTokenResponse = resp.readEntity(GetIntroSpectResponse.class);
				return getTokenResponse;
			} else {
				Map<String, Object> errorResponse = resp.readEntity(new GenericType<Map<String, Object>>() {
				});

				GetIntroSpectResponse errorTokenResponse = new GetIntroSpectResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());

				return errorTokenResponse;
			}
		} catch (ClientWebApplicationException ex) {

			String responseBody = ex.getResponse().readEntity(String.class);
			try {
				Map<String, Object> errorResponse = new ObjectMapper().readValue(responseBody,
						new TypeReference<Map<String, Object>>() {
						});

				GetIntroSpectResponse errorTokenResponse = new GetIntroSpectResponse();
				errorTokenResponse.setError(errorResponse.get("error").toString());
				errorTokenResponse.setErrorDescription(errorResponse.get("error_description").toString());
				return errorTokenResponse;
			} catch (IOException e) {
				// Handle JSON parsing error
			}
			GetIntroSpectResponse errorResponse = new GetIntroSpectResponse();
			errorResponse.setError("Internal Server Error");
			errorResponse.setErrorDescription("An error occurred while processing the request.");
			return errorResponse;
		}
	}

}

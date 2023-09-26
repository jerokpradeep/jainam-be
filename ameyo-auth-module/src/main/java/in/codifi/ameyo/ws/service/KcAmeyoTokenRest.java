package in.codifi.ameyo.ws.service;

import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.esotericsoftware.minlog.Log;

import in.codifi.ameyo.config.KeyCloakConfig;
import in.codifi.ameyo.model.request.AuthReq;
import in.codifi.ameyo.ws.model.kc.GetTokenResponse;
import in.codifi.ameyo.ws.service.spec.KcAmeyoTokenRestSpec;

@ApplicationScoped
public class KcAmeyoTokenRest {

	@Inject
	@RestClient
	KcAmeyoTokenRestSpec tokenService;
	@Inject
	KeyCloakConfig props;

	/**
	 * Method to get employee token
	 * 
	 * @author Gowthaman
	 * @created on 20-Sep-2023
	 * @param authmodel
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public GetTokenResponse getToken(AuthReq authmodel) throws ClientWebApplicationException {
		GetTokenResponse tokenDetail = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(authmodel.getPassword());
			String pw = new String(decodedBytes);
			System.out.println("Decoded password -- " + pw);
			String clientId = props.getAmeyoClientId();
			String clientSecret = props.getAmeyoClientSecret();
			String grantType = props.getAmeyoGrantType();
			String userId = authmodel.getUserId();
			String password = pw;

			tokenDetail = tokenService.fetchToken(clientId, clientSecret, grantType, userId, password);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Log fetch Token -- " + e.getMessage());
		}
		return tokenDetail;
	}

	/**
	 * Method to employee logout
	 * 
	 * @author Gowthaman
	 * @created on 21-Sep-2023
	 * @param token
	 * @param refreshToken
	 * @param userId
	 * @throws ClientWebApplicationException
	 */
	public void empLogout(String token, String refreshToken, String userId) throws ClientWebApplicationException {
		String clientId = props.getAmeyoClientId();
		String clientSecret = props.getAmeyoClientSecret();

		tokenService.logout(token, clientId, clientSecret, refreshToken, userId);

	}

}

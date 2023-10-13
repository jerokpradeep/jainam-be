package in.codifi.odn.service;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.google.inject.Inject;

import in.codifi.odn.config.ApplicationProperties;
import in.codifi.odn.config.HazelcastConfig;
import in.codifi.odn.model.AuthRequest;
import in.codifi.odn.model.AuthResponse;
import in.codifi.odn.model.RegisterApiReq;
import in.codifi.odn.model.RegisterApiResp;
import in.codifi.odn.service.spec.AuthenticationServiceSpec;
import in.codifi.odn.service.spec.RestServiceClient;

@ApplicationScoped
public class AuthenticationService implements AuthenticationServiceSpec {

	@Inject
	ApplicationProperties properties;
	
	@Inject
	@RestClient
	RestServiceClient restServiceClient;

	@Override
	public AuthResponse getAuthResponse(AuthRequest request) {
		return restServiceClient.getAuthResponse(request);
	}

	@Override
	public RegisterApiResp registerApi(RegisterApiReq request) {
		return restServiceClient.registerApi(request);
	}

	@Override
	public String getWsEndPoint() {
		RegisterApiResp registerApi = registerApi(buildRegisterRequest());
		return registerApi.getJWebSocketDetail();
	}

	private RegisterApiReq buildRegisterRequest() {
		return RegisterApiReq.builder().jConnMode(2).jRequestID("CH-" + System.currentTimeMillis())
				.jToken(getAuthToken()).build();
	}

	public AuthenticationService(ApplicationProperties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public String getAuthToken() {
		if (StringUtils.isEmpty(HazelcastConfig.getInstance().getWebSocketJtoken().get(this.properties.getApiKey()))) {
			AuthResponse resp = getAuthResponse(buildAuthRequest());
			return resp.getJToken();
		}
		return HazelcastConfig.getInstance().getWebSocketJtoken().get(this.properties.getApiKey());
	}

	private AuthRequest buildAuthRequest() {
		return AuthRequest.builder().jRequestID("CH-" + System.currentTimeMillis())
				.jSecretKey(properties.getJSecretKey()).jAPIKey(properties.getApiKey()).build();
	}

}

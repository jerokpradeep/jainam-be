package in.codifi.auth.config;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import in.codifi.auth.model.response.UsersLoggedInRespModel;
import in.codifi.auth.ws.model.kc.GetIntroSpectResponse;
import in.codifi.auth.ws.model.kc.GetTokenResponse;
import in.codifi.auth.ws.model.kc.GetUserInfoResp;
import in.codifi.auth.ws.model.login.LoginRestResp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HazelcastConfig {

	public static HazelcastConfig HazelcastConfig = null;
	private HazelcastInstance hz = null;

	public static HazelcastConfig getInstance() {
		if (HazelcastConfig == null) {
			HazelcastConfig = new HazelcastConfig();

		}
		return HazelcastConfig;
	}

	public HazelcastInstance getHz() {
		if (hz == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setClusterName(ConfigProvider.getConfig().getValue("config.app.hazel.cluster", String.class));
			clientConfig.getNetworkConfig()
					.addAddress(ConfigProvider.getConfig().getValue("config.app.hazel.address", String.class));
			hz = HazelcastClient.newHazelcastClient(clientConfig);
		}
		return hz;
	}

	private Map<String, String> userSessionOtp = getHz().getMap("userSessionOtp");
	private Map<String, GetIntroSpectResponse> keycloakMedianUserInfo = getHz().getMap("kcMedianUserInfo");
	private Map<String, GetTokenResponse> keycloakMedianSession = getHz().getMap("keycloakMedianSession");
	private Map<String, GetTokenResponse> keycloakSession = getHz().getMap("keycloakSession");
	private Map<String, GetIntroSpectResponse> keycloakUserInfo = getHz().getMap("keycloakUserInfo");
	private Map<String, List<GetUserInfoResp>> keycloakUserDetails = getHz().getMap("keycloakUserDetails");
	private Map<String, String> keycloakAdminSession = getHz().getMap("keycloakAdminSession");

	private Map<String, String> restUserSession = getHz().getMap("restUserSession");
	private Map<String, String> restUserSsoToken = getHz().getMap("restUserSsoTomen");
	private Map<String, Boolean> isRestUserSessionActive = getHz().getMap("isRestUserSessionActive");
	private Map<String, LoginRestResp> userSessionDetails = getHz().getMap("userSessionDetails");
	private Map<String, String> twoFAUserPreference = getHz().getMap("twoFAUserPreference");

	IMap<String, String> resendOtp = getHz().getMap("resendOtp"); // Expire - 30 seconds
	IMap<String, String> otp = getHz().getMap("otp"); // Expire - 5 minutes
	IMap<String, Integer> retryOtpCount = getHz().getMap("otp");
	IMap<String, Boolean> holdResendOtp = getHz().getMap("otp");
	IMap<String, Integer> passwordRetryCount = getHz().getMap("passwordRetryCount");

	private Map<String, String> restApiClinetInfo = getHz().getMap("restApiClinetInfo");

	private Map<String, String> vendorAuthCode = getHz().getMap("vendorAuthCode");

	private Map<String, UsersLoggedInRespModel> webLoggedInUsers = getHz().getMap("webLoggedInUsers");
	private Map<String, UsersLoggedInRespModel> mobLoggedInUsers = getHz().getMap("webLoggedInUsers");
	private Map<String, UsersLoggedInRespModel> apiLoggedInUsers = getHz().getMap("webLoggedInUsers");
	private Map<String, UsersLoggedInRespModel> ssoLoggedInUsers = getHz().getMap("webLoggedInUsers");
	
	private Map<String, String> logResponseModel = getHz().getMap("logResponseModel");

}

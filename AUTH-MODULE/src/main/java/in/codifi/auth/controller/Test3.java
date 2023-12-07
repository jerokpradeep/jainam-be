package in.codifi.auth.controller;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class Test3 {

//	private final String serverUrl = "https://idaas.jainam.in/idaas/realms/Jainam";
//	private final String realm = "Jainam";
//	private final String clientId = "jainam";
//	private final String clientSecret = "0vBv6lrv3L6Oh3a78zJTW8tJ9J3H8arz";
//	private final ObjectMapper objectMapper = new ObjectMapper();
//
//	public String impersonate(String userKeycloakId) throws IOException {
//		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
//			String tokenExchangeUrl = serverUrl + "/protocol/openid-connect/token";
////			Log.info("Impersonate URL: {}", tokenExchangeUrl);
//
//			// Prepare the HTTP request for token exchange
//			RequestBuilder requestBuilder = RequestBuilder.post(tokenExchangeUrl)
//					.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
//					.addParameter("client_id", clientId).addParameter("client_secret", clientSecret)
//					.addParameter("grant_type", "client_credentials")
//					.addParameter("subject_token", getKeycloak().tokenManager().getAccessTokenString())
//					.addParameter("requested_subject", userKeycloakId);
//
//			// Execute the HTTP request
//			HttpResponse response = httpClient.execute(requestBuilder.build());
//
//			// Handle the response
//			if (response.getStatusLine().getStatusCode() == 200) {
//				String responseBody = new BufferedReader(
//						new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
//						.collect(Collectors.joining("\n"));
//
//				AccessTokenResponse token = objectMapper.readValue(responseBody, AccessTokenResponse.class);
////				Log.infof("Impersonate token: {}", token);
//
//				// Return the token or handle it as needed
//				return responseBody;
//			} else {
////				Log.infof("Impersonate error: {}", response.getStatusLine().getStatusCode());
//				// Handle the error response
//				return null;
//			}
//		}
//	}
//
//	private Keycloak getKeycloak() {
//		return KeycloakBuilder.builder().serverUrl(serverUrl).realm(realm).grantType("client_credentials").clientId(clientId)
//				.clientSecret(clientSecret).build();
//
//	}
//}

	private final String serverUrl = "https://idaas.jainam.in/idaas/realms/Jainam";
	private final String realm = "Jainam";
	private final String clientId = "jainam";
	private final String clientSecret = "0vBv6lrv3L6Oh3a78zJTW8tJ9J3H8arz";

	public String impersonate(String userKeycloakId) throws IOException {
		String tokenExchangeUrl = serverUrl + "/protocol/openid-connect/token";

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpPost httpPost = new HttpPost(tokenExchangeUrl);
			httpPost.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			httpPost.addHeader("Authorization", "Basic " + getBase64ClientIdSecret());

			RequestBuilder requestBuilder = RequestBuilder.post(tokenExchangeUrl)
					.addParameter("grant_type", "client_credentials")
					.addParameter("subject_token", getKeycloak().tokenManager().getAccessTokenString())
					.addParameter("requested_subject", userKeycloakId);

			httpPost.setEntity(((HttpResponse) requestBuilder.build()).getEntity());

			// Execute the HTTP request
			HttpResponse response = httpClient.execute(httpPost);

			// Handle the response
			if (response.getStatusLine().getStatusCode() == 200) {
				String responseBody = EntityUtils.toString(response.getEntity());
				return responseBody;
			} else {
				// Handle the error response
				return null;
			}
		}
	}

	private String getBase64ClientIdSecret() {
		String credentials = clientId + ":" + clientSecret;
		return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
	}

	// Assuming you have a method similar to this for obtaining the Keycloak
	// instance
	private Keycloak getKeycloak() {
		return KeycloakBuilder.builder().serverUrl(serverUrl).realm(realm).grantType("client_credentials")
				.clientId(clientId).clientSecret(clientSecret).build();
	}
}

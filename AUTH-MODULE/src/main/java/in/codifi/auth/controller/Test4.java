package in.codifi.auth.controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Test4 {

    public static void main(String[] args) throws Exception {
        String keycloakBaseUrl = "https://idaas.jainam.in/idaas/realms/Jainam";
        String clientId = "jainam";
        String clientSecret = "0vBv6lrv3L6Oh3a78zJTW8tJ9J3H8arz";
        String redirectUri = "http://localhost:80/*";
        String code = "authorization-code";  // The code received from the authorization endpoint

        // Step 1: Exchange Authorization Code for Tokens
        String tokenEndpoint = keycloakBaseUrl + "/protocol/openid-connect/token";
        String tokenRequestBody =
                "grant_type=" + URLEncoder.encode("authorization_code", "UTF-8") +
                "&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
                "&code=" + URLEncoder.encode(code, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");

        String tokenResponse = sendPostRequest(tokenEndpoint, tokenRequestBody);
        System.out.println("Token Response:\n" + tokenResponse);

        // Parse the access token and refresh token from the token response (JSON parsing is recommended)

        // Step 2: Use Refresh Token (if needed)
        String refreshToken = "refresh-token";  // Replace with the actual refresh token
        if (refreshToken != null && !refreshToken.isEmpty()) {
            String refreshEndpoint = keycloakBaseUrl + "/protocol/openid-connect/token";
            String refreshRequestBody =
                    "grant_type=" + URLEncoder.encode("refresh_token", "UTF-8") +
                    "&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                    "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
                    "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8");

            String refreshedTokenResponse = sendPostRequest(refreshEndpoint, refreshRequestBody);
            System.out.println("Refreshed Token Response:\n" + refreshedTokenResponse);
        }
    }

    private static String sendPostRequest(String endpoint, String requestBody) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.getOutputStream().write(requestBody.getBytes("UTF-8"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}


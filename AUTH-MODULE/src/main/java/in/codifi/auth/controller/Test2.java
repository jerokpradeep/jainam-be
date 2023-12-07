package in.codifi.auth.controller;

import java.io.IOException;

public class Test2 {

	public static void main(String[] args) {
		try {
			// Replace these values with your actual Keycloak configuration
			String userKeycloakId = "j33";

			Test3 keycloakImpersonation = new Test3();
			String tokenResponse = keycloakImpersonation.impersonate(userKeycloakId);

			// Do something with the token response, e.g., print it
			System.out.println("Token Response:\n" + tokenResponse);
		} catch (IOException e) {
			// Handle IO exception
			e.printStackTrace();
		}
	}
}

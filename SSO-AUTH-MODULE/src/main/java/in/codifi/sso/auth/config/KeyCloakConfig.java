package in.codifi.sso.auth.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
@Getter
@Setter
public class KeyCloakConfig {

	@ConfigProperty(name = "quarkus.oidc.auth-server-url")
	private String authServerUrl;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	private String clientId;

	@ConfigProperty(name = "quarkus.oidc.credentials.secret")
	private String clientSecret;

	@ConfigProperty(name = "auth.org.grant.type")
	private String grantType;
	
	@ConfigProperty(name = "auth.org.grant.type.refresh.token")
	private String grandTypeRefreshToken;
	

}

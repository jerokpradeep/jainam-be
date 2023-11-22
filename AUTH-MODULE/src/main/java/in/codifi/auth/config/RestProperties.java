package in.codifi.auth.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
@Getter
@Setter
public class RestProperties {

	@ConfigProperty(name = "appconfig.odin.url.base")
	private String baseUrl;

	@ConfigProperty(name = "appconfig.odin.url.login")
	private String login;
	
	@ConfigProperty(name = "appconfig.odin.url.loginOTP")
	private String loginOTP;
	
	@ConfigProperty(name = "appconfig.odin.xAuthKeyValue")
	private String xAuthKeyValue;
	
	@ConfigProperty(name = "appconfig.odin.apiKey")
	private String apiKey;
	
	@ConfigProperty(name = "appconfig.odin.xApiKey")
	private String xApiKey;
	
	@ConfigProperty(name = "appconfig.odin.url.logout")
	private String logout;

}

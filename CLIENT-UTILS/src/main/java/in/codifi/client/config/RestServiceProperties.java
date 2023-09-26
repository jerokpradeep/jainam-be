package in.codifi.client.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {
	
	@ConfigProperty(name = "config.odin.url.clientdetails")
	private String clientDetails;

	@ConfigProperty(name = "config.odin.url.createsession")
	private String wsCreateSession;

	@ConfigProperty(name = "config.odin.url.invalidatesession")
	private String wsInvalidateSession;
	
	@ConfigProperty(name = "config.odin.url.userProfile")
	private String userProfile;
	
	@ConfigProperty(name = "appconfig.odin.url.xapikey")
	private String xApiKey;

}

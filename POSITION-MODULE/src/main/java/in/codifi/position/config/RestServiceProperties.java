package in.codifi.position.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {

	@ConfigProperty(name = "appconfig.odin.url.base")
	private String baseUrl;

	@ConfigProperty(name = "appconfig.odin.url.position")
	private String positionUrl;

	@ConfigProperty(name = "appconfig.odin.url.positionconversion")
	private String conversionUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.squareoffpostion")
	private String squareOffPostionUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.xApiKey")
	private String xApiKey;

}

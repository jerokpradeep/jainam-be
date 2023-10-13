package in.codifi.odn.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
@Getter
@Setter
public class ApplicationProperties {
	
	@ConfigProperty(name = "appconfig.admin.websocket.websocketEndpoint")
	private String websocketEndpoint;

	@ConfigProperty(name = "appconfig.admin.websocket.jMessageType")
	private String jMessageType;
	
	@ConfigProperty(name = "appconfig.admin.websocket.jAPIKey")
	private String apiKey;

	@ConfigProperty(name = "appconfig.admin.websocket.jSecretKey")
	private String jSecretKey;
	
	@ConfigProperty(name="appconfig.pushnotification.channleId")
	private String channelId;
	
	@ConfigProperty(name="appconfig.pushnotification.androidChannelId")
	private String androidChannelId;
	
	@ConfigProperty(name="appconfig.pushnotification.titleColor")
	private String titleColor;
	
	@ConfigProperty(name="appconfig.admin.support.email")
	private String supportEmail;
}


package in.codifi.auth.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class EmailProperties {

//	@ConfigProperty(name = "quarkus.mail.zoho.from")
//	private String zohoFrom;
//	@ConfigProperty(name = "quarkus.mail.zoho.hostname")
//	private String zohoHostName;
//	@ConfigProperty(name = "quarkus.mail.zoho.username")
//	private String zohoUserName;
//	@ConfigProperty(name = "quarkus.mail.zoho.password")
//	private String zohoPassword;
//	@ConfigProperty(name = "quarkus.mail.zoho.port")
//	private String zohoPort;
//	@ConfigProperty(name = "quarkus.mail.kasplo.from")
//	private String kasploFrom;
//	@ConfigProperty(name = "quarkus.mail.kasplo.hostname")
//	private String kasploHostName;
//	@ConfigProperty(name = "quarkus.mail.kasplo.username")
//	private String kasploUserName;
//	@ConfigProperty(name = "quarkus.mail.kasplo.password")
//	private String kasploPassword;
//	@ConfigProperty(name = "quarkus.mail.kasplo.port")
//	private String kasploPort;

	@ConfigProperty(name = "appconfig.mail.password")
	private String jainamPW;
	@ConfigProperty(name = "appconfig.mail.from")
	private String jainamFrom;
	@ConfigProperty(name = "appconfig.mail.port")
	private String jainamPort;
	@ConfigProperty(name = "appconfig.mail.username")
	private String jainamUserName;
	@ConfigProperty(name = "appconfig.mail.host")
	private String jainamHost;

}

package in.codifi.odn.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class EmailConfig {
	
	@ConfigProperty(name = "quarkus.mailer.from")
	private String from;
	@ConfigProperty(name = "quarkus.mailer.smtpHost")
	private String smtpHost;
	@ConfigProperty(name = "quarkus.mailer.smtpPort")
	private int smtpPort;
	@ConfigProperty(name = "quarkus.mailer.smtpUsername")
	private String smtpUsername;
	@ConfigProperty(name = "quarkus.mailer.smtpPassword")
	private String smtpPassword;
	@ConfigProperty(name = "quarkus.mailer.smtpAuth")
	private boolean smtpAuth;
	@ConfigProperty(name = "quarkus.mailer.smtpStartTls")
	private boolean smtpStartTls;
}

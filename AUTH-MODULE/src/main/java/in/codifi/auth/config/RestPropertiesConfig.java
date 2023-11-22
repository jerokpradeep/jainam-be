package in.codifi.auth.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestPropertiesConfig {

	@ConfigProperty(name = "appconfig.jainam.vendor-code-mob")
	private String mobileVendorCode;

	@ConfigProperty(name = "appconfig.jainam.vendor-code-web")
	private String webVendorCode;

	@ConfigProperty(name = "appconfig.jainam.vendor-code-api")
	private String apiVendorCode;

	@ConfigProperty(name = "appconfig.jainam.vendor-key-mob")
	private String mobileVendorKey;

	@ConfigProperty(name = "appconfig.jainam.vendor-key-web")
	private String webVendorKey;

	@ConfigProperty(name = "appconfig.jainam.vendor-key-api")
	private String apiVendorKey;

	@ConfigProperty(name = "appconfig.jainam.web.baseurl")
	private String cholaWebBaseUrl;

	@ConfigProperty(name = "appconfig.jainam.mob.baseurl")
	private String cholaMobBaseUrl;

	@ConfigProperty(name = "appconfig.jainam.api.baseurl")
	private String cholaApiBaseUrl;

	@ConfigProperty(name = "appconfig.jainam.apk-version")
	private String cholaApkVersion;

	@ConfigProperty(name = "appconfig.jainam.method.auth")
	private String cholaMethodAuth;

	@ConfigProperty(name = "appconfig.jainam.method.userdetails")
	private String cholaMethodUserDetails;
	
	@ConfigProperty(name = "appconfig.jainam.source")
	private String cholaSource;
	
	@ConfigProperty(name = "appconfig.jainam.method.logout")
	private String cholaMethodLogout;
}

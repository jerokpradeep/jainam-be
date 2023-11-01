package in.codifi.holdings.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {

	@ConfigProperty(name = "appconfig.odin.url.holdings")
	private String holdingsUrl;

	@ConfigProperty(name = "appconfig.odin.url.edis.dpdetails")
	private String edisDpDetailUrl;

	@ConfigProperty(name = "appconfig.odin.url.edis.summary")
	private String edisSummaryUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.edis.summarys")
	private String edisSummarysUrl;

	@ConfigProperty(name = "appconfig.odin.url.edis.redirect")
	private String reDirectUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.xapikey")
	private String xApiKey;

}
package in.codifi.alerts.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class ApplicationProperties {

	@ConfigProperty(name = "config.app.push.fcm.baseurl")
	private String fcmBaseUrl;

	@ConfigProperty(name = "config.app.push.fcm.apikey")
	private String fcmApiKey;

	@ConfigProperty(name = "config.app.alert.vendorname")
	private String alertVendorName;

//	@ConfigProperty(name = "config.app.exchmsg.url")
//	private String exchMsgUrl;
//
//	@ConfigProperty(name = "config.app.brokeragemsg.url")
//	private String brokerageMsgUrl;
//
//	@ConfigProperty(name = "config.app.exchstatus.url")
//	private String exchStatusUrl;

	@ConfigProperty(name = "appconfig.odin.url.xapikey")
	private String xApiKey;
	
	@ConfigProperty(name = "config.app.alert.baseurl")
	private String alertBaseUrl;

	// Alerts config

	@ConfigProperty(name = "config.app.alert.setalert.url")
	private String setAlertUrl;
	@ConfigProperty(name = "config.app.alert.getalert.url")
	private String getAlertUrl;
	@ConfigProperty(name = "config.app.alert.deletealert.url")
	private String deleteAlertUrl;
	@ConfigProperty(name = "config.app.alert.modifyalert.url")
	private String modifyAlertUrl;
	
	@ConfigProperty(name = "config.app.odin.getAlert.url")
	private String odinAlert;
	@ConfigProperty(name = "config.app.odin.createAlert.url")
	private String odinCreateAlert;
	
}

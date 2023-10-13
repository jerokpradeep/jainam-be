package in.codifi.ameyo.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class SMSPropertiesConfig {

	@ConfigProperty(name = "appconfig.sms.userid")
	String smsUserId;
	@ConfigProperty(name = "appconfig.sms.pass")
	String smsPass;
	@ConfigProperty(name = "appconfig.sms.appid")
	String smsAppId;
	@ConfigProperty(name = "appconfig.sms.subappid")
	String smsSubAppId;
	@ConfigProperty(name = "appconfig.sms.contenttype")
	String smsContentType;
	@ConfigProperty(name = "appconfig.sms.firsttext")
	String smsFirstText;
	@ConfigProperty(name = "appconfig.sms.secondtext")
	String smsSecondText;
	@ConfigProperty(name = "appconfig.sms.from")
	String smsFrom;
	@ConfigProperty(name = "appconfig.sms.selfid")
	String smsSelfid;
	@ConfigProperty(name = "appconfig.sms.alert")
	String smsAlert;
	@ConfigProperty(name = "appconfig.sms.dlrreq")
	String smsDlrReq;

}

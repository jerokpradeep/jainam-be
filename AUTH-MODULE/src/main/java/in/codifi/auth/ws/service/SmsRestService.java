package in.codifi.auth.ws.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.auth.config.SMSPropertiesConfig;
import in.codifi.auth.ws.service.spec.ISmsRestService;

@ApplicationScoped
public class SmsRestService {

	@Inject
	@RestClient
	ISmsRestService iSmsRestService;
	@Inject
	SMSPropertiesConfig props;

	public boolean sendOTPtoMobile(String otp, String mobileNumber) {
		try {
			String Text = props.getSmsFirstText() + " " + otp + " " + props.getSmsSecondText();
			String message = iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(), mobileNumber, props.getSmsFrom(), Text,
					props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			System.out.println(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

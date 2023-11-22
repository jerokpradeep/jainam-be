package in.codifi.auth.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.JSONValue;

import in.codifi.auth.config.SMSPropertiesConfig;
import in.codifi.auth.utility.CodifiUtil;
import in.codifi.auth.ws.service.spec.ISmsRestService;

@ApplicationScoped
public class SmsRestService {

	@Inject
	@RestClient
	ISmsRestService iSmsRestService;
	@Inject
	SMSPropertiesConfig props;

	public boolean sendOTPtoMobile(String otp, String mobileNumber, String userId) {
		Object object = new Object();
		try {
			
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String outputDate = formatter1.format(currentDate);

			String Sms = "https://api.msg91.com/api/sendhttp.php?authkey=372841Af3Lrw19xzHB62010a76P1&sender=JNMSHR&mobiles=91"
					+ mobileNumber + "&route=4&message=Your password for logging into the system for Client id: "
					+ userId + " is " + otp + ". Generated on " + outputDate + " Team Jainam";
			System.out.println(Sms);
			
//			https://api.msg91.com/api/sendhttp.php?authkey=372841Af3Lrw19xzHB62010a76P1&sender=JNMSHR&mobiles=918248078950&route=4&message=Your password for logging into the system for Client id: 1234 is CL1234. Generated on 08-09-2023 Team Jainam
//			String Text = props.getSmsFirstText() + " " + otp + " " + props.getSmsSecondText();
//			String message = iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
//					props.getSmsSubAppId(), props.getSmsContentType(), mobileNumber, props.getSmsFrom(), Sms,
//					props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());

//			String message = iSmsRestService.SendSms(Sms);

			CodifiUtil.trustedManagement();
			URL url = new URL(Sms);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
//				conn.setRequestProperty("Authorization", userSession);
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + responseCode);
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br1.readLine()) != null) {
				object = JSONValue.parse(output);
			}
			br1.close();
			conn.disconnect();

			System.out.println(Sms);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

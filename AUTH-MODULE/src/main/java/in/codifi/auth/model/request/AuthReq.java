package in.codifi.auth.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReq {

	private String userId;
	private String password;
	private String otp;
	private String source;
	private String newPassword;
	private String totp;
	private String deviceId;
	private String fcmToken;
	private String vendor;
	private String checkSum;
	private String token;
	private String imei;
	private String keyValue;
	private String deviceType;
}

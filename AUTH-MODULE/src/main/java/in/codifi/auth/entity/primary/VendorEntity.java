package in.codifi.auth.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "TBL_VENDOR_APP")
@Getter
@Setter
public class VendorEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "APP_NAME")
	private String appName;

	@Column(name = "API_KEY")
	private String apiKey;

	@Column(name = "API_SECRET")
	private String apiSecret;

	@Column(name = "CLIENT_ID")
	private String clientId;

	@Column(name = "CONTACT_NAME")
	private String contactName;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "ICON_URL")
	private String iconUrl;

	@Column(name = "REDIRECT_URL")
	private String redirectUrl;

	@Column(name = "POSTBACK_URL")
	private String postbackUrl;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "AUTHORIZATION_STATUS")
	private int authorizationStatus = 1;

	@Column(name = "IS_ACCEPTED")
	private int isAccepted = 1;

	@Column(name = "REJECTED_RESON")
	private String rejectedReson;

	@Column(name = "TYPE")
	private String type;

}

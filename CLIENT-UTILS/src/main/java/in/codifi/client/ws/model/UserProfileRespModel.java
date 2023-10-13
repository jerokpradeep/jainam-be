package in.codifi.client.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileRespModel {
	
	@JsonProperty("user_name")
	public String userName;
	@JsonProperty("email")
	public String email;
	@JsonProperty("mobile_no")
	public String mobileNo;
	@JsonProperty("pan")
	public String pan;
	@JsonProperty("address")
	public String address;
	@JsonProperty("mpin_enabled")
	public String mpinEnabled;
	@JsonProperty("fingerprint_enabled")
	public String fingerprintEnabled;
	@JsonProperty("dob")
	public String dob;
	@JsonProperty("gender")
	public String gender;
	@JsonProperty("productAllowed")
	public String productAllowed;
	@JsonProperty("bank_details")
	public List<BracketDetailsModel> bank_details;
	@JsonProperty("dp_details")
	public List<DpDetailsModel> dp_details;
	@JsonProperty("mf_details")
	public MfDetailsModel mf_details;

}

package in.codifi.funds.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("Active")
	public boolean active;
	@JsonProperty("BOCODE")
	public String boCode;
	@JsonProperty("BrCode")
	public String brCode;
	@JsonProperty("CCATEGORY")
	public String cCategory;
	@JsonProperty("CORRADDRESS1")
	public String corrAddress1;
	@JsonProperty("CORRADDRESS2")
	public String corrAddress2;
	@JsonProperty("CORRADDRESS3")
	public String corrAddress3;
	@JsonProperty("CORRCITY")
	public String corrCity;
	@JsonProperty("CORRCOUNTRY")
	public String corrCountry;
	@JsonProperty("CORRPINCODE")
	public String corrPincode;
	@JsonProperty("CORRSTATE")
	public String corrState;
	@JsonProperty("CrtDt")
	public String crtDt;
	@JsonProperty("DOB")
	public String dob;
	@JsonProperty("DPID")
	public String dpId;
	@JsonProperty("DPcode")
	public String dpCode;
	@JsonProperty("FirstName")
	public String firstName;
	@JsonProperty("Gender")
	public String gender;
	@JsonProperty("Income")
	public String income;
	@JsonProperty("LastName")
	public String lastName;
	@JsonProperty("MartialStatus")
	public String martialStatus;
	@JsonProperty("MobileFlag")
	public String mobileFlag;
	@JsonProperty("MobileNo")
	public String mobileNo;
	@JsonProperty("Name")
	public String name;
	@JsonProperty("Occupation")
	public String occupation;
	@JsonProperty("PERMADDRESS1")
	public String permAddress1;
	@JsonProperty("PERMADDRESS2")
	public String permAddress2;
	@JsonProperty("PERMADDRESS3")
	public String permAddress3;
	@JsonProperty("PERMCITY")
	public String permCity;
	@JsonProperty("PERMCOUNTRY")
	public String permCountry;
	@JsonProperty("PERMPINCODE")
	public String permPincode;
	@JsonProperty("PERMSTATE")
	public String permState;
	@JsonProperty("POAFlag")
	public String poaFlag;
	@JsonProperty("PanNo")
	public String panNo;
	@JsonProperty("Qualification")
	public String qualification;
	@JsonProperty("TradeConfirmation")
	public String tradeConfirmation;
	@JsonProperty("ccemailid")
	public String ccEmailId;
	@JsonProperty("discflag")
	public String discFlag;
	@JsonProperty("prefix")
	public String prefix;

}

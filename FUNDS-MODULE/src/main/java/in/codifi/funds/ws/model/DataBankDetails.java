package in.codifi.funds.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataBankDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("BANKACCOUNTNUMBER")
	public String bankAccountNumber;
	@JsonProperty("BANKCODE")
	public String bankCode;
	@JsonProperty("BANKNAME")
	public String bankName;
	@JsonProperty("DefaultBankFlag")
	public String deFaultBankFlag;
	@JsonProperty("IFSCCODE")
	public String ifscCode;
	@JsonProperty("MandateRegisteredFlag")
	public String mandateRegisteredFlag;

}

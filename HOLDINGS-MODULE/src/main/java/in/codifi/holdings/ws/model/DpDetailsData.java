package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DpDetailsData {

	@JsonProperty("sClientId")
	public String sClientId;
	@JsonProperty("sGroupId")
	public String sGroupId;
	@JsonProperty("sClientCode")
	public String sClientCode;
	@JsonProperty("nBeneficiaryAccountNumber")
	public String nBeneficiaryAccountNumber;
	@JsonProperty("sDPId")
	public String sDPId;
	@JsonProperty("sDepository")
	public String sDepository;
	@JsonProperty("nBeneficiaryStatus")
	public Integer nBeneficiaryStatus;
	@JsonProperty("nPriority")
	public Integer nPriority;
	@JsonProperty("sDPBankId")
	public String sDPBankId;

}

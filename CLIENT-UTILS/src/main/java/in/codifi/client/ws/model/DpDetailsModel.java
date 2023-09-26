package in.codifi.client.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class DpDetailsModel {
	
	@JsonProperty("dpId")
	public String dpId;
	@JsonProperty("clientBeneficiaryId")
	public String clientBeneficiaryId;
	@JsonProperty("holdingType")
	public String holdingType;
	@JsonProperty("dpType")
	public String dpType;
	@JsonProperty("defaultDP")
	public String defaultDP;
	@JsonProperty("dpName")
	public String dpName;

}

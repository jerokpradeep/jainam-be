package in.codifi.client.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BracketDetailsModel {
	
	@JsonProperty("bank_name")
	public String bankName;
	@JsonProperty("branch_name")
	public String brancName;
	@JsonProperty("branch_city")
	public String branchCity;
	@JsonProperty("account_no")
	public String accountNo;
	@JsonProperty("account_type")
	public String accountType;
	@JsonProperty("is_default")
	public String isDefault;

}

package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDISCDSLValidation {

	@JsonProperty("DPCheck")
	public Integer dPCheck;
	@JsonProperty("MaxTranCount")
	public Integer maxTranCount;
	@JsonProperty("Singletrans")
	public Integer singletrans;
	@JsonProperty("UserTranCount")
	public Integer userTranCount;
	@JsonProperty("ValueUtilized")
	public Integer valueUtilized;
	@JsonProperty("PerDayTransactionvalue")
	public Integer perDayTransactionvalue;

}

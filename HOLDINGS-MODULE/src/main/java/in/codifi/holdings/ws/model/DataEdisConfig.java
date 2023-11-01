package in.codifi.holdings.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataEdisConfig {
	
	@JsonProperty("DPCheck")
	public String dpCheck;
	@JsonProperty("MaxTranCount")
	public String maxTranCount;
	@JsonProperty("Singletrans")
	public String singleTrans;
	@JsonProperty("UserTranCount")
	public String userTranCount;
	@JsonProperty("ValueUtilized")
	public String valueUtilized;
	@JsonProperty("PerDayTransactionvalue")
	public String perDayTransvalue;

}

package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisScripDetailModel {

	@JsonProperty("ISIN")
	public String isin;
	@JsonProperty("Quantity")
	public Integer quantity;
	@JsonProperty("ISINName")
	public String iSINName;
	@JsonProperty("SettlmtCycle")
	public String settlmtCycle = "T1";

}

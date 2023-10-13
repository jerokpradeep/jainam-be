package in.codifi.holdings.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisHoldModel {

	@JsonProperty("ISIN")
	@JsonAlias({ "isin", "ISIN" })
	public String isin;
	@JsonProperty("Quantity")
	@JsonAlias({ "qty", "Quantity" })
	public Integer qty;
	@JsonProperty("ISINName")
	@JsonAlias({ "isinName", "ISINName" })
	public String isinName;
	@JsonProperty("SettlmtCycle")
	@JsonAlias({ "settlmtCycle", "SettlmtCycle" })
	public String settlmtCycle = "T1";

}

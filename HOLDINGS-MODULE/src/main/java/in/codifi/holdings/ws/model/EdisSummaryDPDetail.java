package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisSummaryDPDetail {

	@JsonProperty("ISIN")
	public String isin;
	@JsonProperty("SecurityDescription")
	public String securityDescription;
	@JsonProperty("QuantityToHold")
	public Integer quantityToHold;
	@JsonProperty("TotalFreeQty")
	public Integer totalFreeQty;
	@JsonProperty("TodaysFreeQty")
	public Integer todaysFreeQty;
	@JsonProperty("ApprovedFreeQty")
	public Integer approvedFreeQty;
	@JsonProperty("ClosePrice")
	public Integer closePrice;
	@JsonProperty("eDISCheckQty")
	public Integer eDISCheckQty;
	@JsonProperty("eDISRequestQty")
	public Integer eDISRequestQty;
	@JsonProperty("eDISDPQty")
	public Integer eDISDPQty;
	@JsonProperty("nSettlementType")
	public Integer nSettlementType;

}

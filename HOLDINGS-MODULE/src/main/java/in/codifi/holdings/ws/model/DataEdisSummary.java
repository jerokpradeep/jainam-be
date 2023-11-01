package in.codifi.holdings.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataEdisSummary {
	
	@JsonProperty("sDealerCode")
	public String dealerCode;
	@JsonProperty("sISINCode")
	public String isinCode;
	@JsonProperty("sSecurityDesc")
	public String securityDesc;
	@JsonProperty("TotalFreeQty")
	public String totalFreeQty;
	@JsonProperty("TodayFreeQty")
	public String todayFreeQty;
	@JsonProperty("ApprovedQuantity")
	public String approvedQty;
	@JsonProperty("ClosePrice")
	public String closePrice;
	@JsonProperty("eDISDPQty")
	public String eDISDPQty;
	@JsonProperty("eDISCheckQty")
	public String eDISCheckQty;
	@JsonProperty("eDISRequestQty")
	public String eDISRequestQty;
	@JsonProperty("nSettlementType")
	public String settlementType;

}

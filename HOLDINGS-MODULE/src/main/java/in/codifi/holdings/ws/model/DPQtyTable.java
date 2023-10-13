package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DPQtyTable {

	@JsonProperty("sDealerCode")
	public String sDealerCode;
	@JsonProperty("sISINCode")
	public String sISINCode;
	@JsonProperty("sSecurityDesc")
	public String sSecurityDesc;
	@JsonProperty("TotalFreeQty")
	public Integer totalFreeQty;
	@JsonProperty("TodayFreeQty")
	public Integer todayFreeQty;
	@JsonProperty("ApprovedQuantity")
	public Integer approvedQuantity;
	@JsonProperty("ClosePrice")
	public Float closePrice;
	@JsonProperty("eDISDPQty")
	public Integer eDISDPQty;
	@JsonProperty("eDISCheckQty")
	public Integer eDISCheckQty;
	@JsonProperty("eDISRequestQty")
	public Integer eDISRequestQty;
	@JsonProperty("nSettlementType")
	public Integer nSettlementType;

}

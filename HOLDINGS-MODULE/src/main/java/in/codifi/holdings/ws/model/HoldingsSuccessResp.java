package in.codifi.holdings.ws.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoldingsSuccessResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("isin")
	private String isin;
	@JsonProperty("security_info")
	private List<SecurityInfo> securityInfo = new ArrayList<>();
	@JsonProperty("total_free")
	private String totalFree;
	@JsonProperty("dp_free")
	private String dpFree;
	@JsonProperty("pool_free")
	private String poolFree;
	@JsonProperty("t1_quantity")
	private String t1Qty;
	@JsonProperty("average_price")
	private String avgPrice;
	@JsonProperty("last_price")
	private String lastPrice;
	@JsonProperty("pnl")
	private String pnl;
	@JsonProperty("current_value")
	private String currentValue;
	@JsonProperty("inv_value")
	private String invValue;
	@JsonProperty("product")
	private String product;
	@JsonProperty("collateral_quantity")
	private String collateralQty;
	@JsonProperty("collateral_value")
	private String collateralValue;

}
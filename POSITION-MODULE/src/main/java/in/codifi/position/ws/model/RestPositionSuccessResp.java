package in.codifi.position.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPositionSuccessResp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("exchange")
	private String exchange;
	@JsonProperty("preferred_exchange")
	private String prefExch;
	@JsonProperty("scrip_token")
	private int scripToken;
	@JsonProperty("preferred_scrip_token")
	private String prefScripToken;
	@JsonProperty("product_type")
	private String prdType;
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("series")
	private String series;
	@JsonProperty("market_lot")
	private int mktLot;
	@JsonProperty("instrument")
	private String instrument;
	@JsonProperty("expiry_date")
	private String expiryDate;
	@JsonProperty("strike_price")
	private String strikePrice;
	@JsonProperty("option_type")
	private String optType;
	@JsonProperty("buy_quantity")
	private int buyQty;
	@JsonProperty("avg_buy_price")
	private String avgBuyPrice;
	@JsonProperty("buy_value")
	private String buyValue;
	@JsonProperty("sell_quantity")
	private int sellQty;
	@JsonProperty("avg_sell_price")
	private String avgSellPrice;
	@JsonProperty("sell_value")
	private String sellValue;
	@JsonProperty("net_quantity")
	private int netQty;
	@JsonProperty("net_price")
	private String netPrice;
	@JsonProperty("net_value")
	private String netValue;
	@JsonProperty("cf_buy_value")
	private String cfBuyValue;
	@JsonProperty("cf_sell_value")
	private String cfSellValue;
	@JsonProperty("cf_net_price")
	private String cfNetPrice;
	@JsonProperty("unsetteled_quantity")
	private int unsetteledQty;
	@JsonProperty("multiplier")
	private String multiplier;
	@JsonProperty("daily_or_expiry")
	private String dailyOrExpiry;

}
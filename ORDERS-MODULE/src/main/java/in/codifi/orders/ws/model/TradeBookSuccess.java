package in.codifi.orders.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeBookSuccess {

	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("exchange")
	private String exch;
	@JsonProperty("scrip_token")
	private String scripToken;
	@JsonProperty("trade_no")
	private String tradeNo;
	@JsonProperty("exchange_order_no")
	private String exchOrderNo;
	@JsonProperty("transaction_type")
	private String transType;
	@JsonProperty("product_type")
	private String productType;
	@JsonProperty("order_type")
	private String orderType;
	@JsonProperty("trade_quantity")
	private String tradeQty;
	@JsonProperty("trade_price")
	private String tradePrice;
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("series")
	private String series;
	@JsonProperty("instrument")
	private String instrument;
	@JsonProperty("expiry_date")
	private String expiryDate;
	@JsonProperty("strike_price")
	private String strikePrice;
	@JsonProperty("option_type")
	private String optType;
	@JsonProperty("trade_timestamp")
	private String tradeTime;
	@JsonProperty("initiated_by")
	private String initiatedBy;
	@JsonProperty("modified_by")
	private String modifiedBy;
	@JsonProperty("order_identifier")
	private String orderIdentifier;

}
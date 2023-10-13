package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookSuccess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("exchange")
	private String exch;
	@JsonProperty("scrip_token")
	private String scripToken;
	@JsonProperty("exchange_order_no")
	private String exchOrderNo;
	@JsonProperty("status")
	private String status;
	@JsonProperty("error_reason")
	private String errorReason;
	@JsonProperty("transaction_type")
	private String transType;
	@JsonProperty("product_type")
	private String productType;
	@JsonProperty("order_type")
	private String orderType;
	@JsonProperty("total_quantity")
	private String totalQty;
	@JsonProperty("pending_quantity")
	private String pendingQty;
	@JsonProperty("traded_quantity")
	private String tradedQty;
	@JsonProperty("disclosed_quantity")
	private String disclosedQty;
	@JsonProperty("order_price")
	private String orderPrice;
	@JsonProperty("trigger_price")
	private String triggerPrice;
	@JsonProperty("validity")
	private String validity;
	@JsonProperty("validity_days")
	private String validityDays;
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
	@JsonProperty("market_lot")
	private String mktLot;
	@JsonProperty("order_timestamp")
	private String orderTime;
	@JsonProperty("exchange_timestamp")
	private String exchTime;
	@JsonProperty("initiated_by")
	private String initiatedBy;
	@JsonProperty("modified_by")
	private String modifiedBy;
	@JsonProperty("is_amo_order")
	private boolean amo;
	@JsonProperty("gateway_order_id")
	private String gatewayOrderId;
	@JsonProperty("order_identifier")
	private String orderIdentifier;
	@JsonProperty("bracket_details")
	private BracketDetails bracketDetails;

}
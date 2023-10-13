package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderHistoryRespData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("exchange")
	private String exchange;
	@JsonProperty("scrip_token")
	private String scripToken;
	@JsonProperty("exchange_order_no")
	private String excOrderNo;
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
	@JsonProperty("order_timestamp")
	private String orderTimestamp;
	@JsonProperty("exchange_timestamp")
	private String exchangeTimestamp;
	@JsonProperty("initiated_by")
	private String initiatedBy;
	@JsonProperty("is_amo_order")
	private boolean amo;
	@JsonProperty("gateway_order_id")
	private String gatewayOrderId;
	@JsonProperty("order_identifier")
	private String orderIdentifier;
	@JsonProperty("sequence_number")
	private String sequenceNumber;
	@JsonProperty("bracket_details")
	private BracketDetailsRes bracketDetails;

}

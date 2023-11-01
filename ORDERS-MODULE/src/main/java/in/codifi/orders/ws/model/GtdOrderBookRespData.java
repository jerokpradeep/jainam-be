package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GtdOrderBookRespData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("client_id")
	public String clientId;
	@JsonProperty("order_id")
	public String orderId;
	@JsonProperty("exchange")
	public String exchange;
	@JsonProperty("scrip_token")
	public Integer scripToken;
	@JsonProperty("exchange_order_no")
	public String exchOrderNo;
	@JsonProperty("status")
	public String status;
	@JsonProperty("error_reason")
	public String errorReason;
	@JsonProperty("transaction_type")
	public String tranType;
	@JsonProperty("product_type")
	public String productType;
	@JsonProperty("order_type")
	public String orderType;
	@JsonProperty("total_quantity")
	public Integer totalQty;
	@JsonProperty("pending_quantity")
	public Integer pendingQty;
	@JsonProperty("traded_quantity")
	public Integer tradedQty;
	@JsonProperty("prev_traded_quantity")
	private Integer prevTradedQty;
	@JsonProperty("today_traded_quantity")
	private String todayTradedQty;
	@JsonProperty("is_amo_order")
	private Boolean isAmoOrder;
	@JsonProperty("disclosed_quantity")
	public Integer disclosedQty;
	@JsonProperty("order_price")
	public String orderPrice;
	@JsonProperty("trigger_price")
	public String triggerPrice;
	@JsonProperty("validity")
	public String validity;
	@JsonProperty("validity_days")
	public Integer validityDays;
	@JsonProperty("symbol")
	public String symbol;
	@JsonProperty("series")
	public String series;
	@JsonProperty("instrument")
	public String instrument;
	@JsonProperty("expiry_date")
	public String expiryDate;
	@JsonProperty("strike_price")
	public String strikePrice;
	@JsonProperty("option_type")
	public String optType;
	@JsonProperty("market_lot")
	public Integer mktLot;
	@JsonProperty("order_timestamp")
	public String order_timestamp;
	@JsonProperty("initiated_by")
	public String initiatedBy;
	@JsonProperty("modified_by")
	public String modifiedBy;
	@JsonProperty("gateway_order_id")
	public String gatewayOrderId;
	@JsonProperty("order_identifier")
	public Integer orderIdentifier;

}

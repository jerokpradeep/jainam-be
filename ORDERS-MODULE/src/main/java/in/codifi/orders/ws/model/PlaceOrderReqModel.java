package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceOrderReqModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("scrip_info")
	private ScripInfo scripInfo = new ScripInfo();
	@JsonProperty("transaction_type")
	private String transactionType;
	@JsonProperty("product_type")
	private String productType;
	@JsonProperty("order_type")
	private String orderType;
	@JsonProperty("quantity")
	private String quantity;
	@JsonProperty("price")
	private float price;
	@JsonProperty("trigger_price")
	private float triggerPrice = 0;
	@JsonProperty("disclosed_quantity")
	private int disclosedQuantity = 0;
	@JsonProperty("validity")
	private String validity;
	@JsonProperty("validity_days")
	private int validityDays;
	@JsonProperty("is_amo")
	private Boolean isAmo = false;
	@JsonProperty("order_identifier")
	private String orderIdentifier;
	@JsonProperty("part_code")
	private String partCode;
	@JsonProperty("algo_id")
	private String algoId;
	@JsonProperty("strategy_id")
	private String strategyId;
	@JsonProperty("vender_code")
	private String venderCode;

}

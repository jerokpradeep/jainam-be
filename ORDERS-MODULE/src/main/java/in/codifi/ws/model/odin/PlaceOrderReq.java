package in.codifi.ws.model.odin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.codifi.orders.ws.model.ScripInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrderReq {

	@JsonProperty("scrip_info")
	public ScripInfo scripInfo;
	@JsonProperty("transaction_type")
	public String transactionType;
	@JsonProperty("product_type")
	public String productType;
	@JsonProperty("order_type")
	public String orderType;
	@JsonProperty("quantity")
	public Integer quantity;
	@JsonProperty("price")
	public Integer price;
	@JsonProperty("trigger_price")
	public Integer triggerPrice;
	@JsonProperty("disclosed_quantity")
	public Integer disclosedQuantity;
	@JsonProperty("validity")
	public String validity;
	@JsonProperty("validity_days")
	public Integer validityDays;
	@JsonProperty("is_amo")
	public Boolean isAmo;
	@JsonProperty("order_identifier")
	public String orderIdentifier;
	@JsonProperty("part_code")
	public String partCode;
	@JsonProperty("algo_id")
	public String algoId;
	@JsonProperty("strategy_id")
	public String strategyId;
	@JsonProperty("vender_code")
	public String venderCode;
}

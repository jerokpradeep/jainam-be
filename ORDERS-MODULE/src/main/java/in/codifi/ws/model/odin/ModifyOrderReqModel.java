package in.codifi.ws.model.odin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModifyOrderReqModel {

	@JsonProperty("order_type")
	public String orderType;
	@JsonProperty("quantity")
	public int quantity;
	@JsonProperty("traded_quantity")
	public int tradedQuantity;
	@JsonProperty("price")
	public float price = 0;
	@JsonProperty("trigger_price")
	public float triggerPrice =0;
	@JsonProperty("disclosed_quantity")
	public int disclosedQuantity;
	@JsonProperty("validity")
	public String validity = "DAY";
	@JsonProperty("validity_days")
	public int validityDays;

}

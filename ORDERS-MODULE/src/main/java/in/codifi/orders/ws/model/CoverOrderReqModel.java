package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoverOrderReqModel extends PlaceOrderReqModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("transaction_type")
	public String transactionType;
	@JsonProperty("main_leg")
	public MainLeg mainLeg = new MainLeg();
	@JsonProperty("stoploss_leg")
	public StoplossLeg stoplossLeg = new StoplossLeg();
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

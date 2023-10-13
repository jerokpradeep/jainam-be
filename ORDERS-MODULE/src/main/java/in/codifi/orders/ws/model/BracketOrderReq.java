package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BracketOrderReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("scrip_info")
	private ScripInfo scripInfo = new ScripInfo();
	@JsonProperty("transaction_type")
	private String transType;
	@JsonProperty("main_leg")
	private BoMainLeg boMainLeg = new BoMainLeg();
	@JsonProperty("stoploss_leg")
	private BoStoplossLeg StopLossLeg = new BoStoplossLeg();
	@JsonProperty("profit_leg")
	private BoProfitLeg boProfitLeg = new BoProfitLeg();
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

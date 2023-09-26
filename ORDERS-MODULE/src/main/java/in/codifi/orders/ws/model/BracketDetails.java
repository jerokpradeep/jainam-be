package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BracketDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("parent_order_id")
	private String parentOrderId;
	@JsonProperty("leg_indicator")
	private String legIndicator;
	@JsonProperty("stoploss_price")
	private String stoplossPrice;
	@JsonProperty("stoploss_trigger_price")
	private String stoplossTriggerPrice;
	@JsonProperty("profit_price")
	private String profitPrice;
	@JsonProperty("stoploss_jump_price")
	private String stoplossJumpPrice;
	@JsonProperty("ltp_jump_price")
	private String ltpJumpPrice;
	@JsonProperty("bo_order_id")
	private String boOrderId;
	@JsonProperty("bo_order_status")
	private String boOrderStatus;
	@JsonProperty("bo_modify_flag")
	private String boModifyFlag;
	@JsonProperty("bo_gatewayorderno")
	private String boGatewayOrderNo;

}

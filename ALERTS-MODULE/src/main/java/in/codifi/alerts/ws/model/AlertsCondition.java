package in.codifi.alerts.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertsCondition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("operand1")
	private AlertsOperandOne operandOne;
	@JsonProperty("operand2")
	private AlertsOperandTwo operandTwo;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("broadcast")
	private Integer[] broadcast;
	@JsonProperty("triggered")
	private Integer triggered;
	@JsonProperty("triggeredTime")
	private String triggeredTime;

}

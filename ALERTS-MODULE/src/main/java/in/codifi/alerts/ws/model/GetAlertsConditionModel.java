package in.codifi.alerts.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAlertsConditionModel {

	@JsonProperty("operand1")
	private Operand1Model operand1;
	@JsonProperty("operand2")
	private Operand2Model operand2;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("broadcast")
	private List<Integer> broadcast;
	@JsonProperty("triggered")
	private Integer triggered;
	@JsonProperty("triggeredTime")
	private String triggeredTime;

}

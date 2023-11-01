package in.codifi.alerts.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionRespModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("operand1")
	private Operand1Model operand1;
	@JsonProperty("operand2")
	private Operand2Model operand2;
	@JsonProperty("operator")
	private String operator;
}

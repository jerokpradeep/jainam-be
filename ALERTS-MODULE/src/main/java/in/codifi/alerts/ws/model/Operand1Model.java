package in.codifi.alerts.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Operand1Model {
	@JsonProperty("marketsegment")
	private Integer marketsegment;
	@JsonProperty("token")
	private Integer token;
	@JsonProperty("field")
	private String field;

}

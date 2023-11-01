package in.codifi.alerts.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Operand2Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("marketsegment")
	private Integer marketsegment;
	@JsonProperty("token")
	private Integer token;
	@JsonProperty("field")
	private String field;
	@JsonProperty("value")
	private Integer value;

}

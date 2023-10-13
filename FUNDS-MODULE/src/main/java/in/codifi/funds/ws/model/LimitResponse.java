package in.codifi.funds.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("status")
	private String status;
	@JsonProperty("result")
	private Object result;

}

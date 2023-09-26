package in.codifi.ws.model.odin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("status")
	private String status;
	@JsonProperty("code")
	private String code;
	@JsonProperty("message")
	private String message;
	@JsonProperty("data")
	private OrderResponseData data;

}

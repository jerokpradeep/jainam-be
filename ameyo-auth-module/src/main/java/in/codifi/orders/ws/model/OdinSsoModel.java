package in.codifi.orders.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OdinSsoModel {

	@JsonProperty("code")
	public Integer code;
	@JsonProperty("message")
	public String message = "";
	@JsonProperty("plain-message")
	public PlainMessage plainMessage;

}

package in.codifi.orders.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlainMessage {

	@JsonProperty("ucc")
	public String ucc = "";
	@JsonProperty("timestamp")
	public String timestamp = "";
	@JsonProperty("callback-url")
	public String callbackUrl = "";

}

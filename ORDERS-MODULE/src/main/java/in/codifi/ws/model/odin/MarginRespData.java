package in.codifi.ws.model.odin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class MarginRespData {

	@JsonProperty("status")
	public Integer status;
	@JsonProperty("result")
	public MarginRespResult result;

}

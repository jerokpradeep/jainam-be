package in.codifi.position.ws.remodeling;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SquareOffResModel {

	@JsonProperty("status")
	public String status;
	@JsonProperty("code")
	public String code;

	@JsonProperty("message")
	private String message;

	@JsonProperty("message")
	@JsonIgnore
	public PlaceOrderRespModel messageObj;

	@JsonProperty("data")
	private List<PlaceOrderRespModel> SquareOffResModel;

}

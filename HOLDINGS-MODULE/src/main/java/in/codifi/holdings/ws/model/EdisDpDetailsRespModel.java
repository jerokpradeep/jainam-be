package in.codifi.holdings.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisDpDetailsRespModel {

	@JsonProperty("status")
	public String status;
	@JsonProperty("code")
	public String code;
	@JsonProperty("message")
	public String message;
	@JsonProperty("data")
	public DpDetailsData data;

}

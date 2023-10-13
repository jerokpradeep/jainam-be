package in.codifi.alerts.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertRespModel {
	@JsonProperty("status")
	private String status;
	@JsonProperty("code")
	private String code;
	@JsonProperty("message")
	private String message;
	@JsonProperty("data")
	private DataModel data;

}

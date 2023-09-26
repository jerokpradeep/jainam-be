package in.codifi.alerts.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class GetAlertsRespModel {
	@JsonProperty("status")
	private String status;
	@JsonProperty("code")
	private String code;
	@JsonProperty("message")
	private String message;
	@JsonProperty("data")
	private List<GetAlertsDataModel> data;

}

package in.codifi.alerts.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertsReqModel {

	@JsonProperty("condition")
	private List<ConditionRespModel> condition;
	@JsonProperty("data")
	private DataRespModel data;
	@JsonProperty("expiration")
	private Integer expiration;
	@JsonProperty("createDate")
	private String createDate;

}

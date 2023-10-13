package in.codifi.alerts.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyAlertsReqModel {

	@JsonProperty("serverAlertId")
	private String serverAlertId;
	@JsonProperty("condition")
	private List<ConditionRespModel> condition;
	@JsonProperty("data")
	private DataRespModel data;
	@JsonProperty("expiration")
	private Integer expiration;
	@JsonProperty("createDate")
	private String createDate;

}

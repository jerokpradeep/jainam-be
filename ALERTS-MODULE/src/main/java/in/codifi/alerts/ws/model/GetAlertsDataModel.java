package in.codifi.alerts.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAlertsDataModel {

	@JsonProperty("condition")
	private List<GetAlertsConditionModel> condition;
	@JsonProperty("data")
	private DataModel data;
	@JsonProperty("expiration")
	private Integer expiration;
	@JsonProperty("createDate")
	private String createDate;
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("workerId")
	private String workerId;
	@JsonProperty("triggered")
	private Integer triggered;
	@JsonProperty("entryTime")
	private String entryTime;
	@JsonProperty("UCC")
	private String ucc;
	@JsonProperty("MessageType")
	private String messageType;
	@JsonProperty("serverAlertId")
	private String serverAlertId;
	@JsonProperty("msgCode")
	private Integer msgCode;

}

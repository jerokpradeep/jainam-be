package in.codifi.alerts.ws.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertResponseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("condition")
	private List<AlertsCondition> condition;
	@JsonProperty("data")
	private DataRespModel data;
	@JsonProperty("expiration")
	private String expiration;
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

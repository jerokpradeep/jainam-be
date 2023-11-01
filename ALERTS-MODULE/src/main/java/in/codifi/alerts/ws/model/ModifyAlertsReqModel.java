package in.codifi.alerts.ws.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModifyAlertsReqModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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

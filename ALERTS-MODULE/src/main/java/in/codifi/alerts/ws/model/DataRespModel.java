package in.codifi.alerts.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRespModel {

	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("userid")
	private String userid;
	@JsonProperty("tenantid")
	private String tenantid;
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("alertType")
	private String alertType;
	@JsonProperty("category")
	private String category;

}

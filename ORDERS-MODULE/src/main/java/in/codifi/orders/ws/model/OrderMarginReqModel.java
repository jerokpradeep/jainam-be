package in.codifi.orders.ws.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMarginReqModel {

	@JsonProperty("NoOfLegs")
	public Integer noOfLegs;
	@JsonProperty("Mode")
	public String mode;
	@JsonProperty("FETraceId")
	public String fETraceId;
	@JsonProperty("LegDetails")
	public List<LegDetail> legDetails = new ArrayList<LegDetail>();

}

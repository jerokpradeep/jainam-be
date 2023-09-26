package in.codifi.orders.ws.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderMarginInfoReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("NoOfLegs")
	private int noOfLegs;
	@JsonProperty("Mode")
	private String mode;
	@JsonProperty("FETraceId")
	private String feTraceId;
	@JsonProperty("LegDetails")
	private List<OrderMarginInfoLegDetails> legDetails;

}

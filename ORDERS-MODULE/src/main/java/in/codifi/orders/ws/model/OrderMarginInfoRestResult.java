package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderMarginInfoRestResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("Status")
	private int status;
	@JsonProperty("ClientId")
	private String clientId;
	@JsonProperty("ClientCode")
	private String clientCode;
	@JsonProperty("Token")
	private String token;
	@JsonProperty("MarketSegmentId")
	private String mktSegmentId;
	@JsonProperty("ApproxMargin")
	private String approxMargin;
	@JsonProperty("AvailableMargin")
	private String availableMargin;
	@JsonProperty("RequestTraceId")
	private String reqTraceId;
	@JsonProperty("ProductType")
	private String productType;
	@JsonProperty("ShortFall")
	private String shortFall;
	@JsonProperty("Brokerage")
	private String brokerage;

}

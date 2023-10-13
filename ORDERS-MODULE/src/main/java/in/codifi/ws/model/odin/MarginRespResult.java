package in.codifi.ws.model.odin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class MarginRespResult {

	@JsonProperty("Status")
	public Integer status;
	@JsonProperty("ClientId")
	public String clientId;
	@JsonProperty("ClientCode")
	public String clientCode;
	@JsonProperty("Token")
	public String token;
	@JsonProperty("MarketSegmentId")
	public String marketSegmentId;
	@JsonProperty("ApproxMargin")
	public String approxMargin;
	@JsonProperty("AvailableMargin")
	public String availableMargin;
	@JsonProperty("RequestTraceId")
	public String requestTraceId;
	@JsonProperty("ProductType")
	public String productType;
	@JsonProperty("ShortFall")
	public String shortFall;
	@JsonProperty("Brokerage")
	public String brokerage;

}

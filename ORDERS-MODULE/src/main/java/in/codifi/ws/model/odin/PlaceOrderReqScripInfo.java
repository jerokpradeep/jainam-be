package in.codifi.ws.model.odin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrderReqScripInfo {

	@JsonProperty("exchange")
	public String exchange;
	@JsonProperty("scrip_token")
	public Integer scripToken;
	@JsonProperty("symbol")
	public String symbol;
	@JsonProperty("series")
	public String series;
	@JsonProperty("expiry_date")
	public String expiryDate;
	@JsonProperty("strike_price")
	public String strikePrice;
	@JsonProperty("option_type")
	public String optionType;

}

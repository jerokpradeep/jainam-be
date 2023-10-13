package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipScripInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("exchange")
	private String exchange;
	@JsonProperty("scrip_token")
	private Integer scripToken;
	@JsonProperty("symbol")
	private String symbol = "";
	@JsonProperty("series")
	private String series = "";
	@JsonProperty("expiry_date")
	private String expiryDate = "";
	@JsonProperty("strike_price")
	private String strikePrice = "";
	@JsonProperty("option_type")
	private String optionType = "";

}

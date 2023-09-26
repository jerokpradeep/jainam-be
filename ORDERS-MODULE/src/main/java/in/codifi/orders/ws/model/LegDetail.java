package in.codifi.orders.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LegDetail {

	@JsonProperty("LegNo")
	public Integer legNo;
	@JsonProperty("BuyOrSell")
	public Integer buyOrSell;
	@JsonProperty("MarketSegmentId")
	public Integer marketSegmentId;
	@JsonProperty("Token")
	public Integer token;
	@JsonProperty("Quantity")
	public Integer quantity;
	@JsonProperty("Price")
	public Float price;
	@JsonProperty("MktFlag")
	public Integer mktFlag;
	@JsonProperty("OldPrice")
	public Integer oldPrice = 0;
	@JsonProperty("OldQuantity")
	public Integer oldQuantity = 0;
	@JsonProperty("ProductType")
	public String productType;
	@JsonProperty("LegIndicator")
	public Integer legIndicator = 0;

}

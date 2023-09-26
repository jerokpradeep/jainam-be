package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderMarginInfoLegDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("LegNo")
	private int LegNo;
	@JsonProperty("BuyOrSell")
	private int buyOrSell;
	@JsonProperty("MarketSegmentId")
	private int mktSegmentId;
	@JsonProperty("Token")
	private int token;
	@JsonProperty("Quantity")
	private int qty;
	@JsonProperty("Price")
	private int price;
	@JsonProperty("MktFlag")
	private int mktFlag;
	@JsonProperty("OldPrice")
	private int oldPrice;
	@JsonProperty("OldQuantity")
	private int oldQuantity;
	@JsonProperty("ProductType")
	private String ProductType;
	@JsonProperty("LegIndicator")
	private int legIndicator;

}

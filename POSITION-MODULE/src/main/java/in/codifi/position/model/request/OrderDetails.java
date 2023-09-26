package in.codifi.position.model.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String exchange;
	private String tradingSymbol;
	private String qty;
	private String price;
	private String orderType;
	private String product;
	private String priceType;
	private String transType;
	private String ret;
	private String triggerPrice;
	private String disclosedQty;
	private String mktProtection;
	private String target;// bookProfitPrice
	private String stopLoss;// bookLossPrice
	private String trailingStopLoss;
	private String orderNo;
	private String source;
	private String token;
}

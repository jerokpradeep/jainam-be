package in.codifi.orders.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrokerageCalculationReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String instrumentType;
	private String productType;
	private String transType;
	private String qty;
	private String price;
	private String brokerage;
	private String legIndicator;
	private String exchange;
	private String mode;
	private String token;
	private String oldPrice;
	private String oldQty;

}

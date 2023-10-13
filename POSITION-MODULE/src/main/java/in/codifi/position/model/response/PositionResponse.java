package in.codifi.position.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private String tradingsymbol;
	private String token;
	private String exchange;
	private String product;
	private String netQty;
	private String netAvgPrice;
	private String buyQty;
	private String buyPrice;
	private String sellQty;
	private String sellPrice;
	private String mtm;
	private String mtmBuyPrice;
	private String mtmSellprice;
	private String pnl;
	private String realizedPnl;
	private String unrealizedPnl;
	private String multiplier;
	private String lotsize;
	private String ticksize;
	private String pdc;
	private String ltp;
	private String breakevenPrice;
	private String overnightQty;
	private String overnightPrice;
	private String orderType;
	private String ret;

}

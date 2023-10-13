package in.codifi.funds.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitsResponseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float availableMargin;
	private float openingBalance;
	private float marginUsed;
	private float payin;
	private float stockPledge;
	private float holdingSellCredit;
//	private String peakMargin;//TODO
//	private float brokerage;
	private float exposure;
//	private float span;
	private float premium;
	private float bookedPAndL;
	private float mtmPAndL;
	
	private float collateral;
	private float fundsTranstoday;
	private float creditForSale;
	private float totalUtilize;
	private float allocationOrWithdrawal;
	private float netAvailableFunds;

}

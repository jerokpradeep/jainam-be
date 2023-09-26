package in.codifi.funds.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitRespData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("Product")
	private int product;
	@JsonProperty("Deposit")
	private float deposit;
	@JsonProperty("Funds Transferred Today")
	private float fundsTransferredToday;
	@JsonProperty("Collateral")
	private float collateral;
	@JsonProperty("Credit for Sale")
	private float creditForSale;
	@JsonProperty("Option CFS")
	private float optionCFS;
	@JsonProperty("Total Trading Power Limit")
	private float totalTradingPowerLimit;
	@JsonProperty("Limit Utilization")
	private float limitUtilization;
	@JsonProperty("Booked P&L")
	private float bookedPAndL;
	@JsonProperty("MTM P&L")
	private float mtmPAndL;
	@JsonProperty("Total Utilization")
	private float totalUtilization;
	@JsonProperty("Net Available Funds")
	private float netAvailableFunds;
	@JsonProperty("For Allocation/Withdrawal")
	private float forAllocationWithdrawal;
	@JsonProperty("Exposure Margin")
	private float exposureMargin;
	@JsonProperty("Cash Deposit")
	private float cashDeposit;
	@JsonProperty("Adhoc Deposit")
	private float adhocDeposit;

}

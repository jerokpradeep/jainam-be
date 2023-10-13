package in.codifi.orders.model.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipOrderDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String exchange;
	private String token;
	private String symbol;
	private String series;
	private String expiryDate;
	private String strikePrice;
	private String optionType;
	private String frequencyType;
	private String frequencyDays;
	private String frequencyStartDate;
	private String frequencySpecificDate;
	private String frequencyMonthlyOption;
	private String noOfInstallments;
	private String companyName;
	private String ltp;
	private String capPrice;
	private String investedBy;
	private String qtyOrValue;
	private String transType;
	private String status;
	private String basketName;
	private String sipId;
	private String investmentType; 

}

package in.codifi.scrips.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityInfoModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("nToken")
	private int nToken;
	@JsonProperty("sSymbol")
	private String sSymbol;
	@JsonProperty("sSeries")
	private String sSeries;
	@JsonProperty("sSecurityDesc")
	private String sSecurityDesc;
	@JsonProperty("sISINCode")
	private String sISINCode;
	@JsonProperty("nInstrumentType")
	private String nInstrumentType;
	@JsonProperty("nRegularLot")
	private Integer nRegularLot;
	@JsonProperty("nPriceTick")
	private Integer nPriceTick;
	@JsonProperty("nIssuedCapital")
	private long nIssuedCapital;
	@JsonProperty("nFreezePercent")
	private Integer nFreezePercent;
	@JsonProperty("nFaceValue")
	private Integer nFaceValue;
	@JsonProperty("nAVMBuyMargin")
	private Integer nAVMBuyMargin;
	@JsonProperty("nAVMSellMargin")
	private Integer nAVMSellMargin;
	@JsonProperty("nBookClosureEndDate")
	private Integer nBookClosureEndDate;
	@JsonProperty("nBookClosureStartDate")
	private Integer nBookClosureStartDate;
	@JsonProperty("nNoDeliveryStartDate")
	private Integer nNoDeliveryStartDate;
	@JsonProperty("nNoDeliveryEndDate")
	private Integer nNoDeliveryEndDate;
	@JsonProperty("sRemarks")
	private String sRemarks;
	@JsonProperty("nRecordDate")
	private Integer nRecordDate;
	@JsonProperty("nExDate")
	private Integer nExDate;
	@JsonProperty("nIntrinsicValue")
	private Integer nIntrinsicValue;
	@JsonProperty("nMarginMultiplier")
	private Integer nMarginMultiplier;
	@JsonProperty("nPriceQuotFactor")
	private Integer nPriceQuotFactor;
	@JsonProperty("nNormal_SecurityStatus")
	private String nNormal_SecurityStatus;
	@JsonProperty("SPOS")
	private Integer SPOS;
	@JsonProperty("SPOSTYPE")
	private Integer SPOSTYPE;
	@JsonProperty("CouponRate")
	private Integer CouponRate;
	@JsonProperty("DaysInYear")
	private Integer DaysInYear;
	@JsonProperty("DaysInMonth")
	private String DaysInMonth;
	@JsonProperty("CouponFrequency")
	private String CouponFrequency;
	@JsonProperty("MathcingType")
	private Integer MathcingType;
	@JsonProperty("ValueMethod")
	private String ValueMethod;
	@JsonProperty("LastInterestPaymentDate")
	private String LastInterestPaymentDate;
	@JsonProperty("NextInterestPaymentDate")
	private String NextInterestPaymentDate;
	@JsonProperty("MaturityDate")
	private String MaturityDate;
	@JsonProperty("InstrumentCode")
	private Integer InstrumentCode;
	@JsonProperty("Status")
	private String Status;
	@JsonProperty("SettlementType")
	private Integer SettlementType;
	@JsonProperty("CreditRating")
	private String CreditRating;

}

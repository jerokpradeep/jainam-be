package in.codifi.orders.model.transformation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SipOrderBookResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String sipId;
	public String symbol;
	public String status;
	public String transTtype;
	public String displayTransType;
	public String frequencyType;
	public String frequencyDetails;
	public String executedInstallments;
	public String noOfInstallments;
	public String createdDate;
	public String frequencyStartDate;
	public String createdBy;
	public String modifiedBy;
	public String investmentType;
	public String investedQtyOrVal;
	public String assestType;
	public String installmentDate;
	public String investmentValue;
	public String tradedQty;
	public String token;
	public String exchange;
	public String series;
	public String bDetail;
	public String bModify;
	public String bStop;
	public String bView;
	public String capPrice;
	public String updatedStatus;
	public String caaRemark;

}

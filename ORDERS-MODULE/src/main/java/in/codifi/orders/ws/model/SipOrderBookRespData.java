package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipOrderBookRespData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("sip_id")
	public String sipId;
	@JsonProperty("symbol")
	public String symbol;
	@JsonProperty("status")
	public String status;
	@JsonProperty("transaction_type")
	public String transTtype;
	@JsonProperty("display_transaction_type")
	public String displayTransType;
	@JsonProperty("frequency_type")
	public String frequencyType;
	@JsonProperty("frequency_details")
	public String frequencyDetails;
	@JsonProperty("executed_installments")
	public String executedInstallments;
	@JsonProperty("no_of_installments")
	public String noOfInstallments;
	@JsonProperty("created_date")
	public String createdDate;
	@JsonProperty("frequency_start_date")
	public String frequencyStartDate;
	@JsonProperty("created_by")
	public String createdBy;
	@JsonProperty("modified_by")
	public String modifiedBy;
	@JsonProperty("investment_type")
	public String investmentType;
	@JsonProperty("invested_qty_or_val")
	public String investedQtyOrVal;
	@JsonProperty("assest_type")
	public String assestType;
	@JsonProperty("installment_date")
	public String installmentDate;
	@JsonProperty("investment_value")
	public String investmentValue;
	@JsonProperty("traded_quantity")
	public String tradedQty;
	@JsonProperty("scrip_token")
	public String token;
	@JsonProperty("exchange")
	public String exchange;
	@JsonProperty("series")
	public String series;
	@JsonProperty("bDetail")
	public String bDetail;
	@JsonProperty("bModify")
	public String bModify;
	@JsonProperty("bStop")
	public String bStop;
	@JsonProperty("bView")
	public String bView;
	@JsonProperty("cap_price")
	public String capPrice;
	@JsonProperty("updated_status")
	public String updatedStatus;
	@JsonProperty("caa_remark")
	public String caaRemark;

}

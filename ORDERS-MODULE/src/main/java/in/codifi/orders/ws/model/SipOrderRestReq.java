package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipOrderRestReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("scrip_info")
	private SipScripInfo sipScripInfo;
	@JsonProperty("frequency_details")
	private SipFrequencyDetails sipFrequencyDetails;
	@JsonProperty("company_name")
	private String companyName;
	@JsonProperty("ltp")
	private String ltp;
	@JsonProperty("cap_price")
	private String capPrice;
	@JsonProperty("invested_by")
	private String investedBy;
	@JsonProperty("quantity_or_value")
	private String qtyOrValue;
	@JsonProperty("transaction_type")
	private String transType;
	@JsonProperty("status")
	private String status;
	@JsonProperty("basket_name")
	private String basketName;
	@JsonProperty("sip_id")
	private String sipId;

}

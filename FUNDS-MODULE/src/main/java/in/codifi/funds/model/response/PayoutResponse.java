package in.codifi.funds.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayoutResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String bankName;
	private String bankActNo;
	private String segment;
	private String upiId;
	private String payMethod;
	private String amount;
	private String orderId;
	private String userId;
	private String paymentStatus;
	private String payment;
	private String receipt;
	private String ifscCode;
	private String clientName;
	private String device;
	private String paymentReason;
	private String bankCode;
	private String backofficeCode;
	private Date createdOn;
	private Date updatedOn;

}

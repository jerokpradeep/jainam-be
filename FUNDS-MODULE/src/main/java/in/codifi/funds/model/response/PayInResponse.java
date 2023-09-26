package in.codifi.funds.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayInResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String orderId;
	private String receiptId;
	private String bankName;
	private String accNum;
	private String exchSeg;
	private String paymentMethod;
	private String upiId;
	private double amount;
	private String request;
	private String paymentStatus;
	private int boUpdate;
	private String voucherNo;
	private Date createdOn;
	private Date updatedOn;

}

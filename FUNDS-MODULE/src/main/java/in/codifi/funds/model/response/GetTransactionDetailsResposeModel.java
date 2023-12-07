package in.codifi.funds.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTransactionDetailsResposeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String bankActNo;
	private String ifscCode;
	private String segment;
	private int amount;
	private String request;
	private String payoutReason;
	private String createdOn;
	private String id;
	private String tableName;
	private String mode;
	private String status;
	private String fullDate;
	private String amountInCurrency;
}

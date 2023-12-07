package in.codifi.funds.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class WithdrawalReqModel {

	private String amount;
	private String bankActNo;
	private String WithdrawalRequestId;
	private String ifscCode;
	private String segment;
	private String payoutReason;
}

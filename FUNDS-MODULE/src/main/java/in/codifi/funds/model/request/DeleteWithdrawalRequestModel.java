package in.codifi.funds.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteWithdrawalRequestModel {

	private String bankActNo;
	private String ifscCode;
	private String segment;
	private int WithdrawalRequestId;
}

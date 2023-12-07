package in.codifi.funds.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteWithdrawalReqModel {

	private String APIKey;
	private String ClientCode;
	private int WithdrawalRequestId;
}

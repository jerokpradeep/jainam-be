package in.codifi.funds.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalRequestModel {

	private String APIKey;
	private String ClientCode;
	private String Amount;
	private String BankAccountNo;
//	private String WithdrawalRequestId;
}

package in.codifi.admin.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequestModel {

	private String name;
	private String userId;
	private String mobileNo;
	private String emailId;
	private String pincode;
	private String loanAmount;
	private String incomeRange;
	private String loanRequiredFor;
	private String fromDate;
	private String toDate;

}

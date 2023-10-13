package in.codifi.loan.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long Id;
	private String name;
	private String clientId;
	private String mobileNo;
	private String emailId;
	private String loanAmount;
	private String incomeRange;
	private String loanRequiredFor;
	private String fromDate;
	private String toDate;

}

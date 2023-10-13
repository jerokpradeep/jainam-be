package in.codifi.loan.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_LOAN_AGAINST_PROPERTY")
public class LoanAgainstPropertyEntity extends CommonEntity implements Serializable {

	/**
	 * @author LOKESH
	 */

	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "loan_amount")
	private String loanAmount;

	@Column(name = "income_range")
	private String incomeRange;

	@Column(name = "loan_required_for")
	private String loanRequiredFor;

}
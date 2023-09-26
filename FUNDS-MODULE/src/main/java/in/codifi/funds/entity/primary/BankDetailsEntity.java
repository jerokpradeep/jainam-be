package in.codifi.funds.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_BANK_DETAILS")
public class BankDetailsEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "mobile_number")
	private String mobileNo;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "account_number")
	private String accNo;

	@Column(name = "ifsc_code")
	private String ifscCode;

	@Column(name = "BANK_NAME")
	private String bankName;

	@Column(name = "DEFAULT_BANK")
	private String defaultBank;

}

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
@Entity(name = "CLIENT_BANK_DETAILS")
public class ClientBankDetailsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "my_row_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long myRowId;

	@Column(name = "CLIENT_ID")
	private String clientId;

	@Column(name = "CLIENT_NAME")
	private String clientName;

	@Column(name = "MOBILE")
	private String mobile;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "BRANCH_CODE_NEW")
	private String branchCodeNew;

	@Column(name = "BANK_ACNO")
	private String bankAcno;

	@Column(name = "CLIENT_BANK_NAME")
	private String clientBankName;

	@Column(name = "CLIENT_BANK_ADDRESS")
	private String clientBankAddress;

	@Column(name = "DEFAULT_ACC_BANK")
	private String defaultAccBank;

	@Column(name = "BANK_ACCTYPE")
	private String bankAcctype;

	@Column(name = "IFSCCODE")
	private String ifscCode;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "UCC_Response_Date")
	private String UCCResponseDate;

}

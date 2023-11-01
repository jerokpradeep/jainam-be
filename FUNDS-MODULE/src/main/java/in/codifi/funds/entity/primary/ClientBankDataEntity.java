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
@Entity(name = "TBL_CLIENT_BANK_DATA")
public class ClientBankDataEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "OOWNCODE")
	private String owncode;

	@Column(name = "CTERMCODE")
	private String termCode;

	@Column(name = "BANKCODE")
	private String bankCode;

	@Column(name = "FIBSACCT")
	private String fibsacct;

	@Column(name = "BANKACCOUNTNUMBER")
	private String bankAccountNumber;

	@Column(name = "BANKACCOUNTTYPE")
	private String bankAccountType;

	@Column(name = "CIFSCCODE")
	private String ifscCode;

	@Column(name = "CMICRBANKCODE")
	private String micrBankCode;

	@Column(name = "BANKBRANCHCODE")
	private String bankBranchCode;

}

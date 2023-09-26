package in.codifi.scrips.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "TBL_ASM_GSM")
@Getter
@Setter
public class PromptEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "ISIN")
	private String isin;
	@Column(name = "SYMBOL")
	private String symbol;
	@Column(name = "COMPANY_NAME")
	private String companyName;
	@Column(name = "TOKEN")
	private String token;
	@Column(name = "EXCH")
	private String exch;
	@Column(name = "MSG")
	private String msg;
	@Column(name = "TYPE")
	private String type;
	@Column(name = "PROMPT")
	private String prompt;
	@Column(name = "SEVERITY")
	private String severity;

}

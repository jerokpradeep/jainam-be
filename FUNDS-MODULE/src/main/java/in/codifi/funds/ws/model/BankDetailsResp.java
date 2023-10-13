package in.codifi.funds.ws.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDetailsResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("code")
	public int code;
	@JsonProperty("data")
	public List<DataDetails> data;
	@JsonProperty("data_bank")
	public List<DataBankDetails> dataBank;
	@JsonProperty("message")
	public String message;
	@JsonProperty("status")
	public String status;
}

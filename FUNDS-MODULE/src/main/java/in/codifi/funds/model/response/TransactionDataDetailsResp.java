package in.codifi.funds.model.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDataDetailsResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	public String id;
	@JsonProperty("tableName")
	public String tableName;
	@JsonProperty("date")
	public String date;
	@JsonProperty("amount")
	public int amount;
	@JsonProperty("mode")
	public String mode;
	@JsonProperty("status")
	public String status;
	@JsonProperty("fullDate")
	public String fullDate;
	@JsonProperty("amountInCurrency")
	public String amountInCurrency;
}

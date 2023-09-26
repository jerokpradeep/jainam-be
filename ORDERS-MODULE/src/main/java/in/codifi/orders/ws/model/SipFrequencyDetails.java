package in.codifi.orders.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipFrequencyDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("frequency_type")
	private String frequencyType;
	@JsonProperty("frequency_days")
	private String frequencyDays;
	@JsonProperty("frequency_start_date")
	private String frequencyStartDate;
	@JsonProperty("frequency_specific_date")
	private String frequencySpecificDate;
	@JsonProperty("frequency_monthly_option")
	private String frequencyMonthlyOption;
	@JsonProperty("no_of_installments")
	private String noOfInstallments;

}

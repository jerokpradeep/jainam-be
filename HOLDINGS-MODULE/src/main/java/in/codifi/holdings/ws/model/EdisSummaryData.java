package in.codifi.holdings.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisSummaryData {

	@JsonProperty("ResponseStatus")
	public Boolean responseStatus;
	@JsonProperty("DPDetail")
	public EdisSummaryDPDetails dPDetail;
	@JsonProperty("EDISCDSLValidation")
	public List<EDISCDSLValidation> eDISCDSLValidation;
	@JsonProperty("DPQtyTable")
	public List<DPQtyTable> dPQtyTable;

}

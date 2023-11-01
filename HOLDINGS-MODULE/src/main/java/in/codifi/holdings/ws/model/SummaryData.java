package in.codifi.holdings.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryData {
	
	@JsonProperty("edis_summary")
	public List<DataEdisSummary> edisSummary;
	@JsonProperty("edis_config")
	public List<DataEdisConfig> edisConfig;

}

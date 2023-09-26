package in.codifi.alerts.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeStatRespModel {

	@JsonProperty("stat")
	private String stat;
	@JsonProperty("emsg")
	private String emsg;
	@JsonProperty("exch")
	private String exch;
	@JsonProperty("exchstat")
	private String exchstat;
	@JsonProperty("exchtype")
	private String exchtype;

}

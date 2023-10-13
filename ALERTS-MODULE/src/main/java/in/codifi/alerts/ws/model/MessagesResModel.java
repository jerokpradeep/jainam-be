package in.codifi.alerts.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagesResModel {

	@JsonProperty("stat")
	private String stat;
	@JsonProperty("emsg")
	private String emsg;
	@JsonProperty("norentm")
	private String norentm;
	@JsonProperty("msgtyp")
	private String msgtyp;
	@JsonProperty("dmsg")
	private String dmsg;

}

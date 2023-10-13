package in.codifi.client.model.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorRestResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("returncode")
	public int returncode;
	@JsonProperty("ucc")
	public String ucc;
	@JsonProperty("view")
	public String view;
	@JsonProperty("source")
	public String source;
	@JsonProperty("timestamp")
	public String timestamp;
	@JsonProperty("callbackurl")
	public String callbackurl;

}

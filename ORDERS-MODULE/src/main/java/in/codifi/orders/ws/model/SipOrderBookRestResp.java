package in.codifi.orders.ws.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipOrderBookRestResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("status")
	public String status;
	@JsonProperty("code")
	public String code;
	@JsonProperty("message")
	public String message;
	@JsonProperty("data")
	public List<SipOrderBookRespData> data;
	@JsonProperty("metadata")
	public SipOrderBookMetadata metadata;

}

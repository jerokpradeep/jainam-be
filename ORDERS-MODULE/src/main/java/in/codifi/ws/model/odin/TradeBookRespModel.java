package in.codifi.ws.model.odin;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.codifi.orders.ws.model.TradeBookSuccess;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeBookRespModel implements Serializable {
	
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
	public List<TradeBookSuccess> data;
	@JsonProperty("metadata")
	public TradeBookMetadata metadata;

}

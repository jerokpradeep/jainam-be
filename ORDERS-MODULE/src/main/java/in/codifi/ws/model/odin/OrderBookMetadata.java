package in.codifi.ws.model.odin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("total_records")
	public int totalRecords;
	@JsonProperty("all_records")
	public int allRecords;
	@JsonProperty("completed_records")
	public int completedRecords;
	@JsonProperty("open_records")
	public int openRecords;

}

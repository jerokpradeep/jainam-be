package in.codifi.client.model.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductTypesExchModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("BSE_FO")
	public List<String> BSE_FO;
	@JsonProperty("NSE_FO")
	public List<String> NSE_FO;
	@JsonProperty("NSE_EQ")
	public List<String> NSE_EQ;
	@JsonProperty("NSE_OTS")
	public List<String> NSE_OTS;
	@JsonProperty("NSE_CUR")
	public List<String> NSE_CUR;
	@JsonProperty("MCX_FO")
	public List<String> MCX_FO;
	@JsonProperty("NCDEX_FO")
	public List<String> NCDEX_FO;
	@JsonProperty("BSE_EQ")
	public List<String> BSE_EQ;

}

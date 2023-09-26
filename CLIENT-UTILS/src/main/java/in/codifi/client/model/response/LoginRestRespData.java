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
public class LoginRestRespData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("user_name")
	public String userName;
	@JsonProperty("login_time")
	public String loginTime;
	@JsonProperty("exchanges")
	public List<String> exchanges;
	@JsonProperty("bcastExchanges")
	public List<String> bcastExchanges;
	@JsonProperty("product_types")
	public List<String> productTypes;
	@JsonProperty("product_types_exchange")
	public ProductTypesExchModel product_types_exchange;
	@JsonProperty("mpin_enabled")
	public boolean mpin_enabled;
	@JsonProperty("fingerprint_enabled")
	public boolean fingerprint_enabled;
	@JsonProperty("others")
	public OthersRespModel others;
	@JsonProperty("user_id")
	public String user_id;
	@JsonProperty("alias_id")
	public String aliasId;

}

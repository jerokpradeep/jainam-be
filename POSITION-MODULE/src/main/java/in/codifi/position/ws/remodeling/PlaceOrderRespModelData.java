package in.codifi.position.ws.remodeling;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceOrderRespModelData implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("orderId")
	public String orderId;

}

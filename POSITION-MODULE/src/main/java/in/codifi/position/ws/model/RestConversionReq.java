package in.codifi.position.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestConversionReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("exchange")
	private String exch;
	@JsonProperty("scrip_token")
	private int scripToken;
	@JsonProperty("transaction_type")
	private String transType;
	@JsonProperty("quantity")
	private int qty;
	@JsonProperty("old_product_type")
	private String oldPrdType;
	@JsonProperty("new_product_type")
	private String newPrdType;
	@JsonProperty("bo_order_id")
	private String boOrderId;

}

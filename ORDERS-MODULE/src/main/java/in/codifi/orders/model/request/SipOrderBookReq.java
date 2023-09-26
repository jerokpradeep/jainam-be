package in.codifi.orders.model.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SipOrderBookReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String offset;
	private String limit;
	private String orderStatus;

}

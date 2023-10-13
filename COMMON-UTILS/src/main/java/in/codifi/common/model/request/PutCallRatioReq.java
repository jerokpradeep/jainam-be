package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutCallRatioReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String symbol;
	private String expiryDate;

}

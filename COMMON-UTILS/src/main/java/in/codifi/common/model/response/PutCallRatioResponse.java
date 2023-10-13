package in.codifi.common.model.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutCallRatioResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String symbol;
	private String expiryDate;
	private String date;
	private String putOI;
	private String callOI;
	private String totalOI;
	private String PCRatioOI;
	private String putQty;
	private String callQty;
	private String totalQty;
	private String PCRatioQty;

}

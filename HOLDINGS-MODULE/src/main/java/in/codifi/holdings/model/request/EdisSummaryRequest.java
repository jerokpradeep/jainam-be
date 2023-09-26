package in.codifi.holdings.model.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisSummaryRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userCode;
	private String sellQty;
	private String segId;
	private String token;
	private String depository;
	private String summaryMktSegId;
	private String productType;

}

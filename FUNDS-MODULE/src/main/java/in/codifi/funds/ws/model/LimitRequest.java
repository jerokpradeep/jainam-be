package in.codifi.funds.ws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String groupId;
	private String periodicityName;
	private int productType;

}

package in.codifi.common.ws.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportAndResistanceRestResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String stat;
	private SupportAndResistanceResult result;
	private String message;

}

package in.codifi.scrips.ws.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityInfoRestReqModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uid;
	private String exch;
	private String token;

}

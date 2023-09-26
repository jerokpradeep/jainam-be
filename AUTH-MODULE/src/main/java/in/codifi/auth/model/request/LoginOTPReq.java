package in.codifi.auth.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginOTPReq implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user_id;
	private String api_key;
	private String source;

}

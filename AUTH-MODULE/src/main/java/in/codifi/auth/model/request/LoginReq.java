package in.codifi.auth.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String password;

}

package in.codifi.position.entity.primary;

import java.io.Serializable;
import java.security.PublicKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PublicKey publicKey4;
	private String tomcatcount;
	private String stringPkey4;
	private String account_id;

}

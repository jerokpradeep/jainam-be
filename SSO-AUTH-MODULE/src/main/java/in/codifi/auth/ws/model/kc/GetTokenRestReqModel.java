package in.codifi.auth.ws.model.kc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTokenRestReqModel {

	private String userId;
	private String password;
}

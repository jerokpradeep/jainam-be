package in.codifi.sso.auth.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAccessTokenDataReqModel {

	private String client_id;
	private String client_secret;
	private String code;
	private String grant_type;
	private String redirect_uri;
}

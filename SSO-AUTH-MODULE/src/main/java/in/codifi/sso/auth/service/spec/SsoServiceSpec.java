package in.codifi.sso.auth.service.spec;

import in.codifi.sso.auth.model.request.GetAccessTokenReqModel;

public interface SsoServiceSpec {

	Object getAccessToken(String clientId, String clientSecret, String code, String grantType, String redirectUri);

	Object getInfo(String userId, String token);

	Object getUserToken(GetAccessTokenReqModel model);

}

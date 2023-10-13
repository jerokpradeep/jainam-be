package in.codifi.odn.service.spec;

import in.codifi.odn.model.AuthRequest;
import in.codifi.odn.model.AuthResponse;
import in.codifi.odn.model.RegisterApiReq;
import in.codifi.odn.model.RegisterApiResp;

public interface AuthenticationServiceSpec {
	
	AuthResponse getAuthResponse(AuthRequest request);
	
	RegisterApiResp registerApi(RegisterApiReq request);

	String getWsEndPoint();

	String getAuthToken();

}

package in.codifi.sso.auth.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.sso.auth.controller.spec.SsoControllerSpec;
import in.codifi.sso.auth.model.request.GetAccessTokenReqModel;
import in.codifi.sso.auth.service.spec.SsoServiceSpec;
import in.codifi.sso.auth.utility.AppConstants;
import in.codifi.sso.auth.utility.AppUtil;
import in.codifi.sso.auth.utility.PrepareResponse;
import in.codifi.sso.auth.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/auth")
public class SsoController implements SsoControllerSpec {

	@Inject
	SsoServiceSpec serviceSpec;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	AppUtil appUtil;

	/**
	 * Method to authorize Vendor
	 * 
	 * @author dinesh
	 * @param vendorReqModel
	 * @return
	 */
	@Override
	public Object getUserDetails(String clientId, String clientSecret, String code, String grantType,
			String redirectUri) {
		return serviceSpec.getAccessToken(clientId, clientSecret, code, grantType, redirectUri);
	}

	/**
	 * Method to get user token
	 * 
	 * @author Dinesh Kumar
	 * @param clientId
	 * @param clientSecret
	 * @param code
	 * @param grantType
	 * @param redirectUri
	 * @return
	 */
	@Override
	public Object getUserToken(GetAccessTokenReqModel model) {
		return serviceSpec.getUserToken(model);
	}
	
	@Override
	public Object getUserTokenByParam(String code, String redirectUri, String grantType, String clientId,
			String clientSecret) {
		System.out.println("code-"+code+">>>>clientId -" +clientId);
		return serviceSpec.getAccessToken(clientId, clientSecret, code, grantType, redirectUri);
	}

	/**
	 * Method to get access user info from keycloak
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public Object getInfo() {
		ClinetInfoModel info = appUtil.getClientInfo();
		String token = appUtil.getToken();
//		String token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDExRjNEMjU3RjQ0ODY4MUFCRkQ1NDc3MUFDMkZEIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMDI0NTc5OSwiaWF0IjoxNzAwMjA0Mjc3fQ.FEKSyW4vJ_EwnNLwJVuGEY-0rpoWetJmd3XCE3Ck-Bk";
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId()) || StringUtil.isNullOrEmpty(token)) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return serviceSpec.getInfo(info.getUserId(), token);
	}

}

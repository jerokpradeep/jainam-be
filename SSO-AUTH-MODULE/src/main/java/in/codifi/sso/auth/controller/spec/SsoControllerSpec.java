package in.codifi.sso.auth.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import in.codifi.sso.auth.model.request.GetAccessTokenReqModel;

public interface SsoControllerSpec {

	/**
	 * Method to get access token from keycloak
	 * 
	 * @author Dinesh Kumar
	 */
	@Path("/get/token")
	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Object getUserDetails(@QueryParam(value = "client_id") String clientId,
			@QueryParam(value = "client_secret") String clientSecret, @QueryParam(value = "code") String code,
			@QueryParam(value = "grant_type") String grantType, @QueryParam(value = "redirect_uri") String redirectUri);

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
	@Path("/token/json")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Object getUserToken(GetAccessTokenReqModel model);

	/**
	 * Method to get user info
	 * 
	 * @author Dinesh Kumar
	 * @param req
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/info")
	Object getInfo();

	@Path("/token")
	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Object getUserTokenByParam(@FormParam("code") String code, @FormParam("redirect_uri") String redirectUri,
			@FormParam("grant_type") String grantType, @FormParam("client_id") String clientId,
			@FormParam("client_secret") String clientSecret);

}

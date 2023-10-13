package in.codifi.ameyo.ws.service.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.ameyo.ws.model.kc.GetTokenResponse;
import in.codifi.ameyo.ws.model.kc.GetUserInfoResp;

@RegisterRestClient(configKey = "ameyo-token-service")
@RegisterClientHeaders
public interface KcAmeyoTokenRestSpec {

	@Path("/users/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To get user info to check whether user exist or not")
	public List<GetUserInfoResp> getUserInfo(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@QueryParam(value = "username") String username,@QueryParam(value = "exact") String exact);
	
	@Path("/token")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@APIResponse(description = "Fetches access token for user creation")
	public GetTokenResponse fetchToken(@FormParam("client_id") String client_id,
			@FormParam("client_secret") String client_secret, @FormParam("grant_type") String grant_type,
			@FormParam("username") String userId, @FormParam("password") String password);
	
	@Path("/logout")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@APIResponse(description = "Logout")
	public void logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@FormParam("client_id") String clientId, @FormParam("client_secret") String clientSecret,
			@FormParam("refresh_token") String token, @FormParam("user_id") String userId);

}

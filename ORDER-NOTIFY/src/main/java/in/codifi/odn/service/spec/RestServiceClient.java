package in.codifi.odn.service.spec;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.odn.model.AuthRequest;
import in.codifi.odn.model.AuthResponse;
import in.codifi.odn.model.RegisterApiReq;
import in.codifi.odn.model.RegisterApiResp;

@Path("")
@RegisterRestClient(configKey="rest-api")
public interface RestServiceClient{
	
    @POST
    @Path("/Authenticate")
    @Produces(MediaType.APPLICATION_JSON)
	AuthResponse getAuthResponse(AuthRequest request);
    
    @POST
    @Path("/RegisterAPIRequest")
    @Produces(MediaType.APPLICATION_JSON)
	RegisterApiResp registerApi(RegisterApiReq request);
    
}
package in.codifi.odn.service.spec;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.odn.model.PushNotification;

@Path("/")
@RegisterRestClient(configKey = "push-api")
public interface PushClientService {

    @POST
    @Path("/fcm/send")
    @Produces("application/json")
    @ClientHeaderParam(name = "Authorization", value = "{generateAuthHeader}")
    Response send(PushNotification msg);

    default String generateAuthHeader() {
        return "key=" + ConfigProvider.getConfig().getValue("firebase.server_key", String.class);
    }
}
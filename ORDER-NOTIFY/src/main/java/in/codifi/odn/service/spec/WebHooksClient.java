package in.codifi.odn.service.spec;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.odn.model.OrderStatusModel;
import io.smallrye.mutiny.Uni;

@Path("")
@RegisterRestClient(configKey="webhooks-api")

public interface WebHooksClient{

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Void> sendOrderNotification(@RequestBody OrderStatusModel orderStatus);
    
}
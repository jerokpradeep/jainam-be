package in.codifi.ws.service.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import in.codifi.ws.model.odin.OrderResponse;

//@RegisterRestClient(configKey = "place-order-api")
//@RegisterClientHeaders
public interface OrdersRestSpecOdin {

	@Path("/regular")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
//	@APIResponse(description = "To get user info")
	public OrderResponse executePlaceorder(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@HeaderParam(HttpHeaders.AUTHORIZATION) String xApiKey, String placeordereReq);

}

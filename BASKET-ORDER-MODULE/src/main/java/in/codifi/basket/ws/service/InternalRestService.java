package in.codifi.basket.ws.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.utility.PrepareResponse;
import in.codifi.basket.ws.model.OrderDetails;
import in.codifi.basket.ws.service.spec.InternalRestServiceSpec;
import io.quarkus.logging.Log;

@ApplicationScoped
public class InternalRestService {

	@Inject
	@RestClient
	InternalRestServiceSpec internalRestServiceSpec;

	@Inject
	PrepareResponse prepareResponse;

	public List<GenericResponse> executeOrders(List<OrderDetails> orderDetails, String accessToken) {
		List<GenericResponse> response = null;
		ObjectMapper om = new ObjectMapper();
		try {
			String token = "Bearer " + accessToken;
			response = internalRestServiceSpec.placeOrder(token, orderDetails);
			try {
				System.out.println("Excute Basket Rest response--" + om.writeValueAsString(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} catch (ClientWebApplicationException e) {
			e.printStackTrace();
			Log.error(e.getMessage());
			int statusCode = e.getResponse().getStatus();

		}
		return response;

	}
}

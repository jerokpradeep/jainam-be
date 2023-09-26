package in.codifi.orders.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceOrderReqModelJson {

	private PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();

}

package in.codifi.orders.model.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderModuleInputs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OrderDetails> OrderDetails = new ArrayList<OrderDetails>();
	private String reqId;

}

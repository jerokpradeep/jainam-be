package in.codifi.orders.model.transformation;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericPositionSqrOffResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestTime;
	private String orderNo;
	private String message;

}

package in.codifi.position.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericOrderResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestTime;
	private String orderNo;
}
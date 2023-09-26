package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovingAverageReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String exchange;
	private String token;

}

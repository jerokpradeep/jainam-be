package in.codifi.cache.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminIndexModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String exch;
	private String exchangeSegment;
	private String token;
	private String alterToken;

}

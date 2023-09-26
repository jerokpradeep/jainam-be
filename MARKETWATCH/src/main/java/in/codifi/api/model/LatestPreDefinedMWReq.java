package in.codifi.api.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LatestPreDefinedMWReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mwId;
	private String scripName;
	private String ex;
	private String exSeg;
	private String token;
	private int sortingOrder;
	private String symbol;
	private String expDt;
	private String pdc;
	private String lotSize;
	private String tickSize;

}

package in.codifi.common.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectorScripDetailsResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String scripName;
	private int sortOrder;
	private String exchange;
	private Date expiry;
	private String token;
	private String pdc;
	private String symbol;
	
	private String companyName;

}

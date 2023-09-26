package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnoucementsScripDataResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int companyCode;
	private int mktSegmentId;
	private int ODINCode;
	private String symbol;
	private String series;
}

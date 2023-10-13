package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotPursuitDataResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sNo;
	private ShareHoldingsScripDataResp scripDataNSE;
	private ShareHoldingsScripDataResp scripDataBSE;
	private String companyCode;
	private String industryCode;
	private String secName;
	private String date;
	private String time;
	private String heading;
	private String caption;
	private String artText;
	private String flag;

}

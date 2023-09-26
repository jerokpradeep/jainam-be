package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnoucementsDataResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String date;
	private String companyCode;
	private String companyLongName;
	private AnnoucementsScripDataResp scripDataNSE;
	private AnnoucementsScripDataResp scripDataBSE;
	private String bseCode;
	private String symbol;
	private String caption;
	private String memo;

}

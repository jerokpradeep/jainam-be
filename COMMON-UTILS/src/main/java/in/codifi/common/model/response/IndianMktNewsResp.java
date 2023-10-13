package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndianMktNewsResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String SNo;
	private String secName;
	private String date;
	private String time;
	private String heading;
	private String caption;
	private String artText;
	private String companyCode;
	private String industryCode;
	private String flag;

}

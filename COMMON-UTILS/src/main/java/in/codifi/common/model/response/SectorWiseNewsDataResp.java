package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectorWiseNewsDataResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sNo;
	private String date;
	private String time;
	private String heading;
	private String caption;
	private String secName;

}

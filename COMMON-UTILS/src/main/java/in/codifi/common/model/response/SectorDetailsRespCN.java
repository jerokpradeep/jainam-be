package in.codifi.common.model.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectorDetailsRespCN implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int sectorList;
	private String sectorName;
	private List<SectorScripDetailsResp> scrips;
	private String imageUrl;
	private String threeMonths;
	private String sixMonths;
	private String oneYear;

}

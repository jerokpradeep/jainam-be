package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectorsModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int sectorList;
	private String sectorName;
	private String imageUrl;
	private String threeMonths;
	private String sixMonths;
	private String oneYear;

}

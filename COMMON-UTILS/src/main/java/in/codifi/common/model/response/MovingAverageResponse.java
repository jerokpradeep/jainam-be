package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovingAverageResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String companyCode;
	private String avgFifteenDays;
	private String avgTwentyDays;
	private String avgThirtyDays;
	private String avgFiftyDays;
	private String avgHunFiftydays;
	private String avgHundredDays;
	private String avgTwoHundredDays;

}

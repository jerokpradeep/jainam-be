package in.codifi.common.model.response;

import java.io.Serializable;
import java.util.List;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovingAverageResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	List<MovingAverageResponse> movingAverageNSE;
//	List<MovingAverageResponse> movingAverageBSE;
	
	List<JSONObject> movingAverageNSE;
	List<JSONObject> movingAverageBSE;

}

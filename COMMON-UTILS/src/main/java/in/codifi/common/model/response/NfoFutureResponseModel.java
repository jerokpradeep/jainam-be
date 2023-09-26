package in.codifi.common.model.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NfoFutureResponseModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String symbol;
	private int spotToken;
	private List<NfoFutureScripsResponseModel> scrips;

}

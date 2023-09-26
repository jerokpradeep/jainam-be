package in.codifi.common.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScripwiseNewsReqModel {
	
	private String exchSeg;
	private String symbol;
	private int token;
	private int pageable;

}

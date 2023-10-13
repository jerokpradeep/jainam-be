package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexDetailsRespModify implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mktSegmentId;
	private int token;
	private String index;
	private String indexDesc;
	private int isDefaultIndex;
	private String cIsIndex;

}

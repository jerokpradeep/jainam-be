package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyReqModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String marketSegmentId;
	private String token;
	private String type;
	private String periods;
}

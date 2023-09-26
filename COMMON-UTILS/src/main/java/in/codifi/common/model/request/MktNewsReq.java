package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MktNewsReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String secName;
	private int pageSize;

}

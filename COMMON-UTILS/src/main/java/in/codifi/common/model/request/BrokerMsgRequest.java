package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrokerMsgRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pageNo;
	private String pageSize;

}

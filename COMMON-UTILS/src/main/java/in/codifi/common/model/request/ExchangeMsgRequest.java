package in.codifi.common.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeMsgRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String exchange;
	private String noOfHrs;
	private String pageNo;
	private String pageSize;

}

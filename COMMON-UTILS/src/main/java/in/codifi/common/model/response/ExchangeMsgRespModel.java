package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeMsgRespModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String segmentId;
	private String time;
	private String messages;

}
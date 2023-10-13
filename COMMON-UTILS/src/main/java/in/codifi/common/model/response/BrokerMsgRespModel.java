package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrokerMsgRespModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String msgSrNo;
	private String sendMsgTime;
	private String subject;
	private String message;
	private String msgType;

}
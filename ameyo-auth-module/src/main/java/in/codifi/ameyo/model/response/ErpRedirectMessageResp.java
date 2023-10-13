package in.codifi.ameyo.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpRedirectMessageResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int Success;
	private ErpMessageErrorResp error;
	private ErpMessageDataResp data;

}

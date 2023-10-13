package in.codifi.ameyo.model.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErpRedirectResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErpRedirectMessageResp message;
	private String home_page;
	private String full_name;
	private String exc;

}

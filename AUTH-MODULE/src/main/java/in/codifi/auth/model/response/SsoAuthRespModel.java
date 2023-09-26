package in.codifi.auth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SsoAuthRespModel {
	
	private String accessToken;
	private String redirectUrl;
	private String clientId;

}

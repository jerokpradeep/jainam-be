package in.codifi.auth.model.request;

import java.io.Serializable;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRestReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	private String user_id;
	@Column(name = "login_type")
	private String login_type;
	@Column(name = "password")
	private String password;
	@Column(name = "second_auth_type")
	private String second_auth_type;
	@Column(name = "second_auth")
	private String second_auth = "";
	@Column(name = "api_key")
	private String api_key;
	@Column(name = "source")
	private String source;
	@Column(name = "UDID")
	private String UDID;
	@Column(name = "version")
	private String version;
	@Column(name = "iosversion")
	private String iosversion;
	@Column(name = "build_version")
	private String build_version;

}

package in.codifi.odn.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class WebHooksDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String call_back_url;
	private int error_count;
	private String app_code;
	private boolean isSubscribed;

}
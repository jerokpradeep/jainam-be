package in.codifi.admin.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogDetailsResponseModel {
	private int uniqueMobileLogin;
	private int uniqueWebLogin;
	private int uniqueApiLogin;
	private int totalMobileLogin;
	private int totalWebLogin;
	private int totalApiLogin;
	private int totalLogin;
	private String date;

}

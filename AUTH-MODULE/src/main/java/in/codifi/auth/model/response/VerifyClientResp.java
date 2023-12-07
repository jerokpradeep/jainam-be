package in.codifi.auth.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyClientResp {

	private String isExist;
	private String mobileNo;
	private String emailID;
	private String name;
	private String userId;
	private boolean isUpdateReq;
	private String roll;
	private boolean isBioEnabled;

}

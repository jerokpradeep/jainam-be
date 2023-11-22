package in.codifi.auth.ws.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuickAuthReqModel {

	@JsonProperty("uid")
	private String uId;
	@JsonProperty("imei")
	private String imei;
	@JsonProperty("appkey")
	private String appKey;
	@JsonProperty("apkversion")
	private String apkVersion;
	@JsonProperty("vc")
	private String vendorCode;
	@JsonProperty("source")
	private String source;
	@JsonProperty("ipaddr")
	private String ipAddress;

}

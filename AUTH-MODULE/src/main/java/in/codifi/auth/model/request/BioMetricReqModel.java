package in.codifi.auth.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BioMetricReqModel {

	private String userId;
	private String deviceId;
	private String deviceType;
	private String enable;
}

package in.codifi.alerts.model.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertResp {

	private String userId;
	private String alertName;
	private String scripName;
	private String exch;
	private String token;
	private String alertType;
	private String operator;
	private double value;
	private String triggerStatus = "0";
	private Date expiry;
	private String triggeredTime;
	private String exchSeg;
	private String alertId;
	private long id;
	private String createdOn;
	private Date updatedOn;
	private String createdBy;
	private String updatedBy;
	private int activeStatus = 1;

}

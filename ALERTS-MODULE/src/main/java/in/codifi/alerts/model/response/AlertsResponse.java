package in.codifi.alerts.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertsResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	private String userId;
	private String alertId;
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
	private Date createdOn;
	private Date updatedOn;
	private String createdBy;
	private String updatedBy;
	private int activeStatus = 1;

}

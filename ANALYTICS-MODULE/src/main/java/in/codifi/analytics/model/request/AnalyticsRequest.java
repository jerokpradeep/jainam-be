package in.codifi.analytics.model.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyticsRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String marketSegmentID;
	private String scripToken;
	private String scripDescription;
	private String recoType;
	private String price;
	private String squareoffPrice;
	private String stoplossPrice;
	private String messageTitle;
	private String reportUrl;
	private String endTime;
	private String uniqueID;
	private String topicNo;
	private String topicName;
	private String channelIds;
	private String isHot;
	private String isPopup;
	private String isMessageBar;
	private String symbol;
	private String companyDesc;
	private String userCategoryId;
	private String userDetails;
	private String smsVendor;
	private String tags;
	private String categoryMode;
	private String messageCategoryId;

}

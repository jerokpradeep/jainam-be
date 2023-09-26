package in.codifi.analytics.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_analytics")
public class AnalyticsEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "market_segment_id")
	private String marketSegmentID;

	@Column(name = "scrip_token")
	private String scripToken;

	@Column(name = "scrip_description")
	private String scripDescription;

	@Column(name = "reco_type")
	private String recoType;

	@Column(name = "price")
	private String price;

	@Column(name = "square_off_price")
	private String squareoffPrice;

	@Column(name = "stop_loss_price")
	private String stoplossPrice;

	@Column(name = "message_title")
	private String messageTitle;

	@Column(name = "report_url")
	private String reportUrl;

	@Column(name = "end_time")
	private String endTime;

	@Column(name = "unique_id")
	private String uniqueID;

	@Column(name = "topic_no")
	private String topicNo;

	@Column(name = "topic_name")
	private String topicName;

	@Column(name = "channel_ids")
	private String channelIds;

	@Column(name = "is_hot")
	private String isHot;

	@Column(name = "is_popup")
	private String isPopup;

	@Column(name = "is_message_bar")
	private String isMessageBar;

	@Column(name = "symbol")
	private String symbol;

	@Column(name = "company_desc")
	private String companyDesc;

	@Column(name = "user_category_id")
	private String userCategoryId;

	@Column(name = "user_details")
	private String userDetails;

	@Column(name = "sms_vendor")
	private String smsVendor;

	@Column(name = "tags")
	private String tags;

	@Column(name = "category_mode")
	private String categoryMode;

	@Column(name = "message_category_id")
	private String messageCategoryId;

}

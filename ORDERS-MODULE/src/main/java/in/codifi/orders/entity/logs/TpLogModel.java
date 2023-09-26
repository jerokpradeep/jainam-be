package in.codifi.orders.entity.logs;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TpLogModel {

	private String reqId;
	private String tpUri;
	private Timestamp inTime;
	private Timestamp outTime;
	private String method;
	private String reqBody;
	private String resBody;
	private String contentType;
	private Timestamp updatedOn;

}

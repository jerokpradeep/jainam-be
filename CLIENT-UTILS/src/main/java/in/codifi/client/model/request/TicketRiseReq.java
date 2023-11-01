package in.codifi.client.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRiseReq {
	
	private String userId;
	private String subject;
	private String description;

}

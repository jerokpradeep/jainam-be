package in.codifi.admin.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexRequestModel {

	private String exch;
	private String exchangeSegment;
	private String token;
	private String alterToken;
}

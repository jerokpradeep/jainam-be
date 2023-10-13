package in.codifi.orders.entity.logs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogModel {

	private String reqId;
	private String className;
	private String method;
	private String error;

}

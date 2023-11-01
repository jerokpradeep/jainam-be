package in.codifi.admin.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedInRequestModel {

	private String counts;
	private String web;
	private String mob;
	private String api;
}

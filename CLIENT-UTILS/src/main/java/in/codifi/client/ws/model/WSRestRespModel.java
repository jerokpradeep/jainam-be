package in.codifi.client.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class WSRestRespModel {
	
	@JsonProperty("status")
	private String status;
	@JsonProperty("message")
	private String message;
	@JsonProperty("result")
	private List<ResultModel> result;

}

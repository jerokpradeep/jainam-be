package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsecodeStatusModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodAndModel;
	private int responsecode;
	private int count;

}

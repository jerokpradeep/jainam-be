package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NfoFutureScripsResponseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String formattedInsName;
	private String expiry;
	private double pdc;
	private String oi;
	private double volume;
	private int token;

}

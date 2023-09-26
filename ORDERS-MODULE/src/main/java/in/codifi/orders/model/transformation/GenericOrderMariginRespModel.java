package in.codifi.orders.model.transformation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericOrderMariginRespModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
//	private float marginUsed;
//	private float openingBalance;
	private String requiredMargin;
	private String MarginShortfall;
	private String availableMargin;
	private String brokerage;

}

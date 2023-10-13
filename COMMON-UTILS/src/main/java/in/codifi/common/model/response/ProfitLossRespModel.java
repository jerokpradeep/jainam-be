package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfitLossRespModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sNo;
	private String columnName;
	private String currentFinYr;
	private String yoy1;
	private String yoy2;
	private String yoy1Per;
	private String yoy2Per;

}

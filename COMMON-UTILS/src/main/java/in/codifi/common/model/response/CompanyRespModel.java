
package in.codifi.common.model.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRespModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String columnName;
	private String currentYear;
	private String year1;
	private String year2;

}

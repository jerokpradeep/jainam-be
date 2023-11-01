package in.codifi.admin.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.LoanRequestModel;
import in.codifi.admin.model.response.GenericResponse;

public interface LoanServiceSpec {

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> getLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> getLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> getLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);

}

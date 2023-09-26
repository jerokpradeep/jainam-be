package in.codifi.loan.service.spec;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.loan.model.request.LoanRequestModel;
import in.codifi.loan.model.response.GenericResponse;

public interface LoanServiceSpec {

	/**
	 * method to add loan details for housing
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> addLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to add loan details for Property
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> addLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to add loan details for Securities
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> addLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to add loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	RestResponse<GenericResponse> addLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	Response getLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	Response getLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	Response getLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 * 
	 * @return
	 */
	Response getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);

}

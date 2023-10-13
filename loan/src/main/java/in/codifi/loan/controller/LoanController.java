package in.codifi.loan.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.loan.controller.spec.LoanControllerSpec;
import in.codifi.loan.model.request.LoanRequestModel;
import in.codifi.loan.model.response.GenericResponse;
import in.codifi.loan.service.spec.LoanServiceSpec;

@Path("/loan")
public class LoanController implements LoanControllerSpec {

	@Inject
	LoanServiceSpec service;

	/**
	 * method to add loan details for housing
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> addLoanDetailsForHousing(LoanRequestModel model) {
		return service.addLoanDetailsForHousing(model);
	}

	/**
	 * method to add loan details for Property
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> addLoanDetailsForProperty(LoanRequestModel model) {
		return service.addLoanDetailsForProperty(model);
	}

	/**
	 * method to add loan details for Securities
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> addLoanDetailsForSecurities(LoanRequestModel model) {
		return service.addLoanDetailsForSecurities(model);
	}

	/**
	 * method to add loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> addLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		return service.addLoanDetailsForSmallMediumEnterprises(model);
	}

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	public Response getLoanDetailsForHousing(LoanRequestModel model) {
		return service.getLoanDetailsForHousing(model);
	}

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	public Response getLoanDetailsForProperty(LoanRequestModel model) {
		return service.getLoanDetailsForProperty(model);
	}

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	public Response getLoanDetailsForSecurities(LoanRequestModel model) {
		return service.getLoanDetailsForSecurities(model);
	}

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	public Response getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		return service.getLoanDetailsForSmallMediumEnterprises(model);
	}
}

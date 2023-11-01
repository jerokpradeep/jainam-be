package in.codifi.admin.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.LoanRequestModel;
import in.codifi.admin.model.response.GenericResponse;

public interface LoanControllerSpec {

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Path("/get/housing")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Path("/get/Property")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Path("/get/Securities")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Path("/get/MediumEnterprises")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);
}

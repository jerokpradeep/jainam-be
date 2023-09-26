package in.codifi.loan.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.loan.model.request.LoanRequestModel;
import in.codifi.loan.model.response.GenericResponse;

public interface LoanControllerSpec {

	/**
	 * method to add loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Path("/add/housing")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> addLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to add loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Path("/add/Property")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> addLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to add loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Path("/add/Securities")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> addLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to add loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Path("/add/MediumEnterprises")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> addLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Path("/get/housing")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLoanDetailsForHousing(LoanRequestModel model);

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Path("/get/Property")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLoanDetailsForProperty(LoanRequestModel model);

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Path("/get/Securities")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLoanDetailsForSecurities(LoanRequestModel model);

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Path("/get/MediumEnterprises")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model);
}

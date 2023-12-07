package in.codifi.funds.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.funds.model.request.DeleteWithdrawalRequestModel;
import in.codifi.funds.model.request.WithdrawalReqModel;
import in.codifi.funds.model.response.GenericResponse;

public interface OdinPaymentControllerSpec {

	/**
	 * Method to get Withdrawal Details
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/WithdrawalDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getWithdrawalDetails();

	/**
	 * Method to create Withdrawal request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/WithdrawalRequest")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createWithdrawalRequest(WithdrawalReqModel model);

	/**
	 * Method to get transaction list
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/transactionlist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getTransactionList();

	/**
	 * Method to delete Withdrawal Request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/deleteWithdrawalRequest")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteWithdrawalRequest(DeleteWithdrawalRequestModel model);
}

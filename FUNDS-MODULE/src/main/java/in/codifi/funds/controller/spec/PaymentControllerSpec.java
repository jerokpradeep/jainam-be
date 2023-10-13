package in.codifi.funds.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.funds.model.request.FormDataModel;
import in.codifi.funds.model.request.PaymentReqModel;
import in.codifi.funds.model.request.UPIReqModel;
import in.codifi.funds.model.request.VerifyPaymentReqModel;
import in.codifi.funds.model.response.GenericResponse;

public interface PaymentControllerSpec {

	/**
	 * Method to create new payment details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createNewPayment(PaymentReqModel limitOrderEntity);

	/**
	 * Method to get upi id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/upi")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getUPIId();

	/**
	 * Method to set upi id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/upi/update")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> setUPIId(UPIReqModel model);

	/**
	 * Method to get payment details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/details")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getPaymentDetails();

	/**
	 * Method to verify payments
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/verify")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> verifyPayments(VerifyPaymentReqModel model);

	/**
	 * Method to payout
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/payout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> payOut(PaymentReqModel model);

	/**
	 * method to get the the payout check balance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/payout/checkbalance")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> payOutCheckBalance();

	/**
	 * method to get the the payout check balance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/payout/details")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getPayOutDetails();

	/**
	 * Method to cancelPayOut
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/payout/cancel")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> cancelPayout(PaymentReqModel model);

	/**
	 * Method to update PayOut
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/payout/update")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> updatePayout(PaymentReqModel model);

	/**
	 * Method to upload bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/upload/bank")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public RestResponse<GenericResponse> uploadBankDetails(@MultipartForm FormDataModel file);

	/**
	 * Method to get last five payin transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/payin/transactions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getpayInTransactions();

	/**
	 * Method to get last five payout transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/payout/transactions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getpayOutTransactions();

}

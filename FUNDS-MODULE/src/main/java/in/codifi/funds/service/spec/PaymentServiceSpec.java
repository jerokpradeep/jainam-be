package in.codifi.funds.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.model.request.FormDataModel;
import in.codifi.funds.model.request.PaymentReqModel;
import in.codifi.funds.model.request.UPIReqModel;
import in.codifi.funds.model.request.VerifyPaymentReqModel;
import in.codifi.funds.model.response.GenericResponse;

public interface PaymentServiceSpec {

	/**
	 * method to create new payment details
	 * 
	 * @author Gowthaman M
	 * @param limitOrderEntity
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> createNewPayment(PaymentReqModel limitOrderEntity, ClinetInfoModel info);

	/**
	 * method to get upi id from database
	 * 
	 * @author Gowthaman M
	 * @param
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getUPIId(ClinetInfoModel info);

	/**
	 * method to set upi id from database
	 * 
	 * @author Gowthaman M
	 * @param model
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> setUPIId(ClinetInfoModel info, UPIReqModel model);

	/**
	 * method to get payment details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getPaymentDetails(ClinetInfoModel info);

	/**
	 * method to verify payment
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> verifyPayments(ClinetInfoModel info, VerifyPaymentReqModel model);

	/**
	 * method to get pay out details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getPayOutDetails(ClinetInfoModel info);

	/**
	 * method to get pay out check bank details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> payOutCheckBalance(ClinetInfoModel info);

	/**
	 * method to get pay out details
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> payOut(ClinetInfoModel info, PaymentReqModel model);

	/**
	 * Method to cancel pay out
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> cancelPayout(ClinetInfoModel info, PaymentReqModel model);

	/**
	 * Method to update Payout
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> updatePayout(ClinetInfoModel info, PaymentReqModel model);

	/**
	 * Method to upload bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> uploadBankDetails(FormDataModel file);

	/**
	 * Method to get last five payin transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> getpayInTransactions(ClinetInfoModel info);

	/**
	 * Method to get last five payout transaction
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> getpayOutTransactions(ClinetInfoModel info);

}

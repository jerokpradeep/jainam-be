package in.codifi.funds.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.model.request.DeleteWithdrawalRequestModel;
import in.codifi.funds.model.request.WithdrawalReqModel;
import in.codifi.funds.model.response.GenericResponse;

public interface OdinPaymentServiceSpec {

	/**
	 * Method to get Withdrawal Details
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getWithdrawalDetails(ClinetInfoModel info);

	/**
	 * Method to create Withdrawal request
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> createWithdrawalRequest(ClinetInfoModel info, WithdrawalReqModel model);

	/**
	 * Method to get transaction list
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getTransactionList(ClinetInfoModel info);

	/**
	 * Method to delete Withdrawal Request
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> deleteWithdrawalRequest(ClinetInfoModel info, DeleteWithdrawalRequestModel model);

}

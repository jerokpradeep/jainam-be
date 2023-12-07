package in.codifi.funds.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.controller.spec.OdinPaymentControllerSpec;
import in.codifi.funds.model.request.DeleteWithdrawalRequestModel;
import in.codifi.funds.model.request.WithdrawalReqModel;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.service.spec.OdinPaymentServiceSpec;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/odinfunds")
public class OdinPaymentController implements OdinPaymentControllerSpec {

	@Inject
	OdinPaymentServiceSpec odinPaymentServiceSpec;
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * Method to get Withdrawal Details
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWithdrawalDetails() {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("wcm549");
		return odinPaymentServiceSpec.getWithdrawalDetails(info);
	}

	/**
	 * Method to create Withdrawal request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createWithdrawalRequest(WithdrawalReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("WCM549");
		return odinPaymentServiceSpec.createWithdrawalRequest(info, model);
	}

	/**
	 * Method to get transaction list
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTransactionList() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("WCM549");
		return odinPaymentServiceSpec.getTransactionList(info);
	}

	/**
	 * Method to delete Withdrawal Request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteWithdrawalRequest(DeleteWithdrawalRequestModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("WCM549");
		return odinPaymentServiceSpec.deleteWithdrawalRequest(info, model);
	}
}

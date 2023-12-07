package in.codifi.funds.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.config.RestServiceProperties;
import in.codifi.funds.model.request.DeleteWithdrawalReqModel;
import in.codifi.funds.model.request.DeleteWithdrawalRequestModel;
import in.codifi.funds.model.request.GetWithdrawalDetailsReqModel;
import in.codifi.funds.model.request.WithdrawalReqModel;
import in.codifi.funds.model.request.WithdrawalRequestModel;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.service.spec.OdinPaymentServiceSpec;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.service.OdinPaymentRestService;

@ApplicationScoped
public class OdinPaymentService implements OdinPaymentServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	OdinPaymentRestService odinPaymentRestService;
	@Inject
	RestServiceProperties props;

	/**
	 * Method to get Withdrawal Details
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWithdrawalDetails(ClinetInfoModel info) {
		Object response = new Object();
		GetWithdrawalDetailsReqModel model = new GetWithdrawalDetailsReqModel();
		try {
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IldDTTU0OSIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFDMkI3NjNGMDBCNUM2NjE2ODA3NDkwRkJCNTYzIiwidXNlckNvZGUiOiJPRkhZVyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTk3Mzc5OSwiaWF0IjoxNzAxOTUxNTI2fQ.k919xl9yNJ_1L5IzllvEK6qaCKSHWOnkJ7B4d2yAHqc";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			String url = props.getWithdrawalDetails();
			model.setAPIKey("JainamPG_API");
			model.setClientCode(info.getUserId());
			response = odinPaymentRestService.getwithdrawalDetailsService(model, info.getUserId(), url);
			if (response != null) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to create Withdrawal request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createWithdrawalRequest(ClinetInfoModel info, WithdrawalReqModel model) {
		Object response = new Object();
		WithdrawalRequestModel reqModel = new WithdrawalRequestModel();
		try {
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IldDTTU0OSIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFGMzE0NjIzRDAyNUY5ODE3RkFBQjcyOUNCQUZGIiwidXNlckNvZGUiOiJPRkhZVyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTk3Mzc5OSwiaWF0IjoxNzAxOTM1NjQwfQ.VYdjg8B9DM6oI7IZqbS3t9Z_F7bWlyr4Wyj2m13kPcc";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			String url = props.getWithdrawalRequest();
			reqModel.setAPIKey("JainamPG_API");
			reqModel.setClientCode(info.getUserId());
			reqModel.setAmount(model.getAmount());
			reqModel.setBankAccountNo(model.getBankActNo());
			response = odinPaymentRestService.getCommonPostService(reqModel, info.getUserId(), url);
			if (response != null) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get transaction list
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTransactionList(ClinetInfoModel info) {
		Object response = new Object();
		WithdrawalRequestModel model = new WithdrawalRequestModel();
		try {
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IldDTTU0OSIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFBNEI1M0Q0QjdBOEJFMjg0QjQ5NDA4OERGNTM2IiwidXNlckNvZGUiOiJPRkhZVyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTU0MTc5OSwiaWF0IjoxNzAxNTA5NDI4fQ.IqU3Va-n-ccEBVI1s7Eb4H0EHIhJFQongnFDg8eILPE";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			String url = props.getTransactionList();
			model.setAPIKey("JainamPG_API");
			model.setClientCode(info.getUserId());
			response = odinPaymentRestService.getTransactionDetails(model, info.getUserId(), url);
			if (response != null) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to delete Withdrawal Request
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteWithdrawalRequest(ClinetInfoModel info,
			DeleteWithdrawalRequestModel model) {
		Object response = new Object();
		DeleteWithdrawalReqModel reqModel = new DeleteWithdrawalReqModel();
		try {
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IldDTTU0OSIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFDMkI3NjNGMDBCNUM2NjE2ODA3NDkwRkJCNTYzIiwidXNlckNvZGUiOiJPRkhZVyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTk3Mzc5OSwiaWF0IjoxNzAxOTUxNTI2fQ.k919xl9yNJ_1L5IzllvEK6qaCKSHWOnkJ7B4d2yAHqc";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			String url = props.getWithdrawalRequestDelete();
			reqModel.setAPIKey("JainamPG_API");
			reqModel.setClientCode(info.getUserId());
			reqModel.setWithdrawalRequestId(model.getWithdrawalRequestId());
			response = odinPaymentRestService.getDeleteService(reqModel, info.getUserId(), url);
			if (response != null) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

package in.codifi.funds.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONValue;

import in.codifi.funds.config.RestServiceProperties;
import in.codifi.funds.entity.logs.RestAccessLogModel;
import in.codifi.funds.model.request.DeleteWithdrawalReqModel;
import in.codifi.funds.model.request.GetWithdrawalDetailsReqModel;
import in.codifi.funds.model.request.WithdrawalRequestModel;
import in.codifi.funds.model.response.GetTransactionDetailsResposeModel;
import in.codifi.funds.model.response.TransactionDataDetailsResp;
import in.codifi.funds.model.response.TransactionDetailsResponse;
import in.codifi.funds.repository.AccessLogManager;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.CodifiUtil;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.BankDetailsResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OdinPaymentRestService {

	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to get Withdrawal Details
	 * 
	 * @author LOKESH
	 * @param url2
	 * @return
	 */
	public Object getCommonPostService(WithdrawalRequestModel model, String userId, String url2) {
		Object object = null;
		List<BankDetailsResp> res = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		HttpURLConnection conn = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestString = mapper.writeValueAsString(model);

			accessLogModel.setMethod("getWithdrawalDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getWithdrawalDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setReqBody(requestString);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();

			URL url = new URL(url2);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Authorization", userSession);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = requestString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Limits response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					object = JSONValue.parse(output);
//					res = mapper.readValue(output,
//							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
//					if (res.get(0).getCode() == 200) {
//						GetPaymentResposeModel bankDetailsresp = bindPaymentBankDetails(res);
					return object;
//					}

				} else {
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}

	public Object getTransactionDetails(WithdrawalRequestModel model, String userId, String url2) {
		Object object = null;
		TransactionDetailsResponse res = new TransactionDetailsResponse();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		HttpURLConnection conn = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestString = mapper.writeValueAsString(model);

			accessLogModel.setMethod("getTransactionDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getWithdrawalDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setReqBody(requestString);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();

			URL url = new URL(url2);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Authorization", userSession);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = requestString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Limits response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
//					object = JSONValue.parse(output);
//					res = mapper.readValue(output, mapper.getTypeFactory().constructCollectionType(List.class,
//							TransactionDetailsResponse.class));
//					if (res.get) {
//						List<GetTransactionDetailsResposeModel> bankDetailsresp = bindPaymentBankDetails(res);
//						return object;
//					}

					res = mapper.readValue(output, TransactionDetailsResponse.class);
					if (res.isSuccess()) {
						List<GetTransactionDetailsResposeModel> bankDetailsresp = bindPaymentBankDetails(res.getData(),
								userId);
						return bankDetailsresp;
					}

				} else {
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}

	private List<GetTransactionDetailsResposeModel> bindPaymentBankDetails(List<TransactionDataDetailsResp> pReq,
			String userId) {

		List<GetTransactionDetailsResposeModel> response = new ArrayList<>();

		for (TransactionDataDetailsResp rSet : pReq) {
			GetTransactionDetailsResposeModel result = new GetTransactionDetailsResposeModel();
			result.setUserId(userId);
			result.setId(rSet.getId());
			result.setAmount(rSet.getAmount());
			result.setTableName(rSet.getTableName());
			result.setCreatedOn(rSet.getDate());
			result.setMode(rSet.getMode());
			result.setStatus(rSet.getStatus());
			result.setFullDate(rSet.getFullDate());
			result.setAmountInCurrency(rSet.getAmountInCurrency());

			response.add(result);

		}

		return response;
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gowthaman M
	 * @param accessLogModel
	 */
	public void insertRestAccessLogs(RestAccessLogModel accessLogModel) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					accessLogManager.insertRestAccessLog(accessLogModel);
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	public Object getwithdrawalDetailsService(GetWithdrawalDetailsReqModel model, String userId, String url2) {
		Object object = null;
		List<BankDetailsResp> res = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		HttpURLConnection conn = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestString = mapper.writeValueAsString(model);

			accessLogModel.setMethod("getWithdrawalDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getWithdrawalDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setReqBody(requestString);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();

			URL url = new URL(url2);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Authorization", userSession);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = requestString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Limits response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					object = JSONValue.parse(output);
//					res = mapper.readValue(output,
//							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
//					if (res.get(0).getCode() == 200) {
//						GetPaymentResposeModel bankDetailsresp = bindPaymentBankDetails(res);
					return object;
//					}

				} else {
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}

	public Object getDeleteService(DeleteWithdrawalReqModel reqModel, String userId, String url2) {
		Object object = null;
		List<BankDetailsResp> res = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		HttpURLConnection conn = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestString = mapper.writeValueAsString(reqModel);

			accessLogModel.setMethod("getWithdrawalDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getWithdrawalDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setReqBody(requestString);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			CodifiUtil.trustedManagement();

			URL url = new URL(url2);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Authorization", userSession);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = requestString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Limits response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					object = JSONValue.parse(output);
//					res = mapper.readValue(output,
//							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
//					if (res.get(0).getCode() == 200) {
//						GetPaymentResposeModel bankDetailsresp = bindPaymentBankDetails(res);
					return object;
//					}

				} else {
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}
}

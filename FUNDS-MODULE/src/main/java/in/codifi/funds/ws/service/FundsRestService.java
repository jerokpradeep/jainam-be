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

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.funds.config.RestServiceProperties;
import in.codifi.funds.entity.logs.RestAccessLogModel;
import in.codifi.funds.model.response.BankDetails;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.model.response.GetPaymentResposeModel;
import in.codifi.funds.model.response.LimitsResponseModel;
import in.codifi.funds.model.response.PayoutDetailsResp;
import in.codifi.funds.repository.AccessLogManager;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.CodifiUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.BankDetailsResp;
import in.codifi.funds.ws.model.BankDetailsRestResp;
import in.codifi.funds.ws.model.LimitRequest;
import in.codifi.funds.ws.model.ResponseLimits;
import in.codifi.funds.ws.model.Result;
import in.codifi.funds.ws.remodeling.LimitsRemodeling;
import io.quarkus.logging.Log;

@ApplicationScoped
public class FundsRestService {

	@Inject
	RestServiceProperties props;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	LimitsRemodeling limitsRemodeling;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to connect the API
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getLimits(LimitRequest request, String userSession) {
		Log.info("Odin Limits Request" + request);
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ResponseLimits res = null;
		try {
			accessLogModel.setMethod("getLimits");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getLimitsUrl());
			accessLogModel.setReqBody(mapper.writeValueAsString(request));
			accessLogModel.setUserId(request.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getLimitsUrl());
			String req = mapper.writeValueAsString(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = req.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode-- " + responseCode);
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 401) {
				Log.error("Unauthorized error in odin Limits api");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Limits response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					res = mapper.readValue(output, ResponseLimits.class);
					if (res.getStatus()) {
						List<List<Result>> limRes = res.getResult();
						LimitsResponseModel limitsResponse = limitsRemodeling.bindLimits(limRes);

						return prepareResponse.prepareSuccessResponseObject(limitsResponse);
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		accessLogModel.setResBody("Failed");
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
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

	/**
	 * Method to get user bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BankDetails getBankDetailsFromOdin(String userId) {
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		List<BankDetailsResp> res = null;
		try {
			accessLogModel.setMethod("getBankDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getBankDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL("https://trade.cholawealthdirect.com/CholaWMSAPI/PortfolioService.svc/ClientDetails");
			String req = "{\"UID\":\"" + userId + "\"}";
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = req.getBytes(AppConstants.UTF_8);
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
					res = mapper.readValue(output,
							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
					if (res.get(0).getCode() == 200) {
						BankDetails bankDetailsresp = bindbankDetails(res);
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

	/**
	 * Method to bind bank details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BankDetails bindbankDetails(List<BankDetailsResp> res) {
		BankDetails response = new BankDetails();
		response.setBankActNo(res.get(0).getDataBank().get(0).getBankAccountNumber().trim());
		response.setBankCode(res.get(0).getDataBank().get(0).getBankCode());
		response.setBankName(res.get(0).getDataBank().get(0).getBankName());
		response.setClientName(res.get(0).getData().get(0).getName().trim());
		response.setIfscCode(res.get(0).getDataBank().get(0).getIfscCode().trim());
		return response;
	}

	public GetPaymentResposeModel getPaymentBankDetails(String userId) {
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		List<BankDetailsResp> res = null;
		try {
			accessLogModel.setMethod("getBankDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getBankDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL("https://trade.cholawealthdirect.com/CholaWMSAPI/PortfolioService.svc/ClientDetails");
			String req = "{\"UID\":\"" + userId + "\"}";
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = req.getBytes(AppConstants.UTF_8);
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
					res = mapper.readValue(output,
							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
					if (res.get(0).getCode() == 200) {
						GetPaymentResposeModel bankDetailsresp = bindPaymentBankDetails(res);
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

	public GetPaymentResposeModel bindPaymentBankDetails(List<BankDetailsResp> res) {
		GetPaymentResposeModel response = new GetPaymentResposeModel();
		
		BankDetails bankDetailsFromRazorpay = new BankDetails();
		bankDetailsFromRazorpay = getBankDetailsFromRazorpay(res.get(0).getDataBank().get(0).getIfscCode().trim());

		List<BankDetails> bankDetailresp = new ArrayList<>();
		BankDetails resp = new BankDetails();
		resp.setBankActNo(res.get(0).getDataBank().get(0).getBankAccountNumber().trim());
		resp.setBankCode(bankDetailsFromRazorpay.getBankCode());
		resp.setBankName(bankDetailsFromRazorpay.getBankName());
		resp.setClientName(res.get(0).getData().get(0).getName().trim());
		resp.setIfscCode(bankDetailsFromRazorpay.getIfscCode());
		bankDetailresp.add(resp);
		
		response.setBankDetails(bankDetailresp);
		response.setEmail(res.get(0).getData().get(0).getCcEmailId());
		response.setPayoutReasons(null);
		response.setPhone(res.get(0).getData().get(0).getMobileNo());
		response.setUpiId(null);
		response.setUserName(res.get(0).getData().get(0).getName());
		
		return response;
	}

	/**
	 * Method to get bank details from Razorpay
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BankDetails getBankDetailsFromRazorpay(String ifsc) {
		BankDetailsRestResp response = new BankDetailsRestResp();
		BankDetails bankDetails = new BankDetails();
		try {
			URL url = new URL(props.getRazorpayIfscUrl() + ifsc);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output = bufferedReader.readLine();
			if (StringUtil.isNotNullOrEmpty(output)) {
				if (output.startsWith("Not Found")) {
					Log.error("Failed to get Bank code - Invalid IFSC code - " + ifsc);
				} else {
					ObjectMapper mapper = new ObjectMapper();
					response = mapper.readValue(output, BankDetailsRestResp.class);
					if (response != null && StringUtil.isNotNullOrEmpty(response.getBankcode())) {
						bankDetails = bindBankDeatilsResponse(response);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return bankDetails;
	}

	/**
	 * Method to bind Bank Deatils Response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BankDetails bindBankDeatilsResponse(BankDetailsRestResp result) {
		BankDetails response = new BankDetails();
		try {
			response.setBankActNo(null);
			response.setBankCode(result.getBankcode());
			response.setBankName(result.getBank());
			response.setClientName(null);
			response.setIfscCode(result.getIfsc());
			
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to get payout details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public PayoutDetailsResp getPayoutDetailsResp(String userId) {
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		List<BankDetailsResp> res = null;
		try {
			accessLogModel.setMethod("getBankDetails");
			accessLogModel.setModule(AppConstants.MODULE_FUNDS);
			accessLogModel.setUrl(props.getBankDetails());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL("https://trade.cholawealthdirect.com/CholaWMSAPI/PortfolioService.svc/ClientDetails");
			String req = "{\"UID\":\"" + userId + "\"}";
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = req.getBytes(AppConstants.UTF_8);
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
					res = mapper.readValue(output,
							mapper.getTypeFactory().constructCollectionType(List.class, BankDetailsResp.class));
					if (res.get(0).getCode() == 200) {
						PayoutDetailsResp response = bindPayoutDetailsResp(res);
						return response;
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

	/**
	 * Method to bind payout details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private PayoutDetailsResp bindPayoutDetailsResp(List<BankDetailsResp> res) {
		PayoutDetailsResp response = new PayoutDetailsResp();
		response.setBackofficeCode(res.get(0).getData().get(0).getBoCode());
		return response;
	}

}

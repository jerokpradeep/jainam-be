package in.codifi.holdings.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.config.HazelcastConfig;
import in.codifi.holdings.config.RestServiceProperties;
import in.codifi.holdings.entity.logs.RestAccessLogModel;
import in.codifi.holdings.model.request.EdisSummaryRequest;
import in.codifi.holdings.model.response.GenericResponse;
import in.codifi.holdings.model.response.HoldingsRespModel;
import in.codifi.holdings.repository.AccessLogManager;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.CodifiUtil;
import in.codifi.holdings.utility.PrepareResponse;
import in.codifi.holdings.utility.StringUtil;
import in.codifi.holdings.ws.model.EdisSummaryResponse;
import in.codifi.holdings.ws.model.Fail;
import in.codifi.holdings.ws.model.HoldingsRestRespModel;
import in.codifi.holdings.ws.model.SummaryResponse;
import in.codifi.holdings.ws.remodeling.HoldingsRemodeling;
import io.quarkus.logging.Log;

@ApplicationScoped
public class HoldingsRestService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	HoldingsRemodeling holdingsRemodeling;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to connect rest service and get Holdings
	 * 
	 * @author Gowthaman M
	 * @param product
	 * @param accessLogModel
	 */
	public RestResponse<GenericResponse> getHoldings(String userSession, String userId) {
		HoldingsRestRespModel holdingsRestRespModel = new HoldingsRestRespModel();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Odin holdings request" + userSession);
		try {

			accessLogModel.setMethod("getHoldings");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getHoldingsUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin holdings api");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin Holdings response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.contains("[{")) {
						holdingsRestRespModel = mapper.readValue(output, HoldingsRestRespModel.class);
						HoldingsRespModel extract = holdingsRemodeling.bindHoldingData(holdingsRestRespModel, userId);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (output.contains("[]")) {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.REST_NO_DATA);
					} else {
						Fail fail = mapper.readValue(output, Fail.class);
						if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
							return prepareResponse.prepareFailedResponse(fail.getEmsg());
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Fail fail = mapper.readValue(output, Fail.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						System.out.println("Error Connection in holdings api. Rsponse code -" + fail.getEmsg());
					return prepareResponse.prepareFailedResponse(fail.getEmsg());
				}
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
	 * Method to get EdisSummary
	 * 
	 * @author Gowthaman
	 * @param req
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> getEdisSummary(EdisSummaryRequest req, ClinetInfoModel info,
			String userSession) {
		EdisSummaryResponse summaryResponse = new EdisSummaryResponse();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Get EdisSummary" + userSession);
		ClientDetailsModel cacheModel = HazelcastConfig.getInstance().getClientDetails().get(info.getUserId());

		try {
			accessLogModel.setMethod("getEdisSummary");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String depository = cacheModel.getDpAccountNumber().get(0).getRepository();
//			String depository = "CDSL";
			String request = props.getEdisSummaryUrl() + info.getUserId() + "/" + req.getSellQty() + "/"
					+ req.getSegId() + "/" + req.getToken() + "/" + depository + "/" + req.getSummaryMktSegId() + "/"
					+ req.getProductType();
			System.out.println("request -- " + request);
			accessLogModel.setReqBody(request);
			CodifiUtil.trustedManagement();
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Get EdisSummary");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin Get EdisSummary response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains("Summary fetched successfully")) {
					summaryResponse = mapper.readValue(output, EdisSummaryResponse.class);
					System.out.println("response -- " + summaryResponse);
					return prepareResponse.prepareSuccessResponseObject(summaryResponse.getData());
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NOT_FOUND);
				}

			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Fail fail = mapper.readValue(output, Fail.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						System.out.println("Error Connection in Get EdisSummary api. Rsponse code -" + fail.getEmsg());
					return prepareResponse.prepareFailedResponse(fail.getEmsg());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Get EdisSummary -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get EdisSummary
	 * 
	 * @author Gowthaman
	 * @param req
	 * @param info
	 * @return
	 */
	public RestResponse<GenericResponse> getEdisSummarys(ClinetInfoModel info, String userSession) {
		SummaryResponse summaryResponse = new SummaryResponse();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		Log.info("Get EdisSummary" + userSession);

		try {
			accessLogModel.setMethod("getEdisSummarys");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String request = props.getEdisSummarysUrl();
			System.out.println("request -- " + request);
			accessLogModel.setReqBody(request);
			CodifiUtil.trustedManagement();
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("getEdisSummarys responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Get EdisSummary");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin Get EdisSummary response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains("Summary fetched successfully")) {
					summaryResponse = mapper.readValue(output, SummaryResponse.class);
					return prepareResponse.prepareSuccessResponseObject(summaryResponse.getData());
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NOT_FOUND);
				}

			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Fail fail = mapper.readValue(output, Fail.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						System.out.println("Error Connection in Get EdisSummary api. Rsponse code -" + fail.getEmsg());
					return prepareResponse.prepareFailedResponse(fail.getEmsg());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Get EdisSummary -- " + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

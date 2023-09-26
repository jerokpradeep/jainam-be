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

import in.codifi.holdings.config.RestServiceProperties;
import in.codifi.holdings.entity.logs.RestAccessLogModel;
import in.codifi.holdings.model.request.EdisReqModel;
import in.codifi.holdings.model.response.GenericResponse;
import in.codifi.holdings.repository.AccessLogManager;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.CodifiUtil;
import in.codifi.holdings.utility.PrepareResponse;
import in.codifi.holdings.utility.StringUtil;
import in.codifi.holdings.ws.model.DpDetailsData;
import in.codifi.holdings.ws.model.EdisDpDetailsRespModel;
import in.codifi.holdings.ws.model.EdisSummaryResModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class EdisRestService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;

	public EdisSummaryResModel getEdisSummary(EdisReqModel edisModel, DpDetailsData dpDetailsData, String userSession,
			String userId) {
		EdisSummaryResModel resModel = new EdisSummaryResModel();
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			Log.info("Odin Edis-summary request" + mapper.writeValueAsString(dpDetailsData));
			CodifiUtil.trustedManagement();

			accessLogModel.setMethod("getEdisSummary");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			URL url = new URL(props.getEdisSummaryUrl() + dpDetailsData.sClientCode + AppConstants.OPERATOR_SLASH
					+ edisModel.getSellQty() + AppConstants.OPERATOR_SLASH + edisModel.getSegId()
					+ AppConstants.OPERATOR_SLASH + edisModel.getToken() + AppConstants.OPERATOR_SLASH
					+ dpDetailsData.getSDepository() + AppConstants.OPERATOR_SLASH + edisModel.getSummaryMktSegId()
					+ AppConstants.OPERATOR_SLASH + edisModel.getProductType());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, AppConstants.X_API_KEY);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode -- " + responseCode);

			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Edis holdings api");
				resModel.setCode("401");

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Edis-summary response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					resModel = mapper.readValue(output, EdisSummaryResModel.class);
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Edis-summary response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					resModel = mapper.readValue(output, EdisSummaryResModel.class);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return resModel;
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author SOWMIYA
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
	 * Method to get ODIN Edis dp dpdetails
	 * 
	 * @param userId
	 * 
	 * @return
	 */
	public EdisDpDetailsRespModel getEdisDpDetails(String userSession, String userId) {
		EdisDpDetailsRespModel respModel = new EdisDpDetailsRespModel();
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			CodifiUtil.trustedManagement();
			accessLogModel.setMethod("getEdisDpDetails");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			URL url = new URL(props.getEdisDpDetailUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
//			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, AppConstants.X_API_KEY);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode -- " + responseCode);

			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin holdings api");
				respModel.setCode("401");
				return respModel;

			} else if (responseCode == 404) {
				Log.error("Unauthorized error in Odin holdings api");
				respModel.setCode("401");// since 404 response is comming from ODIN for unauthorized
				return respModel;
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				accessLogModel.setResBody(output);
				output = bufferedReader.readLine();
				Log.info("Odin Edis dp  response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {

					respModel = mapper.readValue(output, EdisDpDetailsRespModel.class);

				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Edis dp  response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					respModel = mapper.readValue(output, EdisDpDetailsRespModel.class);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return respModel;
	}

	/**
	 * Method to get edis summary details from odin
	 * 
	 * @param edisModel
	 * @param dpDetailsData
	 * @param userSession
	 * @return
	 */
	public RestResponse<GenericResponse> getEdisSummary(EdisReqModel edisModel, String userSession, String clientCode,
			String depository) {
		EdisSummaryResModel resModel = new EdisSummaryResModel();
		ObjectMapper mapper = new ObjectMapper();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		try {
			Log.info("Odin Edis-summary request" + mapper.writeValueAsString(edisModel));
			CodifiUtil.trustedManagement();
			accessLogModel.setMethod("getEdisSummary");
			accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
			accessLogModel.setUrl(props.getHoldingsUrl());
			URL url = new URL(props.getEdisSummaryUrl() + clientCode + AppConstants.OPERATOR_SLASH
					+ edisModel.getSellQty() + AppConstants.OPERATOR_SLASH + edisModel.getSegId()
					+ AppConstants.OPERATOR_SLASH + edisModel.getToken() + AppConstants.OPERATOR_SLASH + depository
					+ AppConstants.OPERATOR_SLASH + edisModel.getSummaryMktSegId() + AppConstants.OPERATOR_SLASH
					+ edisModel.getProductType());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
//			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, AppConstants.X_API_KEY);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode -- " + responseCode);

			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Edis holdings api");
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				Log.info("Odin Edis-summary response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					resModel = mapper.readValue(output, EdisSummaryResModel.class);
				}
				return prepareResponse.prepareSuccessResponseObject(resModel);
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("Odin Edis-summary response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {
					resModel = mapper.readValue(output, EdisSummaryResModel.class);
					return prepareResponse.prepareFailedResponse(resModel.getMessage());
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

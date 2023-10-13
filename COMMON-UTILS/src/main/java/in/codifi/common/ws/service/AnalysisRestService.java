package in.codifi.common.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.config.RestServiceProperties;
import in.codifi.common.entity.logs.RestAccessLogModel;
import in.codifi.common.entity.primary.AnnoucementsDataEntity;
import in.codifi.common.entity.primary.NetValueEntity;
import in.codifi.common.model.request.BrokerMsgRequest;
import in.codifi.common.model.request.CFRatioReq;
import in.codifi.common.model.request.ExchangeMsgRequest;
import in.codifi.common.model.request.MktNewsReq;
import in.codifi.common.model.request.PutCallRatioReq;
import in.codifi.common.model.request.QuarterlyTrendReq;
import in.codifi.common.model.request.ScripwiseNewsReqModel;
import in.codifi.common.model.request.SectorListReq;
import in.codifi.common.model.request.SupportAndResistanceReq;
import in.codifi.common.model.response.AnnoucementsDataResp;
import in.codifi.common.model.response.AnnoucementsScripDataResp;
import in.codifi.common.model.response.BrokerMsgRespModel;
import in.codifi.common.model.response.CFRatioResponse;
import in.codifi.common.model.response.CompanyRespModel;
import in.codifi.common.model.response.ExchangeMsgRespModel;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.HotPursuitDataResp;
import in.codifi.common.model.response.IndexDetailsRespModify;
import in.codifi.common.model.response.IndianMktNewsResp;
import in.codifi.common.model.response.MovingAverageResp;
import in.codifi.common.model.response.ProfitLossRespModel;
import in.codifi.common.model.response.PutCallRatioResponse;
import in.codifi.common.model.response.QuarterlyTrendResp;
import in.codifi.common.model.response.ResponsecodeStatusModel;
import in.codifi.common.model.response.SectorListReponse;
import in.codifi.common.model.response.SectorWiseNewsDataResp;
import in.codifi.common.model.response.ShareHoldingsResp;
import in.codifi.common.model.response.ShareHoldingsScripDataResp;
import in.codifi.common.repository.AccessLogManager;
import in.codifi.common.repository.AnnoucementsDataRepository;
import in.codifi.common.repository.ContractEntityManger;
import in.codifi.common.repository.NetValueRepository;
import in.codifi.common.service.PrepareResponse;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.CodifiUtil;
import in.codifi.common.utility.StringUtil;
import in.codifi.common.ws.model.AnalysisRestResponseModel;
import in.codifi.common.ws.model.AnnoucementsDataRespObj;
import in.codifi.common.ws.model.AnnoucementsDataResultSet;
import in.codifi.common.ws.model.BalanceSheetRestRespModel;
import in.codifi.common.ws.model.BlnSheetResultset;
import in.codifi.common.ws.model.BrokerMsgResultSet;
import in.codifi.common.ws.model.CFDataResultset;
import in.codifi.common.ws.model.CompanyFinReq;
import in.codifi.common.ws.model.CompanyFinancialRatioRestResp;
import in.codifi.common.ws.model.DIIRestResp;
import in.codifi.common.ws.model.DividentReqModel;
import in.codifi.common.ws.model.DividentRespModel;
import in.codifi.common.ws.model.ExchangeMsgResultSet;
import in.codifi.common.ws.model.FIIDIIResp;
import in.codifi.common.ws.model.FIIDIIRestResp;
import in.codifi.common.ws.model.HealthReqModel;
import in.codifi.common.ws.model.HealthRespModel;
import in.codifi.common.ws.model.HotPursuitDataRespObj;
import in.codifi.common.ws.model.HotPursuitResultSet;
import in.codifi.common.ws.model.IndMktNewsResultset;
import in.codifi.common.ws.model.IndexDetailsRestResp;
import in.codifi.common.ws.model.IndexDetailsResult;
import in.codifi.common.ws.model.IndianMktNewsDataResponseObj;
import in.codifi.common.ws.model.MovingAverageRespObj;
import in.codifi.common.ws.model.MovingAverageResultSet;
import in.codifi.common.ws.model.ProfitLostRestRespModel;
import in.codifi.common.ws.model.PutCallRatioRespObj;
import in.codifi.common.ws.model.PutCallRatioResultSet;
import in.codifi.common.ws.model.QuarterlyTrendRestResp;
import in.codifi.common.ws.model.QuarterlyTrendResultset;
import in.codifi.common.ws.model.ResponseObjBrokerMsg;
import in.codifi.common.ws.model.ResponseObjExchangeMsg;
import in.codifi.common.ws.model.ResultsetRestModel;
import in.codifi.common.ws.model.ScripNewsResultset;
import in.codifi.common.ws.model.ScripwiseNewsRespModel;
import in.codifi.common.ws.model.SectorListRespObj;
import in.codifi.common.ws.model.SectorListResultSet;
import in.codifi.common.ws.model.SectorWiseNewsDataRespObj;
import in.codifi.common.ws.model.SectorWiseResultset;
import in.codifi.common.ws.model.ShareHolResultset;
import in.codifi.common.ws.model.ShareHoldingRestResponseObject;
import in.codifi.common.ws.model.SupportAndResistanceRestResponse;
import in.codifi.common.ws.model.WIResponse;
import in.codifi.common.ws.model.WIRestResultset;
import in.codifi.common.ws.model.WIResultResponse;
import in.codifi.common.ws.model.WorldIndicesDataRestResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AnalysisRestService {

	@Inject
	RestServiceProperties props;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AccessLogManager accessLogManager;
	@Inject
	ContractEntityManger entityManager;
	@Inject
	NetValueRepository netValueRepo;
	@Inject
	AnnoucementsDataRepository annoucementsDataRepository;

	/**
	 * Method to get FII Activity Data from odin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getActivityData() {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		FIIDIIRestResp fiidiiResp = new FIIDIIRestResp();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getActivityDataFii());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode--" + responseCode);

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getFII_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getFII");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
					fiidiiResp = mapper.readValue(output, FIIDIIRestResp.class);
					if (fiidiiResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						FIIDIIResp diiResp = getActivityDataDii();
						FIIDIIResp fiiResp = bindFII(fiidiiResp, diiResp);
						HazelcastConfig.getInstance().getActivityData().clear();
						HazelcastConfig.getInstance().getActivityData().put("activityData", fiiResp);
						saveFIIValuesToDB(fiiResp);
						return prepareResponse.prepareSuccessResponseObject(fiiResp);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to prepare and save fii values into database
	 * 
	 * @author SOWMIYA
	 * @param fiiResp
	 */
	private void saveFIIValuesToDB(FIIDIIResp fiiResp) {
		try {
			NetValueEntity entity = new NetValueEntity();
			entity.setFiiYesterday(fiiResp.getFiiYesterday());
			entity.setFiiMonth(fiiResp.getFiiMonth());
			entity.setDiiYesterday(fiiResp.getDiiYesterday());
			entity.setDiiMonth(fiiResp.getDiiMonth());
			netValueRepo.save(entity);
		} catch (Exception e) {
			Log.error(e);
		}

	}

	/**
	 * Method to get FII Activity Data from odin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public FIIDIIResp getActivityDataDii() {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		DIIRestResp restResp = new DIIRestResp();
		FIIDIIResp fiiResp = new FIIDIIResp();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getActivityDataDii());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode--" + responseCode);

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getDII_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getDII");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
					restResp = mapper.readValue(output, DIIRestResp.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						fiiResp = bindDII(restResp);
						return fiiResp;
					}
				} else {
					return fiiResp;
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}

		return fiiResp;
	}

	/**
	 * Method to bind FII
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public FIIDIIResp bindFII(FIIDIIRestResp fiidiiResp, FIIDIIResp diiResp) {
		FIIDIIResp response = new FIIDIIResp();
		try {
			float month = 0;
			float week = 0;
			response.setFiiYesterday(fiidiiResp.getResponseObject().getResultset().get(0).getEquityNetInvestment());
			for (int i = 0; i <= 6; i++) {
				float daily = Float
						.parseFloat(fiidiiResp.getResponseObject().getResultset().get(i).getEquityNetInvestment());
				week = week + daily;
			}
			for (int i = 0; i <= 31; i++) {
				int size = fiidiiResp.getResponseObject().getResultset().size();
				if(i <= size-1) {
					float daily = Float
							.parseFloat(fiidiiResp.getResponseObject().getResultset().get(i).getEquityNetInvestment());
					month = month + daily;
				}
			}
			response.setFiiWeek(Float.toString(week));
			response.setFiiMonth(Float.toString(month));
			response.setDiiYesterday(diiResp.getDiiYesterday());
			response.setDiiWeek(diiResp.getDiiWeek());
			response.setDiiMonth(diiResp.getDiiMonth());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to bind DII
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public FIIDIIResp bindDII(DIIRestResp diiRestResp) {
		FIIDIIResp response = new FIIDIIResp();
		try {
			float month = 0;
			float week = 0;
			response.setDiiYesterday(diiRestResp.getResponseObject().getResultset().get(0).getNetValue());

			for (int i = 0; i <= 6; i++) {
				float daily = Float.parseFloat(diiRestResp.getResponseObject().getResultset().get(i).getNetValue());
				week = week + daily;
			}
			response.setDiiWeek(Float.toString(week));
			for (int i = 0; i <= 31; i++) {
				int size = diiRestResp.getResponseObject().getResultset().size();
				if(i <= size-1) {
					float daily = Float.parseFloat(diiRestResp.getResponseObject().getResultset().get(i).getNetValue());
					month = month + daily;
				}
			}
			response.setDiiMonth(Float.toString(month));
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gowthaman M
	 *
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
	 * Method to get World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getWorldIndicesData() {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		WorldIndicesDataRestResp worldIndicesData = new WorldIndicesDataRestResp();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getWorldIndicesData());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();

			System.out.println("WorldIndicesData responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getWorldIndicesData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getWorldIndicesData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					worldIndicesData = mapper.readValue(output, WorldIndicesDataRestResp.class);
					if (worldIndicesData.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {

						WIResponse extract = bindWorldIndicesData(worldIndicesData.getResponseObject().getResultset());

						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!worldIndicesData.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse.prepareFailedResponseForRestService(
								worldIndicesData.getResponseObject().getErrorMessage());
					}
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			} else {
				System.out.println("Error Connection in World Indices Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					worldIndicesData = mapper.readValue(output, WorldIndicesDataRestResp.class);
					if (StringUtil.isNotNullOrEmpty(worldIndicesData.getResponseObject().getErrorMessage()))
						return prepareResponse.prepareFailedResponseForRestService(
								worldIndicesData.getResponseObject().getErrorMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public WIResponse bindWorldIndicesData(List<WIRestResultset> resultset) {
		DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		DateFormat originalFormats = new SimpleDateFormat("dd-MMM-yyyy");
		DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		WIResponse response = new WIResponse();
		List<WIResultResponse> resultList = new ArrayList<>();
		try {
			for (WIRestResultset rSet : resultset) {
				WIResultResponse result = new WIResultResponse();
				result.setToken(null);

				if (rSet.getIndexName().equalsIgnoreCase("Hang Seng") || rSet.getIndexName().equalsIgnoreCase("NASDAQ")
						|| rSet.getIndexName().equalsIgnoreCase("S&P 100")
						|| rSet.getIndexName().equalsIgnoreCase("CAC 40")
						|| rSet.getIndexName().equalsIgnoreCase("DAX")
						|| rSet.getIndexName().equalsIgnoreCase("IPC")) {
					if (rSet.getDate().length() > 12) {
						date = originalFormat.parse(rSet.getDate());
					} else {
						date = originalFormats.parse(rSet.getDate());
					}
					String formattedDate = targetFormat.format(date);
					result.setDate(formattedDate);
					result.setHighlight("LTP");
					result.setSymbol(rSet.getIndexName());
					result.setLtp(rSet.getClosePrice());
					result.setClosePerChg(rSet.getPercentageChange());
					result.setDirection(null);
					result.setIsin(null);
					result.setPClose(rSet.getPreviousClosePrice());
					result.setExchange(null);
					result.setIndexID(rSet.getIndexID());
					result.setIndexName(rSet.getIndexName());
					result.setCountryName(rSet.getIndexCountry());
					resultList.add(result);

					HazelcastConfig.getInstance().getWorldIndices().put(rSet.getIndexName(), result);
				}
			}

			List<String> index = new ArrayList<>();
			for (WIResultResponse rSet : resultList) {
				index.add(rSet.getIndexName());
			}
			Iterator<String> itr = index.iterator();
			itr = sortedIterator(itr);
			List<WIResultResponse> finalResponse = new ArrayList<>();
			while (itr.hasNext()) {
				WIResultResponse res = HazelcastConfig.getInstance().getWorldIndices().get(itr.next());
				finalResponse.add(res);

			}
			response.setEquityresult(finalResponse);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to get World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getWorldIndicesDataAll() {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		WorldIndicesDataRestResp worldIndicesData = new WorldIndicesDataRestResp();
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getWorldIndicesData());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();

			System.out.println("WorldIndicesData responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getWorldIndicesDataAll_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getWorldIndicesData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUrl(url.toString());
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					worldIndicesData = mapper.readValue(output, WorldIndicesDataRestResp.class);
					if (worldIndicesData.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {

						WIResponse extract = bindWorldIndicesDataAll(
								worldIndicesData.getResponseObject().getResultset());

						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!worldIndicesData.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse.prepareFailedResponseForRestService(
								worldIndicesData.getResponseObject().getErrorMessage());
					}
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			} else {
				System.out.println("Error Connection in World Indices Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					worldIndicesData = mapper.readValue(output, WorldIndicesDataRestResp.class);
					if (StringUtil.isNotNullOrEmpty(worldIndicesData.getResponseObject().getErrorMessage()))
						return prepareResponse.prepareFailedResponseForRestService(
								worldIndicesData.getResponseObject().getErrorMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public WIResponse bindWorldIndicesDataAll(List<WIRestResultset> resultset) {
		DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		DateFormat originalFormats = new SimpleDateFormat("dd-MMM-yyyy");
		DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		WIResponse response = new WIResponse();
		List<WIResultResponse> resultList = new ArrayList<>();
		try {
			for (WIRestResultset rSet : resultset) {
				WIResultResponse result = new WIResultResponse();
				result.setToken(null);

				if (rSet.getDate().length() > 12) {
					date = originalFormat.parse(rSet.getDate());
				} else {
					date = originalFormats.parse(rSet.getDate());
				}
				String formattedDate = targetFormat.format(date);
				result.setDate(formattedDate);
				result.setHighlight("LTP");
				result.setSymbol(rSet.getIndexName());
				result.setLtp(rSet.getClosePrice());
				result.setClosePerChg(rSet.getPercentageChange());
				result.setDirection(null);
				result.setIsin(null);
				result.setPClose(rSet.getPreviousClosePrice());
				result.setExchange(null);
				result.setIndexID(rSet.getIndexID());
				result.setIndexName(rSet.getIndexName());
				result.setCountryName(rSet.getIndexCountry());
				resultList.add(result);

				HazelcastConfig.getInstance().getWorldIndices().put(rSet.getIndexName(), result);
			}

			List<String> index = new ArrayList<>();
			for (WIResultResponse rSet : resultList) {
				index.add(rSet.getIndexName());
			}
			Iterator<String> itr = index.iterator();
			itr = sortedIterator(itr);
			List<WIResultResponse> finalResponse = new ArrayList<>();
			while (itr.hasNext()) {
				WIResultResponse res = HazelcastConfig.getInstance().getWorldIndices().get(itr.next());
				finalResponse.add(res);

			}
			response.setEquityresult(finalResponse);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to get Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyFinancialRatioData(CFRatioReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		CompanyFinancialRatioRestResp restResp = new CompanyFinancialRatioRestResp();
		try {
			CodifiUtil.trustedManagement();

			String request = "/cds/1404/v1/" + req.getMktSegmentId() + "/" + req.getToken() + "/" + req.getType()
					+ "/GetCompanyFinancialRatioData";
			URL url = new URL(props.getBaseUrl() + request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();

			System.out.println("GetCompanyFinancialRatioData responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getCompanyFinancialRatioData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getCompanyFinancialRatioData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setReqBody(url.toString());
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.NO_DATA_FOUND)) {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.NO_RECORDS_FOUND);
				}
				if (StringUtil.isNotNullOrEmpty(output)) {
					restResp = mapper.readValue(output, CompanyFinancialRatioRestResp.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<CFRatioResponse> extract = bindCFRatioData(restResp.getResponseObject().getResultset());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!restResp.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
					}
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			} else {
				System.out.println("Error Connection in Company Financial Ratio Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					restResp = mapper.readValue(output, CompanyFinancialRatioRestResp.class);
					if (StringUtil.isNotNullOrEmpty(restResp.getResponseObject().getErrorMessage()))
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<CFRatioResponse> bindCFRatioData(List<CFDataResultset> resultset) {
		List<CFRatioResponse> response = new ArrayList<>();
		for (CFDataResultset resultData : resultset) {
			CFRatioResponse ratioResponse = new CFRatioResponse();
			ratioResponse.setCurrentRatio(resultData.getCurrentRatio());
			ratioResponse.setDebtEquityRatio(resultData.getDebtEquityRatio());
			ratioResponse.setDividendYield(resultData.getDividendYield());
			ratioResponse.setEbitdaGrowth(resultData.getEbitdaGrowth());
			ratioResponse.setEps(resultData.getEps());
			ratioResponse.setNetSalesGrowth(resultData.getNetSalesGrowth());
			ratioResponse.setPatGrowth(resultData.getPatGrowth());
			ratioResponse.setPe(resultData.getPe());
			ratioResponse.setPeg(resultData.getPeg());
			ratioResponse.setQuickRatio(resultData.getQuickRatio());
			String year = getYear(resultData.getYear());
			ratioResponse.setYear(year);
			response.add(ratioResponse);
		}
		return response;
	}

	private String getYear(String year) {
		String mounthYear = "";
		try {
			String firstFourChars = year.substring(0, 4);
			int length = year.length();
			String lastTwoChars = year.substring(length - 2);
			String mounth = "";
			if (lastTwoChars.equalsIgnoreCase("01")) {
				mounth = "JAN";
			} else if (lastTwoChars.equalsIgnoreCase("02")) {
				mounth = "FEB";
			} else if (lastTwoChars.equalsIgnoreCase("03")) {
				mounth = "MAR";
			} else if (lastTwoChars.equalsIgnoreCase("04")) {
				mounth = "APR";
			} else if (lastTwoChars.equalsIgnoreCase("05")) {
				mounth = "MAY";
			} else if (lastTwoChars.equalsIgnoreCase("06")) {
				mounth = "JUN";
			} else if (lastTwoChars.equalsIgnoreCase("07")) {
				mounth = "JUL";
			} else if (lastTwoChars.equalsIgnoreCase("08")) {
				mounth = "AUG";
			} else if (lastTwoChars.equalsIgnoreCase("09")) {
				mounth = "SEP";
			} else if (lastTwoChars.equalsIgnoreCase("10")) {
				mounth = "OCT";
			} else if (lastTwoChars.equalsIgnoreCase("11")) {
				mounth = "NOV";
			} else if (lastTwoChars.equalsIgnoreCase("12")) {
				mounth = "DEC";
			}
			mounthYear = mounth + " " + firstFourChars;

		} catch (Exception e) {
			Log.error(e);
		}
		return mounthYear;
	}

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getQuarterlyTrend(QuarterlyTrendReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		QuarterlyTrendRestResp restResp = new QuarterlyTrendRestResp();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getQuarterlyTrend");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			accessLogModel.setUserId(info.getUserId());

			String request = "/cds/1404/v1/" + req.getExchange() + "/" + req.getToken() + "/" + req.getQuarterlytend()
					+ "/GetQuarterlyTrend";
			URL url = new URL(props.getBaseUrl() + request);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("GetQuarterlyTrend responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getQuarterlyTrend_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.NO_DATA_FOUND)) {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.NO_RECORDS_FOUND);
				}
				if (StringUtil.isNotNullOrEmpty(output)) {
					restResp = mapper.readValue(output, QuarterlyTrendRestResp.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<QuarterlyTrendResp> extract = bindQuarterlyTrendResp(
								restResp.getResponseObject().getResultset());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!restResp.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
					}
				} else {
					System.out
							.println("Error Connection in Company Financial Ratio Data Rsponse code - " + responseCode);
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					if (StringUtil.isNotNullOrEmpty(output)) {
						restResp = mapper.readValue(output, QuarterlyTrendRestResp.class);
						if (StringUtil.isNotNullOrEmpty(restResp.getResponseObject().getErrorMessage()))
							return prepareResponse.prepareFailedResponseForRestService(
									restResp.getResponseObject().getErrorMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<QuarterlyTrendResp> bindQuarterlyTrendResp(List<QuarterlyTrendResultset> quarterlyTrendList) {
		List<QuarterlyTrendResp> response = new ArrayList<>();
		for (QuarterlyTrendResultset rSet : quarterlyTrendList) {
			QuarterlyTrendResp result = new QuarterlyTrendResp();
			result.setCompanyCode(rSet.getCompanyCode());
			result.setProfitMargin(rSet.getProfitMargin());
			result.setRevenu(rSet.getRevenu());
			String year = getYear(rSet.getYrc());
			result.setYrc(year);
			response.add(result);
		}

		return response;
	}

	/**
	 * Method to get Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getShareHoldingData(CFRatioReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		ShareHoldingRestResponseObject restResp = new ShareHoldingRestResponseObject();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getShareHoldingPatternData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			accessLogModel.setUserId(info.getUserId());

			String request = "/cds/1404/v1/" + req.getMktSegmentId() + "/" + req.getToken() + "/"
					+ "/GetShareHoldingPatternData";
			URL url = new URL(props.getBaseUrl() + request);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Share Holding Pattern Data responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getShareHoldingPatternData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, ShareHoldingRestResponseObject.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<ShareHoldingsResp> extract = bindShareHoldingResp(
								restResp.getResponseObject().getResultset());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!restResp.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
					}
				} else {
					System.out
							.println("Error Connection in Company Financial Ratio Data Rsponse code - " + responseCode);
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
					bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
					output = bufferedReader.readLine();
					if (StringUtil.isNotNullOrEmpty(output)) {
						restResp = mapper.readValue(output, ShareHoldingRestResponseObject.class);
						if (StringUtil.isNotNullOrEmpty(restResp.getResponseObject().getErrorMessage()))
							return prepareResponse.prepareFailedResponseForRestService(
									restResp.getResponseObject().getErrorMessage());
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<ShareHoldingsResp> bindShareHoldingResp(List<ShareHolResultset> resultset) {
		List<ShareHoldingsResp> response = new ArrayList<>();

		System.out.println("resultset size--" + resultset.size());
		for (ShareHolResultset rSet : resultset) {
			ShareHoldingsResp result = new ShareHoldingsResp();
			result.setCompanyCode(rSet.getCompanyCode());
			result.setFIIHolding(rSet.getFiiHolding());
			result.setMutualFundHolding(rSet.getMutualFundHolding());
			result.setOthers(rSet.getOthers());
			result.setPromoterHolding(rSet.getPromoterHolding());
			result.setYrc(rSet.getYrc());

			ShareHoldingsScripDataResp scripDataBSE = new ShareHoldingsScripDataResp();
			scripDataBSE.setCompanyCode(rSet.getScripDataBSE().getCompanyCode());
//			scripDataBSE.setMktSegmentId(null);
			scripDataBSE.setODINCode(rSet.getScripDataBSE().getODINCode());
			scripDataBSE.setSeries(rSet.getScripDataBSE().getSeries());
			scripDataBSE.setSymbol(rSet.getScripDataBSE().getSymbol());
			result.setScripDataBSE(scripDataBSE);

			ShareHoldingsScripDataResp scripDataNSE = new ShareHoldingsScripDataResp();
			scripDataNSE.setCompanyCode(rSet.getScripDataNSE().getCompanyCode());
//			scripDataNSE.setMktSegmentId(null);
			scripDataNSE.setODINCode(rSet.getScripDataNSE().getODINCode());
			scripDataNSE.setSeries(rSet.getScripDataNSE().getSeries());
			scripDataNSE.setSymbol(rSet.getScripDataNSE().getSymbol());
			result.setScripDataNSE(scripDataNSE);

			response.add(result);
		}
		return response;
	}

	/**
	 * method to get analysisData from server
	 * 
	 * @author SOWMIYA
	 * @param baseUrl
	 * @return
	 */
	public List<AnalysisRespModel> getFundamentalAnalysisData(String topGainerUrl) {
		ObjectMapper mapper = new ObjectMapper();
		List<AnalysisRespModel> response = new ArrayList<>();
		String output = "";
		try {
			URL url = new URL(props.getAnalysisBaseUrl() + topGainerUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			output = reader.readLine();
			if (StringUtil.isNotNullOrEmpty(output)) {
				List<AnalysisRestResponseModel> analysisRestResponseModels = mapper.readValue(output,
						new TypeReference<List<AnalysisRestResponseModel>>() {
						});
				response = bindResp(analysisRestResponseModels);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to bind response
	 * 
	 * @author Dinesh Kumar
	 * @param responseModels
	 * @return
	 */
	private List<AnalysisRespModel> bindResp(List<AnalysisRestResponseModel> responseModels) {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			if (HazelcastConfig.getInstance().getNseTokenCache().isEmpty()) {
				entityManager.loadNSEData();
			}
			for (AnalysisRestResponseModel model : responseModels) {
				AnalysisRespModel analysisRespModel = new AnalysisRespModel();
				String symbol = model.getSymbol().trim();
				analysisRespModel.setSymbol(symbol);
				analysisRespModel.setDateval(model.getDateval());
				analysisRespModel.setClosePerChg(model.getClosePerChg());
				analysisRespModel.setDirection(model.getDirection());
				analysisRespModel.setHighlight(model.getHighlight());
				analysisRespModel.setIsin(model.getIsin());
				analysisRespModel.setLtp(model.getLtp());
				analysisRespModel.setPdc(model.getPdc());
				String token = "";
				if (HazelcastConfig.getInstance().getNseTokenCache().get(symbol) != null) {
					token = HazelcastConfig.getInstance().getNseTokenCache().get(model.getSymbol());
				}
				analysisRespModel.setToken(token);

				if (StringUtil.isNotNullOrEmpty(token)) {
					String key = AppConstants.NSE + "_" + token;
					if (HazelcastConfig.getInstance().getContractMaster().get(key) != null) {
						ContractMasterModel contractModel = HazelcastConfig.getInstance().getContractMaster().get(key);
						if (contractModel != null) {
							analysisRespModel.setCompanyName(contractModel.getCompanyName());
						}
					}
				}

				analysisRespModel.setExch(AppConstants.NSE);
				response.add(analysisRespModel);

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	public RestResponse<GenericResponse> getProfitLossData(String profitLossUr, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		List<ProfitLossRespModel> responseModel = new ArrayList<>();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getProfitLossData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(profitLossUr);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("ProfitLossData responseCode--" + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getProfitLossData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
					ProfitLostRestRespModel restRespModel = mapper.readValue(output, ProfitLostRestRespModel.class);
					if (restRespModel.getResponseObject() != null
							&& restRespModel.getResponseObject().getType().equals(AppConstants.REST_STATUS_SUCCESS)) {

						responseModel = bindProfitLossData(restRespModel);
						return prepareResponse.prepareSuccessResponseObject(responseModel);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}

		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to bind profit and loss data
	 * 
	 * @author SOWMIYA
	 * @param restRespModel
	 * @return
	 */
	private List<ProfitLossRespModel> bindProfitLossData(ProfitLostRestRespModel restRespModel) {
		List<ProfitLossRespModel> responseModel = new ArrayList<>();
		try {
			int count = 1;
			for (ResultsetRestModel model : restRespModel.getResponseObject().getResultset()) {
				ProfitLossRespModel respModel = new ProfitLossRespModel();
				respModel.setSNo(model.getSNo());
				respModel.setColumnName(model.getColumnName());
				if (count == 1) {
					count++;
					String currentFinYr = getYear(model.getCurrentFinYr());
					respModel.setCurrentFinYr(currentFinYr);
					String yoy1 = getYear(model.getYoy1());
					respModel.setYoy1(yoy1);
					String yoy1Per = getYear(model.getYoy1Per());
					respModel.setYoy1Per(yoy1Per);
					String yoy2 = getYear(model.getYoy2());
					respModel.setYoy2(yoy2);
					String yoy2Per = getYear(model.getYoy2Per());
					respModel.setYoy2Per(yoy2Per);
					responseModel.add(respModel);
				} else {
					respModel.setCurrentFinYr(model.getCurrentFinYr());
					respModel.setYoy1(model.getYoy1());
					respModel.setYoy1Per(model.getYoy1Per());
					respModel.setYoy2(model.getYoy2());
					respModel.setYoy2Per(model.getYoy2Per());
					responseModel.add(respModel);
				}

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return responseModel;
	}

	/**
	 * method to get company balance sheet data
	 * 
	 * @author SOWMIYA
	 * @param sheetDataUrl
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyBalanceSheetData(String sheetDataUrl, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		List<CompanyRespModel> responseModel = new ArrayList<>();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getCompanyBalanceSheetData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(sheetDataUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("Company Balance Sheet Data ResponseCode--" + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getCompanyBalanceSheetData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
					BalanceSheetRestRespModel restRespModel = mapper.readValue(output, BalanceSheetRestRespModel.class);
					if (restRespModel.getResponseObject() != null
							&& restRespModel.getResponseObject().getType().equals(AppConstants.REST_STATUS_SUCCESS)) {

						responseModel = bindBlnSheetData(restRespModel);
						return prepareResponse.prepareSuccessResponseObject(responseModel);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to bind balance sheet data
	 * 
	 * @author SOWMIYA
	 * @param restRespModel
	 * @return
	 */
	private List<CompanyRespModel> bindBlnSheetData(BalanceSheetRestRespModel restRespModel) {
		List<CompanyRespModel> responseModel = new ArrayList<>();
		try {
			int count = 1;
			for (BlnSheetResultset model : restRespModel.getResponseObject().getResultset()) {
				CompanyRespModel resultModel = new CompanyRespModel();
				resultModel.setColumnName(model.getColumnName());
				if (count == 1) {
					count++;
					String currentYear = getYear(model.getCurrentYear());
					resultModel.setCurrentYear(currentYear);
					String year1 = getYear(model.getYear1());
					resultModel.setYear1(year1);
					String year2 = getYear(model.getYear2());
					resultModel.setYear2(year2);
					responseModel.add(resultModel);
				} else {
					resultModel.setCurrentYear(model.getCurrentYear());
					resultModel.setYear1(model.getYear1());
					resultModel.setYear2(model.getYear2());
					responseModel.add(resultModel);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return responseModel;
	}

	/**
	 * method to get company result data
	 * 
	 * @author SOWMIYA
	 * @param companyUrl
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyResultData(String companyUrl, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		List<CompanyRespModel> responseModel = new ArrayList<>();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getCompanyResultData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(companyUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("responseCode--" + responseCode);
			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getCompanyResultData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
					BalanceSheetRestRespModel restRespModel = mapper.readValue(output, BalanceSheetRestRespModel.class);
					if (restRespModel.getResponseObject() != null
							&& restRespModel.getResponseObject().getType().equals(AppConstants.REST_STATUS_SUCCESS)) {

						responseModel = bindBlnSheetData(restRespModel);
						return prepareResponse.prepareSuccessResponseObject(responseModel);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Indian Market News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndianMktNews(MktNewsReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		IndianMktNewsDataResponseObj restResp = new IndianMktNewsDataResponseObj();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getShareHoldingPatternData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String request = "/cds/1404/v1/" + req.getSecName() + "/" + req.getPageSize() + "/"
					+ "GetIndianMktNewsData";
			URL url = new URL(props.getBaseUrl() + request);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getIndianMktNews_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			System.out.println("Indian Mkt News responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, IndianMktNewsDataResponseObj.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						List<IndianMktNewsResp> extract = bindIndMktNewsResp(
								restResp.getResponseObject().getResultset());
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else if (!restResp.getResponseObject().getType()
							.equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
					}
				}
			} else {
				System.out.println("Error Connection in Indian Mkt News Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					restResp = mapper.readValue(output, IndianMktNewsDataResponseObj.class);
					if (StringUtil.isNotNullOrEmpty(restResp.getResponseObject().getErrorMessage()))
						return prepareResponse
								.prepareFailedResponseForRestService(restResp.getResponseObject().getErrorMessage());
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Indian Market News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<IndianMktNewsResp> bindIndMktNewsResp(List<IndMktNewsResultset> resultset) {
		List<IndianMktNewsResp> response = new ArrayList<>();
		IndianMktNewsResp result = new IndianMktNewsResp();
		for (int i = 0; i < resultset.size(); i++) {
			result.setArtText(resultset.get(i).getArtText());
			result.setCaption(resultset.get(i).getCaption());
			result.setCompanyCode(resultset.get(i).getCompanyCode());
			result.setDate(resultset.get(i).getDate());
			result.setFlag(resultset.get(i).getFlag());
			result.setHeading(resultset.get(i).getHeading());
			result.setIndustryCode(resultset.get(i).getIndustryCode());
			result.setSecName(resultset.get(i).getSecName());
			result.setSNo(resultset.get(i).getSNo());
			result.setTime(resultset.get(i).getTime());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetails(String userSession, String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		IndexDetailsRestResp restResp = new IndexDetailsRestResp();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getIndexDetails");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(props.getIndexDetails());
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Index Detail responseCode--" + responseCode);

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getIndexDetails_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, IndexDetailsRestResp.class);
					if (restResp.isStatus()) {
						List<IndexDetailsRespModify> extract = bindIndexDetailsResp(restResp.getResult());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}

			} else {
				System.out.println("Error Connection in Index Detail Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<IndexDetailsRespModify> bindIndexDetailsResp(List<IndexDetailsResult> resultset) {
		List<IndexDetailsRespModify> response = new ArrayList<>();
		for (IndexDetailsResult rSet : resultset) {
			IndexDetailsRespModify result = new IndexDetailsRespModify();
			if (rSet.getIndexDesc().equalsIgnoreCase("NIFTY 100") || rSet.getIndexDesc().equalsIgnoreCase("NIFTY 200")
					|| rSet.getIndexDesc().equalsIgnoreCase("NIFTY MIDCAP 50")
					|| rSet.getIndexDesc().equalsIgnoreCase("NIFTY NEXT 50")
					|| rSet.getIndexDesc().equalsIgnoreCase("Nifty Fin Service")
					|| rSet.getIndexDesc().equalsIgnoreCase("NIFTY SMLCAP 100")) {

				int segmentId = rSet.getNMarketSegmentId();
				if (segmentId == 1) {
					result.setCIsIndex(rSet.getIsIndex());
					result.setIndex(rSet.getIndex());
					result.setIndexDesc(rSet.getIndexDesc());
					result.setIsDefaultIndex(rSet.getIsDefaultIndex());
					result.setToken(rSet.getToken());
					result.setMktSegmentId(AppConstants.NSE);
					response.add(result);
					HazelcastConfig.getInstance().getIndicesModify().put(rSet.getIndexDesc(), result);
				} else if (segmentId == 8) {
					result.setCIsIndex(rSet.getIsIndex());
					result.setIndex(rSet.getIndex());
					result.setIndexDesc(rSet.getIndexDesc());
					result.setIsDefaultIndex(rSet.getIsDefaultIndex());
					result.setToken(rSet.getToken());
					result.setMktSegmentId(AppConstants.BSE);
					response.add(result);
					HazelcastConfig.getInstance().getIndicesModify().put(rSet.getIndexDesc(), result);
				}

			}

		}

		List<String> index = new ArrayList<>();
		for (IndexDetailsRespModify rSet : response) {
			index.add(rSet.getIndexDesc());
		}
		Iterator<String> itr = index.iterator();
		itr = sortedIterator(itr);
		List<IndexDetailsRespModify> finalResponse = new ArrayList<>();
		while (itr.hasNext()) {
			IndexDetailsRespModify res = HazelcastConfig.getInstance().getIndicesModify().get(itr.next());
			finalResponse.add(res);

		}
		return finalResponse;
	}

	public Iterator<String> sortedIterator(Iterator<String> it) {
		List<String> list = new ArrayList<>();
		while (it.hasNext()) {
			list.add((String) it.next());
		}
		Collections.sort(list);
		return list.iterator();
	}

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetailsAll(String userSession, String userId) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		IndexDetailsRestResp restResp = new IndexDetailsRestResp();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getIndexDetailsAll");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(props.getIndexDetails());
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Index Detail responseCode--" + responseCode);

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getIndexDetailsAll_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, IndexDetailsRestResp.class);
					if (restResp.isStatus()) {
						List<IndexDetailsRespModify> extract = bindIndexDetailsRespAll(restResp.getResult());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}

			} else {
				System.out.println("Error Connection in Index Detail Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<IndexDetailsRespModify> bindIndexDetailsRespAll(List<IndexDetailsResult> resultset) {
		List<IndexDetailsRespModify> response = new ArrayList<>();
		for (IndexDetailsResult rSet : resultset) {
			IndexDetailsRespModify result = new IndexDetailsRespModify();
			int segmentId = rSet.getNMarketSegmentId();
			if (segmentId == 1) {
				result.setCIsIndex(rSet.getIsIndex());
				result.setIndex(rSet.getIndex());
				result.setIndexDesc(rSet.getIndexDesc());
				result.setIsDefaultIndex(rSet.getIsDefaultIndex());
				result.setMktSegmentId(AppConstants.NSE);
				result.setToken(rSet.getToken());
				response.add(result);
				HazelcastConfig.getInstance().getIndicesModify().put(rSet.getIndexDesc(), result);
			} else if (segmentId == 8) {
				result.setCIsIndex(rSet.getIsIndex());
				result.setIndex(rSet.getIndex());
				result.setIndexDesc(rSet.getIndexDesc());
				result.setIsDefaultIndex(rSet.getIsDefaultIndex());
				result.setMktSegmentId(AppConstants.BSE);
				result.setToken(rSet.getToken());
				response.add(result);
				HazelcastConfig.getInstance().getIndicesModify().put(rSet.getIndexDesc(), result);
			}

		}
		List<String> index = new ArrayList<>();
		for (IndexDetailsRespModify rSet : response) {
			index.add(rSet.getIndexDesc());
		}
		Iterator<String> itr = index.iterator();
		itr = sortedIterator(itr);
		List<IndexDetailsRespModify> finalResponse = new ArrayList<>();
		while (itr.hasNext()) {
			IndexDetailsRespModify res = HazelcastConfig.getInstance().getIndicesModify().get(itr.next());
			finalResponse.add(res);

		}
		return finalResponse;
	}

	/**
	 * Method to get Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getHotPursuitData(MktNewsReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		HotPursuitDataRespObj restResp = new HotPursuitDataRespObj();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getHotPursuitData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getHotPursuitData() + req.getPageSize() + "/GetHotPursuitData";

			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Hot Pursuit Data responseCode--" + responseCode);

			if (responseCode != AppConstants.RESPONSE_CODE) {
				ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
				statesCode.setCount(1);
				statesCode.setMethodAndModel("getHotPursuitData_" + AppConstants.MODULE_COMMON);
				statesCode.setResponsecode(responseCode);
				responsecodeInCache(statesCode);
			}

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, HotPursuitDataRespObj.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						/** Method to bind HotPursuitRes */
						List<HotPursuitDataResp> extract = bindHotPursuitResp(
								restResp.getResponseObject().getResultSet());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Hot Pursuit Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to Bind Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<HotPursuitDataResp> bindHotPursuitResp(List<HotPursuitResultSet> resultSet) {
		List<HotPursuitDataResp> response = new ArrayList<>();

//		for (HotPursuitResultSet rSet : resultSet) {
		for (int i = 0; i < resultSet.size(); i++) {
			HotPursuitDataResp result = new HotPursuitDataResp();
			result.setSNo(resultSet.get(i).getSNo());
			if (resultSet.get(i).getScripDataNSE() != null) {
				ShareHoldingsScripDataResp scripDataNSE = new ShareHoldingsScripDataResp();
				scripDataNSE.setCompanyCode(resultSet.get(i).getScripDataNSE().getCompanyCode());
				scripDataNSE.setMktSegmentId(resultSet.get(i).getScripDataNSE().getMktSegmentId());
				scripDataNSE.setODINCode(resultSet.get(i).getScripDataNSE().getODINCode());
				scripDataNSE.setSeries(resultSet.get(i).getScripDataNSE().getSeries());
				scripDataNSE.setSymbol(resultSet.get(i).getScripDataNSE().getSymbol());
				result.setScripDataNSE(scripDataNSE);
			}
			if (resultSet.get(i).getScripDataBSE() != null) {
				ShareHoldingsScripDataResp scripDataBSE = new ShareHoldingsScripDataResp();
				scripDataBSE.setCompanyCode(resultSet.get(i).getScripDataBSE().getCompanyCode());
				scripDataBSE.setMktSegmentId(resultSet.get(i).getScripDataBSE().getMktSegmentId());
				scripDataBSE.setODINCode(resultSet.get(i).getScripDataBSE().getODINCode());
				scripDataBSE.setSeries(resultSet.get(i).getScripDataBSE().getSeries());
				scripDataBSE.setSymbol(resultSet.get(i).getScripDataBSE().getSymbol());
				result.setScripDataBSE(scripDataBSE);
			}
			result.setCompanyCode(resultSet.get(i).getCompanyCode());
			result.setIndustryCode(resultSet.get(i).getIndustryCode());
			result.setSecName(resultSet.get(i).getSectionName());
			result.setDate(resultSet.get(i).getDate());
			result.setTime(resultSet.get(i).getTime());
			result.setHeading(resultSet.get(i).getHeading());
			result.setCaption(resultSet.get(i).getCaption());
			result.setArtText(resultSet.get(i).getArtText());
			result.setFlag(resultSet.get(i).getFlag());
			response.add(result);

		}
		return response;
	}

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorList(SectorListReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		SectorListRespObj restResp = new SectorListRespObj();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getSectorList");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getSectorlist() + req.getPageSize() + "/GetSectorList";

			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			accessLogModel.setReqBody(url.toString());
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			System.out.println("Sector List responseCode--" + responseCode);

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getSectorList_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, SectorListRespObj.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						/** Method to bind SectorListRespObj */
						List<SectorListReponse> extract = bindSectorListResp(
								restResp.getResponseObject().getResultSet());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Hot Pursuit Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<SectorListReponse> bindSectorListResp(List<SectorListResultSet> resultSet) {
		List<SectorListReponse> response = new ArrayList<>();
		for (SectorListResultSet rSet : resultSet) {
			SectorListReponse result = new SectorListReponse();
			result.setSecCode(rSet.getSecCode());
			result.setSecName(rSet.getSecName());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorWiseNewsData(SectorListReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		SectorWiseNewsDataRespObj restResp = new SectorWiseNewsDataRespObj();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getSectorWiseNewsData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getSectorwisenewsdata() + req.getSectorCode() + "/1/" + req.getPageSize()
					+ "/GetSectorWiseNewsData";
			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Sector Wise News Data responseCode--" + responseCode);

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getSectorWiseNewsData_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, SectorWiseNewsDataRespObj.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						/** Method to bind SectorListRespObj */
						List<SectorWiseNewsDataResp> extract = bindSectorWiseNewsResp(
								restResp.getResponseObject().getResultSet());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Sector Wise News Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<SectorWiseNewsDataResp> bindSectorWiseNewsResp(List<SectorWiseResultset> resultSet) {
		List<SectorWiseNewsDataResp> response = new ArrayList<>();
		for (SectorWiseResultset rSet : resultSet) {
			SectorWiseNewsDataResp result = new SectorWiseNewsDataResp();
			result.setCaption(rSet.getCaption());
			result.setDate(rSet.getDate());
			result.setHeading(rSet.getHeading());
			result.setSecName(rSet.getSecName());
			result.setSNo(rSet.getSNo());
			result.setTime(rSet.getTime());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to get AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	public RestResponse<GenericResponse> getAnnoucementsData(SectorListReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		AnnoucementsDataRespObj restResp = new AnnoucementsDataRespObj();

		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getAnnoucementsData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getAnnoucementsdata() + req.getExchange() + "/1/" + "100" + "/GetAnnoucementsData";
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("AnnoucementsData responseCode--" + responseCode);

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getAnnoucementsData_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, AnnoucementsDataRespObj.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						/** Method to bind SectorListRespObj */
						List<AnnoucementsDataResp> extract = bindAnnoucementsData(
								restResp.getResponseObject().getResultSet());

						List<AnnoucementsDataEntity> annoucementsData = prepareAnnoucementsData(extract,
								req.getExchange());
//						List<AnnoucementsDataEntity> datas = annoucementsDataRepository.saveAll(annoucementsData);

						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Sector Wise News Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @param exch
	 * @return
	 */
	private List<AnnoucementsDataEntity> prepareAnnoucementsData(List<AnnoucementsDataResp> extract, String exch) {
		List<AnnoucementsDataEntity> response = new ArrayList<>();
		for (AnnoucementsDataResp rSet : extract) {
			AnnoucementsDataEntity result = new AnnoucementsDataEntity();
			result.setBseCode(rSet.getBseCode());
			result.setCaption(rSet.getCaption());
			result.setCompanyCode(rSet.getCompanyCode());
			result.setDate(rSet.getDate());
			result.setExch(exch);
			result.setMemo(rSet.getMemo());
			if (rSet.getScripDataBSE() != null) {
				result.setMktSegmentIdBSE(String.valueOf(rSet.getScripDataBSE().getMktSegmentId()));
				result.setOdincodeBSE(String.valueOf(rSet.getScripDataBSE().getODINCode()));
				result.setSeriesBSE(rSet.getScripDataBSE().getSeries());
			}
			if (rSet.getScripDataNSE() != null) {
				result.setMktSegmentIdNSE(String.valueOf(rSet.getScripDataNSE().getMktSegmentId()));
				result.setOdincodeNSE(String.valueOf(rSet.getScripDataNSE().getODINCode()));
				result.setSeriesNSE(rSet.getScripDataNSE().getSeries());
			}
			result.setSymbol(rSet.getSymbol());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to bind AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<AnnoucementsDataResp> bindAnnoucementsData(List<AnnoucementsDataResultSet> resultSet) {
		List<AnnoucementsDataResp> response = new ArrayList<>();
		try {
			for (AnnoucementsDataResultSet rSet : resultSet) {
				AnnoucementsDataResp result = new AnnoucementsDataResp();
				AnnoucementsScripDataResp scripDataNSE = new AnnoucementsScripDataResp();
				AnnoucementsScripDataResp scripDataBSE = new AnnoucementsScripDataResp();
				result.setBseCode(rSet.getBseCode());
				result.setCaption(rSet.getCaption());
				result.setCompanyCode(rSet.getCompanyCode());
				result.setCompanyLongName(rSet.getCompanyLongName());
				result.setDate(rSet.getDate());
				result.setMemo(rSet.getMemo());
				result.setSymbol(rSet.getSymbol());

				if (rSet.getScripDataNSE() != null) {
					scripDataNSE.setCompanyCode(rSet.getScripDataNSE().getCompanyCode());
					scripDataNSE.setMktSegmentId(rSet.getScripDataNSE().getMktSegmentId());
					scripDataNSE.setODINCode(rSet.getScripDataNSE().getODINCode());
					scripDataNSE.setSeries(rSet.getScripDataNSE().getSeries());
					scripDataNSE.setSymbol(rSet.getScripDataNSE().getSymbol());
					result.setScripDataNSE(scripDataNSE);
				}

				if (rSet.getScripDataBSE() != null) {
					scripDataBSE.setCompanyCode(rSet.getScripDataBSE().getCompanyCode());
					scripDataBSE.setMktSegmentId(rSet.getScripDataBSE().getMktSegmentId());
					scripDataBSE.setODINCode(rSet.getScripDataBSE().getODINCode());
					scripDataBSE.setSeries(rSet.getScripDataBSE().getSeries());
					scripDataBSE.setSymbol(rSet.getScripDataBSE().getSymbol());
					result.setScripDataBSE(scripDataBSE);
					response.add(result);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getMovingAverage(CompanyFinReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		MovingAverageRespObj restResp = new MovingAverageRespObj();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getMovingAverage");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl1 = props.getMovingAverage() + req.getSegment().get(0) + "/" + req.getToken().get(0)
					+ "/GetMovingAverage";

			String strUrl2 = props.getMovingAverage() + req.getSegment().get(1) + "/" + req.getToken().get(1)
					+ "/GetMovingAverage";

			URL url = new URL(strUrl1);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Moving Average responseCode--" + responseCode);

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getMovingAverage_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					if (!output.contains(AppConstants.NO_DATA_FOUND)) {
						restResp = mapper.readValue(output, MovingAverageRespObj.class);
						if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
							/** Method to bind SectorListRespObj */
//							List<MovingAverageResponse> extract = bindMovingAverage(
//									restResp.getResponseObject().getResultSet());
							List<JSONObject> extract = bindMovingAverage(restResp.getResponseObject().getResultSet());
//							List<MovingAverageResponse> secondExtract = getMovingAverageTwo(strUrl2, info);
							List<JSONObject> secondExtract = getMovingAverageTwo(strUrl2, info);
							MovingAverageResp extractResp = new MovingAverageResp();
							if (req.getSegment().get(0).equalsIgnoreCase(AppConstants.NSE_EQ)) {
								extractResp.setMovingAverageNSE(extract);
								if (StringUtil.isListNotNullOrEmpty(secondExtract)) {
									extractResp.setMovingAverageBSE(secondExtract);
								}
							} else {
								if (StringUtil.isListNotNullOrEmpty(secondExtract)) {
									extractResp.setMovingAverageNSE(secondExtract);
								}
								extractResp.setMovingAverageBSE(extract);
							}
							return prepareResponse.prepareSuccessResponseObject(extractResp);
						}
					} else {
//						List<MovingAverageResponse> secondExtract = getMovingAverageTwo(strUrl2, info);
						List<JSONObject> secondExtract = getMovingAverageTwo(strUrl2, info);
						MovingAverageResp extractResp = new MovingAverageResp();
						if (req.getSegment().get(0).equalsIgnoreCase(AppConstants.NSE_EQ)) {
							extractResp.setMovingAverageBSE(secondExtract);
						} else {
							extractResp.setMovingAverageNSE(secondExtract);
						}
						return prepareResponse.prepareSuccessResponseObject(extractResp);
					}

				}
			} else {
				System.out.println("Error Connection in Get Moving Average Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<JSONObject> getMovingAverageTwo(String strUrl2, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		MovingAverageRespObj restResp = new MovingAverageRespObj();
		List<JSONObject> extract = new ArrayList<>();
		try {
			CodifiUtil.trustedManagement();
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getMovingAverage");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			URL url = new URL(strUrl2);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getMovingAverageTwo_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);
			System.out.println("Moving Average responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					if (!output.contains(AppConstants.NO_DATA_FOUND)) {
						restResp = mapper.readValue(output, MovingAverageRespObj.class);
						if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
							/** Method to bind SectorListRespObj */
							extract = bindMovingAverage(restResp.getResponseObject().getResultSet());
							return extract;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("getMovingAverageTwo -- " + e.getMessage());
		}
		return extract;
	}

	/**
	 * Method to bind moving average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
//	private List<MovingAverageResponse> bindMovingAverage(List<MovingAverageResultSet> resultSet) {
	@SuppressWarnings("unchecked")
	private List<JSONObject> bindMovingAverage(List<MovingAverageResultSet> resultSet) {
		List<JSONObject> objList = new ArrayList<>();
//		List<MovingAverageResponse> response = new ArrayList<>();
		for (MovingAverageResultSet rSet : resultSet) {
//			MovingAverageResponse result = new MovingAverageResponse();
//			result.setAvgFifteenDays(rSet.getAvgFifteenDays());
//			result.setAvgFiftyDays(rSet.getAvgFiftyDays());
//			result.setAvgHundredDays(rSet.getAvgHundredDays());
//			result.setAvgHunFiftydays(rSet.getAvgHunFiftydays());
//			result.setAvgThirtyDays(rSet.getAvgThirtyDays());
//			result.setAvgTwentyDays(rSet.getAvgTwentyDays());
//			result.setAvgTwoHundredDays(rSet.getAvgTwoHundredDays());
//			result.setCompanyCode(rSet.getCompanyCode());
//			response.add(result);

			JSONObject jobj = new JSONObject();
			jobj.put("15 Days", rSet.getAvgFifteenDays());
			jobj.put("50 Days", rSet.getAvgFiftyDays());
			jobj.put("100 Days", rSet.getAvgHundredDays());
			jobj.put("150 Days", rSet.getAvgHunFiftydays());
			jobj.put("30 Days", rSet.getAvgThirtyDays());
			jobj.put("20 Days", rSet.getAvgTwentyDays());
			jobj.put("200 Days", rSet.getAvgTwoHundredDays());
			jobj.put("companyCode", rSet.getAvgFifteenDays());
			objList.add(jobj);

		}
		return objList;
	}

	/**
	 * method to get scripwise news data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> getScripwiseNewsData(ScripwiseNewsReqModel model, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		ScripwiseNewsRespModel restResp = new ScripwiseNewsRespModel();
		String output = null;
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getScripwiseNewsData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getScripwiseNewsDataUrl() + "/" + model.getExchSeg() + "/" + model.getToken() + "/"
					+ model.getPageable() + props.getScripwiseNewsDataUrlEndpoints();
			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getScripwiseNewsData_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains(AppConstants.NO_DATA_FOUND)) {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.NO_RECORD_FOUND);
				}
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, ScripwiseNewsRespModel.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						restResp = bindScripwiseData(restResp);
						return prepareResponse.prepareSuccessResponseObject(restResp);
					}
				}
			} else {
				System.out.println("Error Connection in scrip Wise News Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error("getScripwiseNewsData", e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to bind scrip wise data
	 * 
	 * @author SOWMIYA
	 * @param restResp
	 * @return
	 */
	private ScripwiseNewsRespModel bindScripwiseData(ScripwiseNewsRespModel restResp) {
		try {
			for (ScripNewsResultset resultSet : restResp.getResponseObject().getResultset()) {
				String artText = resultSet.getArtText();
				String tempArtText = artText.replaceAll("\\<.*?\\>", "");
				resultSet.setArtText(tempArtText);

				String date = resultSet.getDate();
				String outputDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
				String dateFormat = "MM/dd/yyyy hh:mm:ss a";

				SimpleDateFormat inputSdf = new SimpleDateFormat(dateFormat);
				SimpleDateFormat outputSdf = new SimpleDateFormat(outputDateFormat);
				try {
					Date date1 = inputSdf.parse(date);
					String outputDateStr = outputSdf.format(date1);
//					System.out.println(outputDateStr);
					resultSet.setDate(outputDateStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			Log.error("bindScripwiseData", e);
		}
		return restResp;
	}

	/**
	 * method to get divident data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public RestResponse<GenericResponse> getDividentData(DividentReqModel model, String fromDate, String toDate,
			ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		DividentRespModel restResp = new DividentRespModel();
		String output = null;
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getDividentData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getDividentData() + "/" + model.getExchSeg() + "/" + model.getToken() + "/" + fromDate
					+ "/" + toDate + "/" + "100" + props.getDividentDataEndpoints();
			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getDividentData_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					if (output.contains(AppConstants.REST_STATUS_SUCCESS)) {
						restResp = mapper.readValue(output, DividentRespModel.class);
						if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
							return prepareResponse.prepareSuccessResponseObject(restResp);
						}
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.REST_NO_DATA);
					}
				}
			} else {
				System.out.println("Error Connection in divident Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error("getDividentData", e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * methd to get health total score
	 * 
	 * @author SOWMIYA
	 * 
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> getHealthTotalScore(HealthReqModel model, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		HealthRespModel restResp = new HealthRespModel();
		String output = null;
		try {
			CodifiUtil.trustedManagement();
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getHealthTotalScore");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getHealthTotalScoreUrl() + "/" + model.getExchSeg() + "/" + model.getToken()
					+ props.getHealthTotalScoreEndpoints();
			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getHealthTotalScore_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, HealthRespModel.class);
					if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
						return prepareResponse.prepareSuccessResponseObject(restResp);
					}
				}
			} else {
				System.out.println("Error Connection in Health Total  Score Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error("getHealthTotalScore", e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getPutCallRatio(PutCallRatioReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		PutCallRatioRespObj restResp = new PutCallRatioRespObj();
		try {
			CodifiUtil.trustedManagement();
			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getPutCallRatio");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);

			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			String strUrl = props.getPutCallRatio() + req.getSymbol() + "/" + req.getExpiryDate() + "/GetPutCallRatio";
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.JTENANT_ID, AppConstants.JTENANT_ID_VALUE);
			conn.setRequestProperty(AppConstants.JTENANT_TOKEN_KEY, AppConstants.JTENANT_TOKEN_VALUE);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getPutCallRatio_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			System.out.println("Put Call Ratio responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					if (!output.contains(AppConstants.NO_DATA_FOUND)) {
						restResp = mapper.readValue(output, PutCallRatioRespObj.class);
						if (restResp.getResponseObject().getType().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
							/** Method to bind SectorListRespObj */
							List<PutCallRatioResponse> extract = bindPutCallRatio(
									restResp.getResponseObject().getResultSet());
							return prepareResponse.prepareSuccessResponseObject(extract);
						}
					} else {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.NO_RECORD_FOUND);
					}
				}
			} else {
				System.out.println("Error Connection in Get Moving Average Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error("Test", e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<PutCallRatioResponse> bindPutCallRatio(List<PutCallRatioResultSet> resultSet) {
		List<PutCallRatioResponse> responses = new ArrayList<>();
		for (PutCallRatioResultSet rSet : resultSet) {
			PutCallRatioResponse result = new PutCallRatioResponse();
			result.setCallOI(rSet.getCallOI());
			result.setCallQty(rSet.getCallQty());
			result.setDate(rSet.getDate());
			result.setExpiryDate(rSet.getExpiryDate());
			result.setPCRatioOI(rSet.getPCRatioOI());
			result.setPCRatioQty(rSet.getPCRatioQty());
			result.setPutOI(rSet.getPutOI());
			result.setPutQty(rSet.getPutQty());
			result.setSymbol(rSet.getSymbol());
			result.setTotalOI(rSet.getTotalOI());
			result.setTotalQty(rSet.getTotalQty());
			responses.add(result);
		}
		return responses;
	}

	/**
	 * method to get Exchange Message
	 * 
	 * @author LOKESH
	 * @return
	 */

	public RestResponse<GenericResponse> getExchangeMsgRequest(ExchangeMsgRequest model, ClinetInfoModel info,
			String session) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		ResponseObjExchangeMsg restResp = new ResponseObjExchangeMsg();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("exchangeMsgData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getExchangeMessages() + model.getExchange() + "?noofhrs=" + model.getNoOfHrs()
					+ "&pageNo=" + model.getPageNo() + "&pageSize=" + model.getPageSize();

			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();
			statesCode.setCount(1);
			statesCode.setMethodAndModel("getExchangeMsgRequest_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			System.out.println("Exchange Msg Data responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, ResponseObjExchangeMsg.class);
					if (restResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_TRUE)) {
						/** Method to bind ExchangeMsgResultSet */
						List<ExchangeMsgRespModel> extract = ExchangeMsg(restResp.getResultSet());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Excange Message Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Exchange Message
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<ExchangeMsgRespModel> ExchangeMsg(List<ExchangeMsgResultSet> resultSet) {
		List<ExchangeMsgRespModel> response = new ArrayList<>();
		for (ExchangeMsgResultSet rSet : resultSet) {
			ExchangeMsgRespModel result = new ExchangeMsgRespModel();
			result.setSegmentId(rSet.getSegmentId());
			result.setTime(rSet.getTime());
			result.setMessages(rSet.getMsg());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to get Broker Message
	 * 
	 * @author LOKESH
	 * @return
	 */
	public RestResponse<GenericResponse> getBrokerMessage(BrokerMsgRequest model, ClinetInfoModel info,
			String session) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		ResponseObjBrokerMsg restResp = new ResponseObjBrokerMsg();
		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("brokerMsgData");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getBrokerMessages() + "?pageNo=" + model.getPageNo() + "&pageSize="
					+ model.getPageSize();

			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + session);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();

			statesCode.setCount(1);
			statesCode.setMethodAndModel("getBrokerMessage_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Broker Msg Data responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					System.out.println("output--" + output);
					restResp = mapper.readValue(output, ResponseObjBrokerMsg.class);
					if (restResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_TRUE)) {
						/** Method to bind BrokerMsgResultSet */
						List<BrokerMsgRespModel> extract = BrokerMsg(restResp.getResultSet());
						return prepareResponse.prepareSuccessResponseObject(extract);
					}
				}
			} else {
				System.out.println("Error Connection in Broker Message Data Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Broker Message
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<BrokerMsgRespModel> BrokerMsg(List<BrokerMsgResultSet> resultSet) {
		List<BrokerMsgRespModel> response = new ArrayList<>();
		for (BrokerMsgResultSet rSet : resultSet) {
			BrokerMsgRespModel result = new BrokerMsgRespModel();
			result.setMsgSrNo(rSet.getMsgSrNo());
			result.setSendMsgTime(rSet.getSendMsgTime());
			result.setSubject(rSet.getSubject());
			result.setMessage(rSet.getMessage());
			result.setMsgType(rSet.getMsgType());
			response.add(result);
		}
		return response;
	}

	/**
	 * Method to get Support And Resistance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSupportAndResistance(SupportAndResistanceReq req, ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		SupportAndResistanceRestResponse restResp = new SupportAndResistanceRestResponse();
		if (HazelcastConfig.getInstance().getSupportAndResistanceRestResponse()
				.get(req.getExch() + "_" + req.getToken()) != null)
			return prepareResponse.prepareSuccessResponseObject(HazelcastConfig.getInstance()
					.getSupportAndResistanceRestResponse().get(req.getExch() + "_" + req.getToken()));

		try {
			CodifiUtil.trustedManagement();

			RestAccessLogModel accessLogModel = new RestAccessLogModel();
			accessLogModel.setMethod("getSupportAndResistance");
			accessLogModel.setModule(AppConstants.MODULE_COMMON);
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			String strUrl = props.getSupportAndResistance();
			System.out.println("strUrl--" + strUrl);
			URL url = new URL(strUrl);
			accessLogModel.setReqBody(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = mapper.writeValueAsString(req).getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();

			ResponsecodeStatusModel statesCode = new ResponsecodeStatusModel();

			statesCode.setCount(1);
			statesCode.setMethodAndModel("getSupportAndResistance_" + AppConstants.MODULE_COMMON);
			statesCode.setResponsecode(responseCode);
			responsecodeInCache(statesCode);

			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			System.out.println("Support And Resistance responseCode--" + responseCode);

			BufferedReader bufferedReader;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (output.contains("No data")) {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
				} else if (output.contains("Ok")) {
					restResp = mapper.readValue(output, SupportAndResistanceRestResponse.class);
					if (restResp.getStat().equalsIgnoreCase("Ok")) {
						HazelcastConfig.getInstance().getSupportAndResistanceRestResponse()
								.put(req.getExch() + "_" + req.getToken(), restResp);
						return prepareResponse.prepareSuccessResponseObject(restResp);
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			Log.error("get Support And Resistance -- " + e.getMessage());
			e.printStackTrace();

		}

		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to save response code count in cache
	 * 
	 * @author Gowthaman
	 * @param statusModel
	 */
	public void responsecodeInCache(ResponsecodeStatusModel statusModel) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					CodifiUtil.responsecodeInCache(statusModel);
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage());
				}
			}
		});
		pool.shutdown();
	}

}

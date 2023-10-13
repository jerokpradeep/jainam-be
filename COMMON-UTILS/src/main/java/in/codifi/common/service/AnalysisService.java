package in.codifi.common.service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.EventDataModel;
import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.config.RestServiceProperties;
import in.codifi.common.entity.primary.AnnoucementsDataEntity;
import in.codifi.common.model.request.BrokerMsgRequest;
import in.codifi.common.model.request.CFRatioReq;
import in.codifi.common.model.request.CompanyReqModel;
import in.codifi.common.model.request.ExchangeMsgRequest;
import in.codifi.common.model.request.MktNewsReq;
import in.codifi.common.model.request.MovingAverageReq;
import in.codifi.common.model.request.ProfitLossReqModel;
import in.codifi.common.model.request.PutCallRatioReq;
import in.codifi.common.model.request.QuarterlyTrendReq;
import in.codifi.common.model.request.ScripwiseNewsReqModel;
import in.codifi.common.model.request.SectorListReq;
import in.codifi.common.model.request.SupportAndResistanceReq;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.repository.AnnoucementsDataRepository;
import in.codifi.common.repository.ContractEntityManger;
import in.codifi.common.service.spec.AnalysisServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.AppUtil;
import in.codifi.common.utility.StringUtil;
import in.codifi.common.ws.model.CompanyFinReq;
import in.codifi.common.ws.model.DividentReqModel;
import in.codifi.common.ws.model.FIIDIIResp;
import in.codifi.common.ws.model.HealthReqModel;
import in.codifi.common.ws.service.AnalysisRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AnalysisService implements AnalysisServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AnalysisRestService analysisRestService;
	@Inject
	RestServiceProperties props;
	@Inject
	AnnoucementsDataRepository annoucementsDataRepository;
	@Inject
	ContractEntityManger contractEntityManger;

	/**
	 * Method to get FII, DII, MF Activity Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getActivityData() {
		FIIDIIResp fiiDiiCache = HazelcastConfig.getInstance().getActivityData().get("activityData");
		System.out.println("Fii Dii cache -- "+fiiDiiCache);
		if (fiiDiiCache != null)
			return prepareResponse.prepareSuccessResponseObject(fiiDiiCache);
		return analysisRestService.getActivityData();
	}

	/**
	 * Method to get golable indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWorldIndicesData() {
//		WIResponse worldIndicesCache = HazelcastConfig.getInstance().getWorldIndicesData().get("worldIndicesData");
//		if (worldIndicesCache != null)
//			return prepareResponse.prepareSuccessResponseObject(worldIndicesCache);
		return analysisRestService.getWorldIndicesData();
	}

	/**
	 * Method to get golable indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWorldIndicesDataAll() {
//		WIResponse worldIndicesCache = HazelcastConfig.getInstance().getWorldIndicesData().get("worldIndicesData");
//		if (worldIndicesCache != null)
//			return prepareResponse.prepareSuccessResponseObject(worldIndicesCache);
		return analysisRestService.getWorldIndicesDataAll();
	}

	/**
	 * Method to get Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getCompanyFinancialRatioData(CFRatioReq req, ClinetInfoModel info) {
		if (StringUtil.isNotNullOrEmpty(req.getMktSegmentId()) && StringUtil.isNotNullOrEmpty(req.getToken())
				&& StringUtil.isNotNullOrEmpty(req.getType())) {
			if (req.getMktSegmentId().equalsIgnoreCase(AppConstants.NSE)) {
				req.setMktSegmentId(AppConstants.NSE_EQ);
			} else if (req.getMktSegmentId().equalsIgnoreCase(AppConstants.BSE)) {
				req.setMktSegmentId(AppConstants.BSE_EQ);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
			}

			if (req.getType().equalsIgnoreCase("Standalone")) {
				req.setType("S");
				return analysisRestService.getCompanyFinancialRatioData(req, info);
			} else if (req.getType().equalsIgnoreCase("Consolidated")) {
				req.setType("C");
				return analysisRestService.getCompanyFinancialRatioData(req, info);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		}
		return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
	}

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getQuarterlyTrend(QuarterlyTrendReq req, ClinetInfoModel info) {

		if (StringUtil.isNotNullOrEmpty(req.getExchange()) && StringUtil.isNotNullOrEmpty(req.getQuarterlytend())
				&& StringUtil.isNotNullOrEmpty(req.getToken())) {
			if (req.getQuarterlytend().equalsIgnoreCase("Revenue")
					|| req.getQuarterlytend().equalsIgnoreCase("Netprofit")
					|| req.getQuarterlytend().equalsIgnoreCase("EBITDA")
					|| req.getQuarterlytend().equalsIgnoreCase("EBIT")) {
				req.setQuarterlytend("QuarterlyTrend-" + req.getQuarterlytend());
				if (req.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
					req.setExchange(AppConstants.NSE_EQ);
				} else if (req.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
					req.setExchange(AppConstants.BSE_EQ);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
				}

				return analysisRestService.getQuarterlyTrend(req, info);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		}
		return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
	}

	/**
	 * Method to get Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getShareHoldingData(CFRatioReq req, ClinetInfoModel info) {
		if (StringUtil.isNotNullOrEmpty(req.getMktSegmentId()) && StringUtil.isNotNullOrEmpty(req.getToken())) {

			if (req.getMktSegmentId().equalsIgnoreCase(AppConstants.NSE)) {
				req.setMktSegmentId(AppConstants.NSE_EQ);
			} else if (req.getMktSegmentId().equalsIgnoreCase(AppConstants.BSE)) {
				req.setMktSegmentId(AppConstants.BSE_EQ);
			} else if (req.getMktSegmentId().equalsIgnoreCase(AppConstants.NFO)) {
				req.setMktSegmentId(AppConstants.NSE_FO);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
			}

			return analysisRestService.getShareHoldingData(req, info);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
	}

	/**
	 * method to get top gainers details from server
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> getTopGainers() {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			String topGainerKey = props.getTopGainersUrl() + "_" + "Bullish";
			String url = props.getTopGainersUrl();
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(topGainerKey) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysistopGainers().get(topGainerKey) != null) {
				response = HazelcastConfig.getInstance().getAnalysistopGainers().get(url);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				List<AnalysisRespModel> result = analysisRestService.getFundamentalAnalysisData(url);
				if (result != null && result.size() > 0) {
					for (AnalysisRespModel model : result) {
						String direction = model.getDirection();
						if (StringUtil.isNotNullOrEmpty(direction) && direction.equalsIgnoreCase("Bullish")) {
							response.add(model);
						}
					}
					HazelcastConfig.getInstance().getAnalysistopGainers().clear();
					HazelcastConfig.getInstance().getAnalysistopGainers().put(topGainerKey, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get top losers from server
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> getTopLosers() {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			String topLoser = props.getTopGainersUrl() + "_" + "Bearish";
			String url = props.getTopGainersUrl();
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(topLoser) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysistopLosers().get(topLoser) != null) {
				response = HazelcastConfig.getInstance().getAnalysistopLosers().get(topLoser);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {

				List<AnalysisRespModel> result = analysisRestService.getFundamentalAnalysisData(url);
				if (result != null && result.size() > 0) {
					for (AnalysisRespModel model : result) {
						String direction = model.getDirection();
						if (StringUtil.isNotNullOrEmpty(direction) && direction.equalsIgnoreCase("Bearish")) {
							response.add(model);
						}
					}
					HazelcastConfig.getInstance().getAnalysistopLosers().clear();
					HazelcastConfig.getInstance().getAnalysistopLosers().put(topLoser, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get analysis data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getFiftyTwoWeekHigh(String url) {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().get(url);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().clear();
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get analysis data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getAnalysisData(String url) {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisData().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisData().get(url);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisData().clear();
					HazelcastConfig.getInstance().getAnalysisData().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get analysis data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getFiftyTwoWeekLow(String url) {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().get(url);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().clear();
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareSuccessResponseObject(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 * @updated Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getProfitLossData(ProfitLossReqModel reqModel, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(reqModel.getType()) && StringUtil.isNotNullOrEmpty(reqModel.getToken())
					&& StringUtil.isNotNullOrEmpty(reqModel.getMarketSegmentId())) {
				if (reqModel.getMarketSegmentId().equalsIgnoreCase(AppConstants.NSE)) {
					reqModel.setMarketSegmentId(AppConstants.NSE_EQ);
				} else if (reqModel.getMarketSegmentId().equalsIgnoreCase(AppConstants.BSE)) {
					reqModel.setMarketSegmentId(AppConstants.BSE_EQ);
				} else if (reqModel.getMarketSegmentId().equalsIgnoreCase(AppConstants.NFO)) {
					reqModel.setMarketSegmentId(AppConstants.NSE_FO);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
				}

				if (reqModel.getType().equalsIgnoreCase("Standalone")) {
					reqModel.setType("S");
					String profitlossurl = props.getProfitLossbaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + props.getProfitLossData();
					return analysisRestService.getProfitLossData(profitlossurl, info);
				} else if (reqModel.getType().equalsIgnoreCase("Consolidated")) {
					reqModel.setType("C");
					String profitlossurl = props.getProfitLossbaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + props.getProfitLossData();
					return analysisRestService.getProfitLossData(profitlossurl, info);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get company balance sheet data
	 * 
	 * @author SOWMIYA
	 * @updated Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getCompanyBalanceSheetData(CompanyReqModel reqModel, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(reqModel.getMarketSegmentId())
					&& StringUtil.isNotNullOrEmpty(reqModel.getType())
					&& StringUtil.isNotNullOrEmpty(reqModel.getToken())) {
				if (reqModel.getType().equalsIgnoreCase("Standalone")) {
					reqModel.setType("S");
					String sheetDataUrl = props.getSheetbaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + props.getSheetData();
					return analysisRestService.getCompanyBalanceSheetData(sheetDataUrl, info);
				} else if (reqModel.getType().equalsIgnoreCase("Consolidated")) {
					reqModel.setType("C");
					String sheetDataUrl = props.getSheetbaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + props.getSheetData();
					return analysisRestService.getCompanyBalanceSheetData(sheetDataUrl, info);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get company result data
	 * 
	 * @author SOWMIYA
	 * @updated Gowthaman M
	 * @param reqModel
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyResultData(CompanyReqModel reqModel, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(reqModel.getMarketSegmentId())
					&& StringUtil.isNotNullOrEmpty(reqModel.getType())
					&& StringUtil.isNotNullOrEmpty(reqModel.getToken())
					&& StringUtil.isNotNullOrEmpty(reqModel.getPeriods())) {
				if (reqModel.getType().equalsIgnoreCase("Standalone")) {
					reqModel.setType("S");
					String companyUrl = props.getCompanyDataBaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + "/" + reqModel.getPeriods()
							+ props.getCompanyData();
					return analysisRestService.getCompanyResultData(companyUrl, info);
				} else if (reqModel.getType().equalsIgnoreCase("Consolidated")) {
					reqModel.setType("C");
					String companyUrl = props.getCompanyDataBaseUrl() + reqModel.getMarketSegmentId() + "/"
							+ reqModel.getToken() + "/" + reqModel.getType() + "/" + reqModel.getPeriods()
							+ props.getCompanyData();
					return analysisRestService.getCompanyResultData(companyUrl, info);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
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
		try {
			if (req.getPageSize() > 0) {
				String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
				System.out.println("timeStamp--" + timeStamp);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalTime localTime = LocalTime.parse(timeStamp, formatter);
				long currentMilliseconds = localTime.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
				System.out.println("millisecondss--" + currentMilliseconds);
				if (currentMilliseconds >= AppConstants.TWELVE_MILISEC
						&& currentMilliseconds < AppConstants.NINE_FIFTEN_MILISEC) {
					req.setSecName("pre-session");
				} else if (currentMilliseconds >= AppConstants.NINE_FIFTEN_MILISEC
						&& currentMilliseconds < AppConstants.FIFTEN_THIRTY_MILISEC) {
					req.setSecName("mid-session");
				} else if (currentMilliseconds >= AppConstants.FIFTEN_THIRTY_MILISEC
						&& currentMilliseconds < AppConstants.TWENTY_THREE_FIFTY_NINE_MILISEC) {
					req.setSecName("end-session");
				}
				return analysisRestService.getIndianMktNews(req, info);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetails(ClinetInfoModel info) {
		try {
			String userSession = AppUtil.getUserSession(info.getUserId().toUpperCase());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkMwMDAwOCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiOTc2ODY0MTlmOWNlMzc0MSIsIm9jVG9rZW4iOiIweDAxOTcxRTI3RDRERDY0NjU2OTYwQTJDODgyMTA1MiIsInVzZXJDb2RlIjoiQUNKWVUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MjgxNTM5OSwiaWF0IjoxNjkyODA0NTgzfQ.mMQ8EDiNYjk8n9MkoQo-gGHD1I8c9aAbv253DqHoqls";
			System.out.println("getIndexDetails userSession -- " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return analysisRestService.getIndexDetails(userSession, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetailsAll(ClinetInfoModel info) {
		try {
			String userSession = AppUtil.getUserSession(info.getUserId().toUpperCase());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkMwMDAwOCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiOTc2ODY0MTlmOWNlMzc0MSIsIm9jVG9rZW4iOiIweDAxOTcxRTI3RDRERDY0NjU2OTYwQTJDODgyMTA1MiIsInVzZXJDb2RlIjoiQUNKWVUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MjgxNTM5OSwiaWF0IjoxNjkyODA0NTgzfQ.mMQ8EDiNYjk8n9MkoQo-gGHD1I8c9aAbv253DqHoqls";
			System.out.println("getIndexDetails userSession -- " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return analysisRestService.getIndexDetailsAll(userSession, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getHotPursuitData(MktNewsReq req, ClinetInfoModel info) {
		try {
			return analysisRestService.getHotPursuitData(req, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorList(SectorListReq req, ClinetInfoModel info) {
		try {
			return analysisRestService.getSectorList(req, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorWiseNewsData(SectorListReq req, ClinetInfoModel info) {
		try {
			if (StringUtil.isNullOrEmpty(req.getPageSize()) || StringUtil.isNullOrEmpty(req.getSectorCode()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			return analysisRestService.getSectorWiseNewsData(req, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getAnnoucementsData(SectorListReq req, ClinetInfoModel info) {
		try {
			return analysisRestService.getAnnoucementsData(req, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getMovingAverage(MovingAverageReq req, ClinetInfoModel info) {
		try {
			String reqToken = req.getToken();
			String reqSegment = "";
			if (req.getExchange().equalsIgnoreCase(AppConstants.NSE_EQ)) {
				reqSegment = AppConstants.NSE;
			} else if (req.getExchange().equalsIgnoreCase(AppConstants.BSE_EQ)) {
				reqSegment = AppConstants.BSE;
			}
			String token = contractEntityManger.getToken(reqToken, reqSegment);
			List<String> tokenList = new ArrayList<>();
			tokenList.add(reqToken);
			tokenList.add(token);
			List<String> exchangeList = new ArrayList<>();
			exchangeList.add(req.getExchange());
			if (req.getExchange().equalsIgnoreCase(AppConstants.NSE_EQ)) {
				exchangeList.add(AppConstants.BSE_EQ);
			} else {
				exchangeList.add(AppConstants.NSE_EQ);
			}
			CompanyFinReq companyFinReq = new CompanyFinReq();
			companyFinReq.setSegment(exchangeList);
			companyFinReq.setToken(tokenList);

			return analysisRestService.getMovingAverage(companyFinReq, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getPutCallRatio(PutCallRatioReq req, ClinetInfoModel info) {
		try {
			return analysisRestService.getPutCallRatio(req, info);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get scripwise news data
	 * 
	 * @author SOWMIYA
	 * @updated Gowthaman M
	 */
	@Override
	public RestResponse<GenericResponse> getScripwiseNewsData(ScripwiseNewsReqModel model, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(model.getExchSeg()) && model.getToken() > 0 && model.getPageable() > 0
					&& StringUtil.isNotNullOrEmpty(model.getSymbol())) {
				if (model.getExchSeg().equalsIgnoreCase("NSE")) {
					model.setExchSeg("NSE_EQ");
					return analysisRestService.getScripwiseNewsData(model, info);
				} else if (model.getExchSeg().equalsIgnoreCase("BSE")) {
					model.setExchSeg("BSE_EQ");
					return analysisRestService.getScripwiseNewsData(model, info);
				} else if (model.getExchSeg().equalsIgnoreCase("NFO")) {
					String exch = "NSE";
					String symbol = model.getSymbol();
					String groupName = "EQ";
					String token = contractEntityManger.getTokenForNFO(exch, symbol, groupName);

					if (StringUtil.isNotNullOrEmpty(token)) {
						model.setExchSeg("NSE_EQ");
						model.setToken(Integer.parseInt(token));

						return analysisRestService.getScripwiseNewsData(model, info);
					}

				} else if (model.getExchSeg().equalsIgnoreCase("BFO")) {
					String exch = model.getExchSeg();
					String symbol = model.getSymbol();
					String groupName = "EQ";
					String token = contractEntityManger.getTokenForBFO(exch, symbol, groupName);

					model.setExchSeg("BSE_FO");
					model.setToken(Integer.parseInt(token));

					return analysisRestService.getScripwiseNewsData(model, info);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to divdent data
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> getDividentData(DividentReqModel model, ClinetInfoModel info) {
		try {
			if (model.getExchSeg().equalsIgnoreCase(AppConstants.NSE_EQ)
					|| model.getExchSeg().equalsIgnoreCase(AppConstants.BSE_EQ) && model.getToken() > 0) {

				Date currentDate = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				calendar.add(Calendar.MONTH, 1);
				Date futureDate = calendar.getTime();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'z'");
				String formattedFromDate = formatter.format(currentDate);
				String formattedToDate = formatter.format(futureDate);
				return analysisRestService.getDividentData(model, formattedFromDate, formattedToDate, info);

			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get health total score
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> getHealthTotalScore(HealthReqModel model, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(model.getExchSeg()) && model.getToken() > 0) {
				if (model.getExchSeg().equalsIgnoreCase(AppConstants.NSE)) {
					model.setExchSeg(AppConstants.NSE_EQ);
				} else if (model.getExchSeg().equalsIgnoreCase(AppConstants.BSE)) {
					model.setExchSeg(AppConstants.BSE_EQ);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
				}
				return analysisRestService.getHealthTotalScore(model, info);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load event data
	 * 
	 * @author GOWTHAMAN
	 */
	@Override
	public RestResponse<GenericResponse> loadEventData() {
		try {
			List<AnnoucementsDataEntity> datas = annoucementsDataRepository.findAll();

			if (StringUtil.isListNotNullOrEmpty(datas)) {
				for (AnnoucementsDataEntity data : datas) {
					EventDataModel model = new EventDataModel();
					model.setBseCode(data.getBseCode());
					model.setCaption(data.getCaption());
					model.setCompanyCode(data.getCompanyCode());
					model.setCompanyLongName(data.getCompanyLongName());
					model.setDate(data.getDate());
					model.setExch(data.getExch());
					model.setMemo(data.getMemo());
					model.setMktSegmentIdBSE(data.getMktSegmentIdBSE());
					model.setMktSegmentIdNSE(data.getMktSegmentIdNSE());
					model.setOdincodeBSE(data.getOdincodeBSE());
					model.setOdincodeNSE(data.getMktSegmentIdNSE());
					model.setSeriesBSE(data.getOdincodeBSE());
					model.setSeriesNSE(data.getOdincodeNSE());
					model.setSymbol(data.getSymbol());
					String eventKey = data.getExch() + "_" + data.getCompanyCode();
					HazelcastConfig.getInstance().getEventData().put(eventKey, model);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get Exchange Message
	 * 
	 * @author LOKESH
	 * @updated
	 */
	@Override
	public RestResponse<GenericResponse> exchangeMessage(ExchangeMsgRequest model, ClinetInfoModel info) {
		try {
			String session = AppUtil.getUserSession(info.getUserId());
//			String session = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNDAxNCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiNWQzMzYxNWI0ZWJkNjIzZSIsIm9jVG9rZW4iOiIweDAxNkNDQzEzNjgyMEFDQjEwQTlEMDkzRjk1MjA2QiIsInVzZXJDb2RlIjoiQUVYVUUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MDQ4MjU5OSwiaWF0IjoxNjkwNDQzMzExfQ.xBPozjNsQM12essyG1y3G-lqUxsMbJ6UOdDQAvwPJeo";
			if (StringUtil.isNullOrEmpty(session))
				return prepareResponse.prepareUnauthorizedResponse();

			if (StringUtil.isNullOrEmpty(model.getExchange()) && StringUtil.isNullOrEmpty(model.getPageSize())
					&& StringUtil.isNullOrEmpty(model.getNoOfHrs()) && StringUtil.isNullOrEmpty(model.getPageNo()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			if (model.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				model.setExchange(AppConstants.NSE_EQ);
				return analysisRestService.getExchangeMsgRequest(model, info, session);
			} else if (model.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				model.setExchange(AppConstants.BSE_EQ);
				return analysisRestService.getExchangeMsgRequest(model, info, session);
			} else if (model.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				model.setExchange(AppConstants.NSE_FO);
				return analysisRestService.getExchangeMsgRequest(model, info, session);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_SEGMENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get Broker Message
	 * 
	 * @author LOKESH
	 * @updated
	 */
	@Override
	public RestResponse<GenericResponse> getBrokerMessage(BrokerMsgRequest model, ClinetInfoModel info) {
		try {
			String session = AppUtil.getUserSession(info.getUserId());
//			String session = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNDAxNCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiNWQzMzYxNWI0ZWJkNjIzZSIsIm9jVG9rZW4iOiIweDAxNkNDQzEzNjgyMEFDQjEwQTlEMDkzRjk1MjA2QiIsInVzZXJDb2RlIjoiQUVYVUUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5MDQ4MjU5OSwiaWF0IjoxNjkwNDQzMzExfQ.xBPozjNsQM12essyG1y3G-lqUxsMbJ6UOdDQAvwPJeo";
			if (StringUtil.isNullOrEmpty(session))
				return prepareResponse.prepareUnauthorizedResponse();

			if (StringUtil.isNullOrEmpty(model.getPageSize()) && StringUtil.isNullOrEmpty(model.getPageNo()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			return analysisRestService.getBrokerMessage(model, info, session);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Support And Resistance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSupportAndResistance(SupportAndResistanceReq req, ClinetInfoModel info) {
		try {
//			String userSession = AppUtil.getUserSession(info.getUserId());
//			if (StringUtil.isNullOrEmpty(userSession))
//				return prepareResponse.prepareUnauthorizedResponse();

			return analysisRestService.getSupportAndResistance(req, info);
		} catch (Exception e) {
			Log.error("getSupportAndResistance -- " + e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

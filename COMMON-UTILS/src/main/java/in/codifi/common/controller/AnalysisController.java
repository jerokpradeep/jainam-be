package in.codifi.common.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.common.config.RestServiceProperties;
import in.codifi.common.controller.spec.AnalysisControllerSpec;
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
import in.codifi.common.service.PrepareResponse;
import in.codifi.common.service.spec.AnalysisServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.AppUtil;
import in.codifi.common.utility.StringUtil;
import in.codifi.common.ws.model.DividentReqModel;
import in.codifi.common.ws.model.HealthReqModel;
import io.quarkus.logging.Log;

@Path("/analysis")
public class AnalysisController implements AnalysisControllerSpec {

	@Inject
	AnalysisServiceSpec analysisService;
	@Inject
	RestServiceProperties props;
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * Method to get FII, DII, MF Activity Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getActivityData() {
		return analysisService.getActivityData();
	}

	/**
	 * Method to get World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWorldIndicesData() {
		return analysisService.getWorldIndicesData();
	}

	/**
	 * Method to get World indices Data All
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getWorldIndicesDataAll() {
		return analysisService.getWorldIndicesDataAll();
	}

	/**
	 * Method to get Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getCompanyFinancialRatioData(CFRatioReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getCompanyFinancialRatioData(req, info);
	}

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getQuarterlyTrend(QuarterlyTrendReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getQuarterlyTrend(req, info);
	}

	/**
	 * Method to get Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getShareHoldingData(CFRatioReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getShareHoldingData(req, info);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTopGainers() {
		return analysisService.getTopGainers();
	}

	/**
	 * Method to get top Losers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTopLosers() {
		return analysisService.getTopLosers();
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> get52WeekHigh() {
		String url = props.getFiftyTwoWeekHigh();
		return analysisService.getFiftyTwoWeekHigh(url);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> get52WeekLow() {
		String url = props.getFiftyTwoWeekLow();
		return analysisService.getFiftyTwoWeekLow(url);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getRiders() {
		String url = props.getRiders();
		return analysisService.getAnalysisData(url);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDraggers() {
		String url = props.getDraggers();
		return analysisService.getAnalysisData(url);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTopVolume() {
		String url = props.getTopVolume();
		return analysisService.getAnalysisData(url);
	}

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getMeanReversion() {
		String url = props.getMeanreversion();
		return analysisService.getAnalysisData(url);
	}

	/**
	 * method to get profit and loss
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getProfitLossData(ProfitLossReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getProfitLossData(reqModel, info);

	}

	/**
	 * method to get company balance sheet data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyBalanceSheetData(CompanyReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getCompanyBalanceSheetData(reqModel, info);
	}

	/**
	 * method to get company result data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getCompanyResultData(CompanyReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getCompanyResultData(reqModel, info);
	}

	/**
	 * Method to get Indian Market News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndianMktNews(MktNewsReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getIndianMktNews(req, info);
	}

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetails() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return analysisService.getIndexDetails(info);
	}

	/**
	 * Method to get Index Details All
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getIndexDetailsAll() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return analysisService.getIndexDetailsAll(info);
	}

	/**
	 * Method to get Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getHotPursuitData(MktNewsReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getHotPursuitData(req, info);
	}

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorList(SectorListReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getSectorList(req, info);
	}

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getSectorWiseNewsData(SectorListReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getSectorWiseNewsData(req, info);
	}

	/**
	 * Method to get AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getAnnoucementsData(SectorListReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getAnnoucementsData(req, info);
	}

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getMovingAverage(MovingAverageReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		if (req.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
			req.setExchange(AppConstants.NSE_EQ);
		} else if (req.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
			req.setExchange(AppConstants.BSE_EQ);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
		}

		return analysisService.getMovingAverage(req, info);
	}

	/**
	 * Method to get Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getPutCallRatio(PutCallRatioReq req) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return analysisService.getPutCallRatio(req, info);
	}

	/**
	 * method to get scripwise news data
	 * 
	 * @author SOWMIYA
	 */
	public RestResponse<GenericResponse> getScripwiseNewsData(ScripwiseNewsReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getScripwiseNewsData(model, info);
	}

	/**
	 * method to get divident data
	 * 
	 * @author SOWMIYA
	 */
	public RestResponse<GenericResponse> getDividentData(DividentReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
		return analysisService.getDividentData(model, info);
	}

	/**
	 * method to get health total score
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> getHealthTotalScore(HealthReqModel model) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return analysisService.getHealthTotalScore(model, info);
	}

	/**
	 * method to load Event Data
	 * 
	 * @author Gowthaman M
	 * @param model
	 * @return
	 */
	public RestResponse<GenericResponse> loadEventData() {
		return analysisService.loadEventData();
	}

	/**
	 * method to get Exchange Message
	 * 
	 * @author LOKESH
	 * @param
	 * @return
	 */
	public RestResponse<GenericResponse> exchangeMessage(ExchangeMsgRequest model) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("df");
		return analysisService.exchangeMessage(model, info);
	}

	/**
	 * method to get Broker Message
	 * 
	 * @author LOKESH
	 * @param
	 * @return
	 */
	public RestResponse<GenericResponse> getBrokerMessage(BrokerMsgRequest model) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return analysisService.getBrokerMessage(model, info);
	}

	/**
	 * Method to get Support And Resistance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	public RestResponse<GenericResponse> getSupportAndResistance(SupportAndResistanceReq req) {
		String exch = "";
		if (StringUtil.isNullOrEmpty(req.getExch()) || StringUtil.isNullOrEmpty(req.getToken()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		if (req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
			exch = AppConstants.NSE;
		} else if (req.getExch().equalsIgnoreCase(AppConstants.BSE)) {
			exch = AppConstants.BSE;
		} else if (req.getExch().equalsIgnoreCase(AppConstants.NFO)) {
			exch = AppConstants.NFO;
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
		}

//		ClinetInfoModel info = appUtil.getClientInfo();
		ClinetInfoModel info = new ClinetInfoModel();
		info.setUserId("C00008");
		return analysisService.getSupportAndResistance(req, info);
	}

}

package in.codifi.common.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
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
import in.codifi.common.ws.model.DividentReqModel;
import in.codifi.common.ws.model.HealthReqModel;

public interface AnalysisServiceSpec {

	/**
	 * Method to get FII, DII, MF Activity Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getActivityData();

	/**
	 * Method to get World indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getWorldIndicesData();
	
	/**
	 * Method to get World indices Data All
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getWorldIndicesDataAll();

	/**
	 * Method to get Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getCompanyFinancialRatioData(CFRatioReq req, ClinetInfoModel info);

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getQuarterlyTrend(QuarterlyTrendReq req, ClinetInfoModel info);

	/**
	 * Method to get Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getShareHoldingData(CFRatioReq req, ClinetInfoModel info);

	/**
	 * method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getTopGainers();

	/**
	 * method to get top losers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getTopLosers();

	/**
	 * method to get top losers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getFiftyTwoWeekHigh(String url);

	/**
	 * method to get fifty two week low
	 * 
	 * @author sowmiya
	 */
	RestResponse<GenericResponse> getFiftyTwoWeekLow(String url);

	/**
	 * method to get analysis data
	 * 
	 * @author sowmiya
	 */
	RestResponse<GenericResponse> getAnalysisData(String url);

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 */
	RestResponse<GenericResponse> getProfitLossData(ProfitLossReqModel reqModel, ClinetInfoModel info);

	/**
	 * method to get company balance sheet data
	 * 
	 * @author SOWMIYA
	 */
	RestResponse<GenericResponse> getCompanyBalanceSheetData(CompanyReqModel reqModel, ClinetInfoModel info);

	/**
	 * method to get company result data
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	RestResponse<GenericResponse> getCompanyResultData(CompanyReqModel reqModel, ClinetInfoModel info);

	/**
	 * Method to get Indian Market News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getIndianMktNews(MktNewsReq req, ClinetInfoModel info);

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getIndexDetails(ClinetInfoModel info);
	
	/**
	 * Method to get Index Details All
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getIndexDetailsAll(ClinetInfoModel info);

	/**
	 * Method to get Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getHotPursuitData(MktNewsReq req, ClinetInfoModel info);

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getSectorList(SectorListReq req, ClinetInfoModel info);

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getSectorWiseNewsData(SectorListReq req, ClinetInfoModel info);

	/**
	 * Method to get AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getAnnoucementsData(SectorListReq req, ClinetInfoModel info);

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getMovingAverage(MovingAverageReq req, ClinetInfoModel info);

	/**
	 * Method to get Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getPutCallRatio(PutCallRatioReq req, ClinetInfoModel info);

	/**
	 * method to get scripwise news data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> getScripwiseNewsData(ScripwiseNewsReqModel model, ClinetInfoModel info);

	/**
	 * method to get divident data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> getDividentData(DividentReqModel model, ClinetInfoModel info);

	/**
	 * method to get health total score
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> getHealthTotalScore(HealthReqModel model, ClinetInfoModel info);

	/**
	 * method to load Event Data
	 * 
	 * @author Gowthaman M
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> loadEventData();

	/**
	 * method to get Exchange Message
	 * 
	 * @authorLOKESH
	 * @param
	 * @return
	 */
	RestResponse<GenericResponse> exchangeMessage(ExchangeMsgRequest model, ClinetInfoModel info);

	/**
	 * method to get Broker Message
	 * 
	 * @authorLOKESH
	 * @param
	 * @return
	 */
	RestResponse<GenericResponse> getBrokerMessage(BrokerMsgRequest model, ClinetInfoModel info);

	/**
	 * Method to get Support And Resistance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getSupportAndResistance(SupportAndResistanceReq req, ClinetInfoModel info);

}

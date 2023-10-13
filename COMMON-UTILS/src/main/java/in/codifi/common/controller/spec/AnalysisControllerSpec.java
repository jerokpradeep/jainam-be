package in.codifi.common.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

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

public interface AnalysisControllerSpec {

	/**
	 * Method to get FII, DII, MF Activity Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getActivityData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getActivityData();

	/**
	 * Method to get World Indices Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getWorldIndicesData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getWorldIndicesData();

	/**
	 * Method to get World Indices Data All
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getWorldIndicesDataAll")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getWorldIndicesDataAll();

	/**
	 * Method to get Company Financial Ratio Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getCompanyFinancialRatioData")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getCompanyFinancialRatioData(CFRatioReq req);

	/**
	 * Method to get Quarterly Trend
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getQuarterlyTrend")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getQuarterlyTrend(QuarterlyTrendReq req);

	/**
	 * Method to get Share Holding Pattern Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getShareHoldingPatternData")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getShareHoldingData(CFRatioReq req);

	/**
	 * Method to get top gainers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/topgainers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getTopGainers();

	/**
	 * Method to get top losers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/toplosers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getTopLosers();

	/**
	 * Method to get 52 week high details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/52weekhigh")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> get52WeekHigh();

	/**
	 * Method to get 52 week low details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/52weeklow")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> get52WeekLow();

	/**
	 * Method to get riders details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/riders")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getRiders();

	/**
	 * Method to get draggers details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/draggers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getDraggers();

	/**
	 * Method to get top volume details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/topvolume")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getTopVolume();

	/**
	 * Method to get mean reversion details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/meanreversion")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getMeanReversion();

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	@Path("/get/profitloss")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getProfitLossData(ProfitLossReqModel reqModel);

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	@Path("/get/company/balancesheet")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getCompanyBalanceSheetData(CompanyReqModel reqModel);

	/**
	 * method to get profit and loss data
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	@Path("/get/company/result")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getCompanyResultData(CompanyReqModel reqModel);

	/**
	 * Method to get Indian Market News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/get/indianMktNews")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getIndianMktNews(MktNewsReq req);

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/get/indexDetails")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getIndexDetails();

	/**
	 * Method to get Index Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/get/indexDetailsAll")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getIndexDetailsAll();

	/**
	 * Method to get Hot Pursuit Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getHotPursuitData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getHotPursuitData(MktNewsReq req);

	/**
	 * Method to get Sector List
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getSectorList")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSectorList(SectorListReq req);

	/**
	 * Method to get Sector Wise News Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getSectorWiseNewsData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSectorWiseNewsData(SectorListReq req);

	/**
	 * Method to get AnnoucementsData
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getAnnoucementsData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getAnnoucementsData(SectorListReq req);

	/**
	 * Method to get Moving Average
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getMovingAverage")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getMovingAverage(MovingAverageReq req);

	/**
	 * Method to get Put Call Ratio
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getPutCallRatio")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getPutCallRatio(PutCallRatioReq req);

	/**
	 * method to get scripwise news data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	@Path("/get/scripwisenewsdata")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getScripwiseNewsData(ScripwiseNewsReqModel model);

	/**
	 * method to get divident data
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	@Path("/get/dividentdata")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getDividentData(DividentReqModel model);

	/**
	 * method to get health total score
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	@Path("/get/healthtotalscore")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getHealthTotalScore(HealthReqModel model);

	/**
	 * method to load Event Data
	 * 
	 * @author Gowthaman M
	 * @param model
	 * @return
	 */
	@Path("/loadEventData")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> loadEventData();

	/**
	 * method to get Exchange Message
	 * 
	 * @author LOKESH
	 * @param
	 * @return
	 */
	@Path("/exchangeMsgData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> exchangeMessage(ExchangeMsgRequest model);

	/**
	 * method to get Broker Message
	 * 
	 * @author LOKESH
	 * @param
	 * @return
	 */
	@Path("/brokerMsgData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getBrokerMessage(BrokerMsgRequest model);

	/**
	 * Method to get Support And Resistance
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getsupportAndResistance")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSupportAndResistance(SupportAndResistanceReq req);

}

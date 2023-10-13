package in.codifi.funds.ws.remodeling;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.funds.model.response.LimitsResponseModel;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.RestLimitsResp;
import in.codifi.funds.ws.model.Result;
import io.quarkus.logging.Log;

@ApplicationScoped
public class LimitsRemodeling {

	/**
	 * Bind data for limits response
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param model
	 * @return
	 */
//	public LimitsResponseModel bindLimitsResponse(RestLimitsResp model) {
//		LimitsResponseModel respone = new LimitsResponseModel();
//
//		try {
//			float openingBalance = StringUtil.isNotNullOrEmpty(model.getCash()) ? Float.parseFloat(model.getCash()) : 0;
//			float payin = StringUtil.isNotNullOrEmpty(model.getPayin()) ? Float.parseFloat(model.getPayin()) : 0;
//			float payout = StringUtil.isNotNullOrEmpty(model.getPayout()) ? Float.parseFloat(model.getPayout()) : 0;
//			float unclearedCash = StringUtil.isNotNullOrEmpty(model.getUnclearedcash())
//					? Float.parseFloat(model.getUnclearedcash())
//					: 0;
//			float marginUsed = StringUtil.isNotNullOrEmpty(model.getMarginUsed())
//					? Float.parseFloat(model.getMarginUsed())
//					: 0;
//			float holdingSellCredit = StringUtil.isNotNullOrEmpty(model.getCacSellCredits())
//					? Float.parseFloat(model.getCacSellCredits())
//					: 0;
//			float brokerage = StringUtil.isNotNullOrEmpty(model.getBrokerage()) ? Float.parseFloat(model.getBrokerage())
//					: 0;
//			float stockPledge = StringUtil.isNotNullOrEmpty(model.getCollateral())
//					? Float.parseFloat(model.getCollateral())
//					: 0;
//
//			float respPayin = payin + unclearedCash;
//			float availableMarigin = (openingBalance + payin + unclearedCash + stockPledge + holdingSellCredit)
//					- (marginUsed + payout);
//			float span = StringUtil.isNotNullOrEmpty(model.getSpan()) ? Float.parseFloat(model.getSpan()) : 0;
//			float exposure = StringUtil.isNotNullOrEmpty(model.getExpo()) ? Float.parseFloat(model.getExpo()) : 0;
//			float premium = StringUtil.isNotNullOrEmpty(model.getPremium()) ? Float.parseFloat(model.getPremium()) : 0;
//
//			respone.setOpeningBalance(openingBalance);
//			respone.setPayin(respPayin);
//			respone.setMarginUsed(marginUsed);
//			respone.setHoldingSellCredit(holdingSellCredit);
//			respone.setBrokerage(brokerage);
//			respone.setAvailableMargin(availableMarigin);
//			respone.setStockPledge(stockPledge);
//			respone.setSpan(span);
//			respone.setExposure(exposure);
//			respone.setPremium(premium);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.error(e.getMessage());
//		}
//		return respone;
//	}

	/**
	 * Bind data for limits response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	public LimitsResponseModel bindLimits(List<List<Result>> limitRespData) {
		LimitsResponseModel respone = new LimitsResponseModel();

		try {

//			int product = limitRespData.getProduct();
			int product = limitRespData.get(1).get(0).getProduct();
			float deposit = limitRespData.get(1).get(0).getDeposit();
			float fundsTransferredToday = limitRespData.get(1).get(0).getFundsTransferredToday();
			float collateral = limitRespData.get(1).get(0).getCollateral();
			float creditForSale = limitRespData.get(1).get(0).getCreditForSale();
			float optionCFS = limitRespData.get(1).get(0).getOptionCFS();
			float totalTradingPowerLimit = limitRespData.get(1).get(0).getTotalTradingPowerLimit();
			float limitUtilization = limitRespData.get(1).get(0).getLimitUtilization();
			float bookedPAndL = limitRespData.get(1).get(0).getBookedPAndL();
			float mtmPAndL = limitRespData.get(1).get(0).getMtmPAndL();
			float totalUtilization = limitRespData.get(1).get(0).getTotalUtilization();
			float netAvailableFunds = limitRespData.get(1).get(0).getNetAvailableFunds();
			float forAllocationWithdrawal = limitRespData.get(1).get(0).getForAllocationWithdrawal();
			float exposureMargin = limitRespData.get(1).get(0).getExposureMargin();
			float cashDeposit = limitRespData.get(1).get(0).getCashDeposit();
			float adhocDeposit = limitRespData.get(1).get(0).getAdhocDeposit();

			float openingBalance = 0;
			float payin = limitRespData.get(1).get(0).getCashDeposit();
			float payout = 0;
			float unclearedCash = 0;
			float marginUsed = limitRespData.get(1).get(0).getExposureMargin();
			float holdingSellCredit = 0;
			float brokerage = 0;
			float stockPledge = 0;
			float respPayin = payin + unclearedCash;
			float availableMarigin = (openingBalance + payin + unclearedCash + stockPledge + holdingSellCredit)
					- (marginUsed + payout);
			float span = 0;
			float exposure = limitRespData.get(1).get(0).getExposureMargin();
			float premium = 0;

			respone.setOpeningBalance(cashDeposit);
			respone.setPayin(fundsTransferredToday);
			respone.setMarginUsed(totalUtilization);
			respone.setHoldingSellCredit(creditForSale);
//			respone.setBrokerage(brokerage);
			respone.setAvailableMargin(netAvailableFunds);
			respone.setStockPledge(collateral);
//			respone.setSpan(span);
			respone.setExposure(exposure);
			respone.setPremium(optionCFS);
			respone.setBookedPAndL(bookedPAndL);
			respone.setMtmPAndL(mtmPAndL);
			
			respone.setCollateral(collateral);
			respone.setFundsTranstoday(fundsTransferredToday);
			respone.setCreditForSale(creditForSale);
			respone.setTotalUtilize(totalUtilization);
			respone.setAllocationOrWithdrawal(forAllocationWithdrawal);
			respone.setNetAvailableFunds(netAvailableFunds);
			
			
		} catch (Exception e) {
			Log.error(e);
		}
		return respone;
	}

}

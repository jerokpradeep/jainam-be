package in.codifi.scrips.ws.remodeling;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.scrips.model.transformation.SecurityInfoRespModel;
import in.codifi.scrips.ws.model.PivotModel;
import in.codifi.scrips.ws.model.ResultModel;
import in.codifi.scrips.ws.model.SecurityInfoModel;
import in.codifi.scrips.ws.model.SecurityInfoRestSuccRespModel;
import in.codifi.scrips.ws.model.SecurityInfoRestSuccessRespModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SecurityInfoRemodeling {

	/*
	 * method to bind get security information
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	public SecurityInfoRespModel bindSecurityInfoData(SecurityInfoRestSuccRespModel respModel) {
		SecurityInfoRespModel response = new SecurityInfoRespModel();
		try {
			response.setCompanyName(respModel.getCname());
			response.setDeliveryUnits(respModel.getDelunt());
			response.setExchange(respModel.getExch());
			response.setInstrumentName(respModel.getInstname());
			response.setIsin(respModel.getIsin());
			response.setLotSize(respModel.getLs());
			response.setMultiplier(respModel.getMult());
			response.setPrcftr_d(respModel.getPrcftr_d());
			response.setPricePrecision(respModel.getPp());
			response.setSegment(respModel.getSeg());
			response.setSymbolName(respModel.getSymname());
			response.setTickSize(respModel.getTi());
			response.setToken(respModel.getToken());
			response.setTradeUnits(respModel.getTrdunt());
			response.setTradingSymbol(respModel.getTsym());
			response.setVarMargin(respModel.getVarmrg());
			response.setAdditionalLongMargin(respModel.getAddbmrg());
			response.setAdditionalShortMargin(respModel.getAddsmrg());
			response.setDeliveryMargin(respModel.getDelmrg());
			response.setDname(respModel.getDname());
			response.setDeliveryUnits(respModel.getDelunt());
			response.setElmBuyMargin(respModel.getElmbmrg());
			response.setElmSellMargin(respModel.getElmsmrg());
			response.setElmMargin(respModel.getElmmrg());
			response.setExerciseEndDate(respModel.getExeendd());
			response.setExerciseStartDate(respModel.getExestrd());
			response.setExposureMargin(respModel.getExpmrg());
			response.setFreezeQty(respModel.getFrzqty());
			response.setGp_nd(respModel.getGp_nd());
			response.setIssuedate(respModel.getIssue_d());
			response.setLastTradingDate(respModel.getLast_trd_d());
			response.setListingDate(respModel.getListing_d());
			response.setMarkettype(respModel.getMkt_t());
			response.setNontradableinstruments(respModel.getNontrd());
			response.setOptionType(respModel.getOptt());
			response.setTenderStartDate(respModel.getTenstrd());
			response.setTenderEndEate(respModel.getTenendd());
			response.setTenderMargin(respModel.getTenmrg());
			response.setStrikePrice(respModel.getStrprc());
			response.setSpecialLongMargin(respModel.getSplbmrg());
			response.setSpecialShortMargin(respModel.getSplsmrg());
			response.setExpiry(respModel.getExd());
		} catch (Exception e) {
			Log.error(e);
		}

		return response;
	}

	/**
	 * method to bind security info
	 * 
	 * @author SOWMIYA
	 * @param respModel
	 * @return
	 */
	public SecurityInfoRespModel bindSecurityInfo(SecurityInfoRestSuccessRespModel respModel) {
		SecurityInfoRespModel response = new SecurityInfoRespModel();
		try {
			for (SecurityInfoModel model : respModel.getResult().getSecInfo()) {
				response.setCompanyName(model.getSSecurityDesc().trim());
				response.setDeliveryUnits(Integer.toString(model.getNNoDeliveryStartDate()).trim());
				response.setExchange(Integer.toString(model.getNExDate()).trim());
				response.setInstrumentName(model.getNInstrumentType().trim());
				response.setIsin(model.getSISINCode().trim());
				response.setLotSize(Integer.toString(model.getNRegularLot()).trim());
				response.setMultiplier(Integer.toString(model.getNMarginMultiplier()).trim());
				response.setPrcftr_d(Integer.toString(model.getNPriceTick()).trim());
				response.setPricePrecision(Integer.toString(model.getNPriceQuotFactor()).trim());
				response.setSegment(
						Integer.toString(respModel.getResult().getPivot().get(0).getNMarketSegmentId()).trim());
				response.setSymbolName(model.getSSymbol().trim());
				response.setTickSize(Integer.toString(model.getNPriceTick()).trim());
				response.setToken(Integer.toString(model.getNToken()).trim());
				response.setTradeUnits(Integer.toString(respModel.getResult().getSecInfo().get(0).getNToken()).trim());
				response.setTradingSymbol(model.getSSymbol().trim());
				response.setVarMargin(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setAdditionalLongMargin(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setAdditionalShortMargin(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setDeliveryMargin(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setDname(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setDeliveryUnits(Integer.toString(model.getNNoDeliveryStartDate()).trim());
				response.setElmBuyMargin(Integer.toString(model.getNAVMBuyMargin()).trim());
				response.setElmSellMargin(Integer.toString(model.getNAVMSellMargin()).trim());
				response.setElmMargin(Integer.toString(model.getNAVMSellMargin()).trim());
				response.setExerciseEndDate(Integer.toString(model.getNExDate()).trim());
				response.setExerciseStartDate(Integer.toString(model.getNExDate()).trim());
				response.setExposureMargin(Integer.toString(model.getNAVMSellMargin()).trim());
				response.setFreezeQty(Integer.toString(model.getNPriceQuotFactor()).trim());
				response.setGp_nd(Integer.toString(model.getNAVMSellMargin()).trim());
				response.setIssuedate(Integer.toString(model.getNExDate()).trim());
				response.setLastTradingDate(Integer.toString(model.getNNoDeliveryStartDate()).trim());
				response.setListingDate(Integer.toString(model.getNBookClosureEndDate()).trim());
				response.setMarkettype(Integer.toString(model.getMathcingType()).trim());
				response.setNontradableinstruments(model.getNInstrumentType().trim());
				response.setOptionType(Integer.toString(model.getMathcingType()).trim());
				response.setTenderStartDate(Integer.toString(model.getNBookClosureStartDate()).trim());
				response.setTenderEndEate(Integer.toString(model.getNBookClosureStartDate()).trim());
				response.setTenderMargin(Integer.toString(model.getNMarginMultiplier()).trim());
				response.setStrikePrice(Integer.toString(model.getNPriceTick()).trim());
				response.setSpecialLongMargin(Integer.toString(model.getNMarginMultiplier()).trim());
				response.setSpecialShortMargin(Integer.toString(model.getNMarginMultiplier()).trim());
				response.setExpiry(Integer.toString(model.getNExDate()).trim());
			}
		} catch (Exception e) {
			Log.error(e);
		}

		return response;
	}

	/**
	 * Method to bind SecurityInfo
	 * 
	 * @author Gowthaman
	 * @param securityInfoModel
	 * @return
	 */
	public SecurityInfoRestSuccessRespModel bindExtractSecurityInfo(
			SecurityInfoRestSuccessRespModel securityInfoModel) {
		SecurityInfoRestSuccessRespModel response = new SecurityInfoRestSuccessRespModel();
		ResultModel resultModel = new ResultModel();
		List<SecurityInfoModel> infoModelList = new ArrayList<>();
		List<PivotModel> pivotModelList = new ArrayList<>();
		try {
			for (SecurityInfoModel infoModel : securityInfoModel.getResult().getSecInfo()) {
				SecurityInfoModel infoModelResp = new SecurityInfoModel();

				if (infoModel.getCouponFrequency() != null) {
					infoModelResp.setCouponFrequency(infoModel.getCouponFrequency().trim());
				} else {
					infoModelResp.setCouponFrequency(infoModel.getCouponFrequency());
				}
				infoModelResp.setCouponRate(infoModel.getCouponRate());
				if (infoModel.getCreditRating() != null) {
					infoModelResp.setCreditRating(infoModel.getCreditRating().trim());
				} else {
					infoModelResp.setCreditRating(infoModel.getCreditRating());
				}
				if (infoModel.getDaysInMonth() != null) {
					infoModelResp.setDaysInMonth(infoModel.getDaysInMonth().trim());
				} else {
					infoModelResp.setDaysInMonth(infoModel.getDaysInMonth());
				}
				infoModelResp.setDaysInYear(infoModel.getDaysInYear());
				infoModelResp.setInstrumentCode(infoModel.getInstrumentCode());
				if (infoModel.getLastInterestPaymentDate() != null) {
					infoModelResp.setLastInterestPaymentDate(infoModel.getLastInterestPaymentDate().trim());
				} else {
					infoModelResp.setLastInterestPaymentDate(infoModel.getLastInterestPaymentDate());
				}
				infoModelResp.setMathcingType(infoModel.getMathcingType());
				if (infoModel.getMaturityDate() != null) {
					infoModelResp.setMaturityDate(infoModel.getMaturityDate().trim());
				} else {
					infoModelResp.setMaturityDate(infoModel.getMaturityDate());
				}
				infoModelResp.setNAVMBuyMargin(infoModel.getNAVMBuyMargin());
				infoModelResp.setNAVMSellMargin(infoModel.getNAVMSellMargin());
				infoModelResp.setNBookClosureEndDate(infoModel.getNBookClosureEndDate());
				infoModelResp.setNBookClosureStartDate(infoModel.getNBookClosureStartDate());
				infoModelResp.setNExDate(infoModel.getNExDate());
				if (infoModel.getNextInterestPaymentDate() != null) {
					infoModelResp.setNextInterestPaymentDate(infoModel.getNextInterestPaymentDate().trim());
				} else {
					infoModelResp.setNextInterestPaymentDate(infoModel.getNextInterestPaymentDate());
				}
				infoModelResp.setNFaceValue(infoModel.getNFaceValue());
				infoModelResp.setNFreezePercent(infoModel.getNFreezePercent());
				if (infoModel.getNInstrumentType() != null) {
					infoModelResp.setNInstrumentType(infoModel.getNInstrumentType().trim());
				} else {
					infoModelResp.setNInstrumentType(infoModel.getNInstrumentType());
				}
				infoModelResp.setNIntrinsicValue(infoModel.getNIntrinsicValue());
				infoModelResp.setNIssuedCapital(infoModel.getNIssuedCapital());
				infoModelResp.setNMarginMultiplier(infoModel.getNMarginMultiplier());
				infoModelResp.setNNoDeliveryEndDate(infoModel.getNNoDeliveryEndDate());
				infoModelResp.setNNoDeliveryStartDate(infoModel.getNNoDeliveryStartDate());
				if (infoModel.getNNormal_SecurityStatus() != null) {
					infoModelResp.setNNormal_SecurityStatus(infoModel.getNNormal_SecurityStatus().trim());
				} else {
					infoModelResp.setNNormal_SecurityStatus(infoModel.getNNormal_SecurityStatus());
				}
				infoModelResp.setNPriceQuotFactor(infoModel.getNPriceQuotFactor());
				infoModelResp.setNPriceTick(infoModel.getNPriceTick());
				infoModelResp.setNRecordDate(infoModel.getNRecordDate());
				infoModelResp.setNRegularLot(infoModel.getNRegularLot());
				infoModelResp.setNToken(infoModel.getNToken());
				infoModelResp.setSettlementType(infoModel.getSettlementType());
				if (infoModel.getSISINCode() != null) {
					infoModelResp.setSISINCode(infoModel.getSISINCode().trim());
				} else {
					infoModelResp.setSISINCode(infoModel.getSISINCode());
				}
				infoModelResp.setSPOS(infoModel.getSPOS());
				infoModelResp.setSPOSTYPE(infoModel.getSPOSTYPE());
				if (infoModel.getSRemarks() != null) {
					infoModelResp.setSRemarks(infoModel.getSRemarks().trim());
				} else {
					infoModelResp.setSRemarks(infoModel.getSRemarks());
				}
				if (infoModel.getSSecurityDesc() != null) {
					infoModelResp.setSSecurityDesc(infoModel.getSSecurityDesc().trim());
				} else {
					infoModelResp.setSSecurityDesc(infoModel.getSSecurityDesc());
				}
				if (infoModel.getSSeries() != null) {
					infoModelResp.setSSeries(infoModel.getSSeries().trim());
				} else {
					infoModelResp.setSSeries(infoModel.getSSeries());
				}
				if (infoModel.getSSymbol() != null) {
					infoModelResp.setSSymbol(infoModel.getSSymbol().trim());
				} else {
					infoModelResp.setSSymbol(infoModel.getSSymbol());
				}
				if (infoModel.getStatus() != null) {
					infoModelResp.setStatus(infoModel.getStatus().trim());
				} else {
					infoModelResp.setStatus(infoModel.getStatus());
				}
				if (infoModel.getValueMethod() != null) {
					infoModelResp.setValueMethod(infoModel.getValueMethod().trim());
				} else {
					infoModelResp.setValueMethod(infoModel.getValueMethod());
				}

				infoModelList.add(infoModelResp);
			}
			resultModel.setSecInfo(infoModelList);

			for (PivotModel pivotModel : securityInfoModel.getResult().getPivot()) {
				pivotModel.setNMarketSegmentId(pivotModel.getNMarketSegmentId());

				if (pivotModel.getNToken() != null) {
					pivotModel.setNToken(pivotModel.getNToken().trim());
				} else {
					pivotModel.setNToken(pivotModel.getNToken().trim());
				}
				if (pivotModel.getPrevClose() != null) {
					pivotModel.setPrevClose(pivotModel.getPrevClose().trim());
				} else {
					pivotModel.setPrevClose(pivotModel.getPrevClose());
				}
				if (pivotModel.getPrevHigh() != null) {
					pivotModel.setPrevHigh(pivotModel.getPrevHigh().trim());
				} else {
					pivotModel.setPrevHigh(pivotModel.getPrevHigh());
				}
				if (pivotModel.getPrevLow() != null) {
					pivotModel.setPrevLow(pivotModel.getPrevLow().trim());
				} else {
					pivotModel.setPrevLow(pivotModel.getPrevLow());
				}
				if (pivotModel.getPrevOpen() != null) {
					pivotModel.setPrevOpen(pivotModel.getPrevOpen().trim());
				} else {
					pivotModel.setPrevOpen(pivotModel.getPrevOpen());
				}
				if (pivotModel.getSSymbol() != null) {
					pivotModel.setSSymbol(pivotModel.getSSymbol().trim());
				} else {
					pivotModel.setSSymbol(pivotModel.getSSymbol());
				}

				pivotModelList.add(pivotModel);
			}
			resultModel.setPivot(pivotModelList);

			response.setResult(resultModel);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return response;
	}

}

package in.codifi.orders.model.transformation;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.cache.model.ContractMasterModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.model.response.PositionResponse;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.model.PositionRespModel;
import in.codifi.orders.ws.model.RestPositionSuccessResp;

@ApplicationScoped
public class PositionsRemodeling {

	/**
	 * Method to bind data with extraction layer
	 * 
	 * @author Gowthaman M
	 */

	@SuppressWarnings("unused")
	public List<PositionResponse> bindPostitionResponseData(PositionRespModel datas) {
		List<RestPositionSuccessResp> success = datas.getData();
		List<PositionResponse> responseList = new ArrayList<>();
		try {
			for (RestPositionSuccessResp model : success) {
				PositionResponse response = new PositionResponse();

				String token = model.getPrefScripToken();
				String restExch = model.getPrefExch();
				String pdc = "0";
				String segment = "";
				String formattedInsName = "";
				String exch = "";

				if (restExch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
					exch = AppConstants.NSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
					exch = AppConstants.BSE;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_FO)) {
					exch = AppConstants.NFO;
				} else if (restExch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
					exch = AppConstants.CDS;
				}

				ContractMasterModel contractMasterModel = AppUtil.getContractMaster(exch, token);
				if (contractMasterModel != null) {
					pdc = StringUtil.isNotNullOrEmpty(contractMasterModel.getPdc()) ? contractMasterModel.getPdc()
							: "0";
					segment = StringUtil.isNotNullOrEmpty(contractMasterModel.getSegment())
							? contractMasterModel.getSegment()
							: "";
					formattedInsName = StringUtil.isNotNullOrEmpty(contractMasterModel.getFormattedInsName())
							? contractMasterModel.getFormattedInsName()
							: "";
				}

				int netQty = model.getNetQty();

				int lotSize = model.getMktLot();

				float netAvgPrice = StringUtil.isNotNullOrEmpty(model.getNetPrice())
						? Float.parseFloat(model.getNetPrice())
						: 0;
				float buyAvgPrice = StringUtil.isNotNullOrEmpty(model.getAvgBuyPrice())
						? Float.parseFloat(model.getAvgBuyPrice())
						: 0;
				float sellAvgPrice = StringUtil.isNotNullOrEmpty(model.getAvgSellPrice())
						? Float.parseFloat(model.getAvgSellPrice())
						: 0;
				float realizedpnl =  0; //TODO
				
				
				float multiplier = StringUtil.isNotNullOrEmpty(model.getMultiplier()) ? Float.parseFloat(model.getMultiplier()) : 0;

				float breakevenPrice = 0; //TODO

//				float netUploadedPrice = StringUtil.isNotNullOrEmpty(model.getNetupldprc())
//						? Float.parseFloat(model.getNetupldprc())
//						: 0;
//				float uploadedPrice = StringUtil.isNotNullOrEmpty(model.getUpldprc())
//						? Float.parseFloat(model.getUpldprc())
//						: 0;
				float netUploadedPrice = 0;  //TODO
				float uploadedPrice =  0;  //TODO

				/** Day Data **/
				int dayBuyQty = model.getBuyQty();
				float dayBuyAmount = StringUtil.isNotNullOrEmpty(model.getBuyValue())
						? Float.parseFloat(model.getBuyValue())
						: 0;
				float dayBuyAvgPrice = StringUtil.isNotNullOrEmpty(model.getAvgBuyPrice())
						? Float.parseFloat(model.getAvgBuyPrice())
						: 0;
				int daySellQty = model.getSellQty();
				float daySellAmount = StringUtil.isNotNullOrEmpty(model.getSellValue())
						? Float.parseFloat(model.getSellValue())
						: 0;
				float daySellAvgPrice = StringUtil.isNotNullOrEmpty(model.getAvgSellPrice())
						? Float.parseFloat(model.getAvgSellPrice())
						: 0;

				/** Carry forward Data **/
				int cfBuyQty =  0; //TODO
				float cfBuyAmount = StringUtil.isNotNullOrEmpty(model.getCfBuyValue())
						? Float.parseFloat(model.getCfBuyValue())
						: 0;
				float cfBuyAvgPrice = StringUtil.isNotNullOrEmpty(model.getCfNetPrice())
						? Float.parseFloat(model.getCfNetPrice())
						: 0;//TODO
				int cfSellQty =  0;  //TODO
				float cfSellAvgPrice = StringUtil.isNotNullOrEmpty(model.getCfNetPrice())
						? Float.parseFloat(model.getCfNetPrice())
						: 0;//TODO
				float cfSellAmount = StringUtil.isNotNullOrEmpty(model.getCfSellValue())
						? Float.parseFloat(model.getCfSellValue())
						: 0;
				float buyPrice = 0f;
				float sellPrice = 0f;
				float mtmBuyPrice = 0f;
				float mtmSellPrice = 0f;
				response.setDisplayName(formattedInsName);
				response.setTradingsymbol(model.getSymbol());
				response.setExchange(exch);

//				response.setNetAvgPrice(String.valueOf(netUploadedPrice)); //TODO
				response.setNetQty(String.valueOf(netQty));
				response.setOvernightQty(cfBuyQty > 0 ? String.valueOf(cfBuyQty) : String.valueOf(-cfSellQty));
				response.setOvernightPrice(String.valueOf(uploadedPrice));
				response.setBuyQty(String.valueOf(dayBuyQty + cfBuyQty));
				response.setSellQty(String.valueOf(daySellQty + cfSellQty));
				response.setRealizedPnl(String.valueOf(realizedpnl));
//				response.setUnrealizedPnl(model.getUrmtom()); //TODO
				response.setMultiplier(String.valueOf(multiplier));
				response.setLotsize(String.valueOf(model.getMktLot()));
//				response.setTicksize(model.getTi());//TODO
				response.setPdc(pdc);
//				response.setLtp(model.getLp());//TODO
				response.setToken(token);
				response.setBreakevenPrice(String.valueOf(breakevenPrice));
				
				response.setProduct(HazelcastConfig.getInstance().getProductTypes().get(model.getPrdType()));

				/** Buy/sell price calculation **/
				/** If exch is MCX divide the Qty by lot size **/
				if (exch.equalsIgnoreCase("MCX")) {
					cfSellQty = cfSellQty > 0 ? (cfSellQty / lotSize) : cfSellQty;
					cfBuyQty = cfBuyQty > 0 ? (cfBuyQty / lotSize) : cfBuyQty;
					dayBuyQty = dayBuyQty > 0 ? (dayBuyQty / lotSize) : dayBuyQty;
					daySellQty = daySellQty > 0 ? (daySellQty / lotSize) : daySellQty;
				}
				if (dayBuyQty > 0 || cfBuyQty > 0) {
					buyPrice = ((cfBuyQty * uploadedPrice) + (dayBuyQty * dayBuyAvgPrice)) / (cfBuyQty + dayBuyQty);
					mtmBuyPrice = ((cfBuyQty * cfBuyAvgPrice) + (dayBuyQty * dayBuyAvgPrice)) / (cfBuyQty + dayBuyQty);
				}
				if (daySellQty > 0 || cfSellQty > 0) {
					sellPrice = ((cfSellQty * uploadedPrice) + (daySellQty * daySellAvgPrice))
							/ (cfSellQty + daySellQty);
					mtmSellPrice = ((cfSellQty * cfSellAvgPrice) + (daySellQty * daySellAvgPrice))
							/ (cfSellQty + daySellQty);
				}
				response.setBuyPrice(String.valueOf(buyPrice));
				response.setSellPrice(String.valueOf(sellPrice));
				response.setMtmBuyPrice(String.valueOf(mtmBuyPrice));
				response.setMtmSellprice(String.valueOf(mtmSellPrice));
				responseList.add(response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return responseList;

	}
}

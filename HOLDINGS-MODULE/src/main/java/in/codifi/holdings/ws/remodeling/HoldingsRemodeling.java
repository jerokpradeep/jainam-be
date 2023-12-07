package in.codifi.holdings.ws.remodeling;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.holdings.config.HazelcastConfig;
import in.codifi.holdings.entity.primary.PoaEntity;
import in.codifi.holdings.model.response.Holdings;
import in.codifi.holdings.model.response.HoldingsRespModel;
import in.codifi.holdings.model.response.Symbol;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.AppUtil;
import in.codifi.holdings.utility.StringUtil;
import in.codifi.holdings.ws.model.HoldingsRestRespModel;
import in.codifi.holdings.ws.model.HoldingsSuccessResp;
import in.codifi.holdings.ws.model.SecurityInfo;
import io.quarkus.logging.Log;

@ApplicationScoped
public class HoldingsRemodeling {

	/**
	 * Method to bind holding data
	 * 
	 * @author Gowthaman M
	 * @param userId
	 * @param success
	 * @return
	 */
	public HoldingsRespModel bindHoldingData(HoldingsRestRespModel datas, String userId) {
		List<HoldingsSuccessResp> success = datas.getData();
		HoldingsRespModel extract = new HoldingsRespModel();
		try {

			PoaEntity entity = HazelcastConfig.getInstance().getPoaEntity().get(userId);
			if (entity != null) {
				if (entity.getPoa().equalsIgnoreCase("Y")) {
					extract.setPoa(true);
				} else {
					extract.setPoa(false);
				}
			} else {
				extract.setPoa(false);
			}

			extract.setProduct(success.get(0).getProduct());
			List<Holdings> holdingsList = new ArrayList<>();
			for (HoldingsSuccessResp model : success) {
				Holdings holdingData = new Holdings();
				List<Symbol> symbols = new ArrayList<>();
//				String isin = "";
				for (SecurityInfo secInfo : model.getSecurityInfo()) {
					Symbol symbolDetail = new Symbol();
					String exch = secInfo.getExch();
					String token = secInfo.getScripToken();
					String symbol = secInfo.getSymbol();

					if (StringUtil.isNotNullOrEmpty(exch)) {
						if (exch.equalsIgnoreCase(AppConstants.NSE_EQ)) {
							exch = AppConstants.NSE;
						} else if (exch.equalsIgnoreCase(AppConstants.BSE_EQ)) {
							exch = AppConstants.BSE;
						} else if (exch.equalsIgnoreCase(AppConstants.NSE_FO)) {
							exch = AppConstants.NFO;
						} else if (exch.equalsIgnoreCase(AppConstants.NSE_CUR)) {
							exch = AppConstants.CDS;
						} else if (exch.equalsIgnoreCase(AppConstants.MCX_FO)) {
							exch = AppConstants.MCX;
						} else if (exch.equalsIgnoreCase(AppConstants.BSE_CUR)) {
							exch = AppConstants.BCD;
						}
						symbolDetail.setExchange(exch);
						symbolDetail.setToken(token);
						symbolDetail.setTradingSymbol(symbol);
						symbolDetail.setPdc(AppUtil.getPdc(exch, token));
						symbolDetail.setLtp(model.getLastPrice());
						symbols.add(symbolDetail);
					}

				}
				holdingData.setSymbol(symbols);

				holdingData.setIsin(model.getIsin());
				holdingData.setRealizedPnl(model.getPnl());
				holdingData.setUnrealizedPnl(model.getPnl());
				holdingData.setNetPnl(model.getPnl());
				holdingData.setBuyPrice(model.getAvgPrice());
				holdingData.setNetQty(model.getTotalFree());
				holdingData.setHoldQty("0");
				holdingData.setDpQty("0");
				holdingData.setBenQty("0");
				holdingData.setUnpledgedQty("0");
				holdingData.setCollateralQty(model.getCollateralQty());
				holdingData.setBrkCollQty("0");
				holdingData.setBtstQty("0");
				holdingData.setUsedQty("0");
				holdingData.setTradedQty("0");
				holdingData.setSellableQty("0");
				holdingData.setAuthQty("0");
				holdingsList.add(holdingData);
			}
			extract.setHoldings(holdingsList);
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return extract;
	}
}

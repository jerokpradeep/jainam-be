package in.codifi.mw.controller;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.controller.spec.ITickerTapeController;
import in.codifi.mw.model.request.ReqModel;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.service.spec.ITickerTapeService;
import in.codifi.mw.utility.AppConstants;
import in.codifi.mw.utility.PrepareResponse;

public class TickerTapeController implements ITickerTapeController {
	
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ITickerTapeService tickerTapeService;
	
	/**
	 * Method to get stocks for client
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTicketTapeScrips(ReqModel reqModel) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return tickerTapeService.getTicketTapeScrips(reqModel);
	}

}

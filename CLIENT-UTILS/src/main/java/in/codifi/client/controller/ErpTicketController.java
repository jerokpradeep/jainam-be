package in.codifi.client.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.controller.spec.IErpTicketController;
import in.codifi.client.model.request.TicketRiseReq;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.service.spec.IErpTicketService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/erp")
public class ErpTicketController implements IErpTicketController {
	
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IErpTicketService erpTicketService;
	
	/**
	 * Method to Raise ticket
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> raiseTicket(TicketRiseReq req) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		ClinetInfoModel info = new ClinetInfoModel();
		info.setUserId("117995");
		return erpTicketService.raiseTicket(req, info);
	}
	
	/**
	 * Method for preferred
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> preferred(String mobileNo) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		ClinetInfoModel info = new ClinetInfoModel();
		info.setUserId("117995");
		return erpTicketService.preferred(mobileNo, info);
	}

}

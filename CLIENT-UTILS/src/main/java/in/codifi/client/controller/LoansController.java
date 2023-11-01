package in.codifi.client.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.controller.spec.ILoansController;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.service.spec.ILoansService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/loansProfile")
public class LoansController implements ILoansController {
	
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ILoansService loansService;
	
	@Override
	public RestResponse<GenericResponse> getLoansUserProfile() {

		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("117995");
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return loansService.getLoansUserProfile(info);
	}

}

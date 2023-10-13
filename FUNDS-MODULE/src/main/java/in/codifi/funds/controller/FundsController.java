package in.codifi.funds.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.controller.spec.IFundsController;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.service.spec.IFundsService;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/funds")
public class FundsController implements IFundsController {

	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IFundsService fundsService;

	/**
	 * Method to get limits
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getLimits() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("114014");
		return fundsService.getLimits(info);
	}

}

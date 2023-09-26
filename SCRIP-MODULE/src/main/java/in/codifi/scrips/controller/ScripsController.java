package in.codifi.scrips.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.scrips.controller.spec.ScripsControllerSpecs;
import in.codifi.scrips.model.request.GetContractInfoReqModel;
import in.codifi.scrips.model.request.SearchScripReqModel;
import in.codifi.scrips.model.request.SecurityInfoReqModel;
import in.codifi.scrips.model.response.GenericResponse;
import in.codifi.scrips.service.spec.ScripsServiceSpecs;
import in.codifi.scrips.utility.AppConstants;
import in.codifi.scrips.utility.AppUtil;
import in.codifi.scrips.utility.PrepareResponse;
import in.codifi.scrips.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/scrip")
public class ScripsController implements ScripsControllerSpecs {

	@Inject
	ScripsServiceSpecs scripsService;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	AppUtil appUtil;

	/**
	 * Method to get all scrips by search
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScrips(SearchScripReqModel reqModel) {

		if (reqModel != null && reqModel.getSymbol().trim().length() >= 2) {
			return scripsService.getScrips(reqModel);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.ERROR_MIN_CHAR);
	}

	/**
	 * Method to get contract info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getContractInfo(GetContractInfoReqModel reqModel) {
		return scripsService.getContractInfo(reqModel);
	}

	/**
	 * Method to get security info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSecurityInfo(SecurityInfoReqModel reqModel) {

		if (reqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		String exch = "";
		if (reqModel.getExch().equalsIgnoreCase(AppConstants.NSE)) {
			exch = AppConstants.NSE;
		} else if (reqModel.getExch().equalsIgnoreCase(AppConstants.BSE)) {
			exch = AppConstants.BSE;
		} else if (reqModel.getExch().equalsIgnoreCase(AppConstants.NFO)) {
			exch = AppConstants.NFO;
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
		}

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return scripsService.getSecurityInfo(reqModel, info);
	}
}

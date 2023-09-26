package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.api.controller.spec.IAdvanceController;
import in.codifi.api.model.MWReqModel;
import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.ValidateRequestService;
import in.codifi.api.service.spec.IAdvanceMWService;
import in.codifi.api.service.spec.IMarketWatchService;
import in.codifi.api.util.AppConstants;
import in.codifi.api.util.AppUtil;
import in.codifi.api.util.PrepareResponse;
import in.codifi.api.util.StringUtil;
import in.codifi.cache.model.ClinetInfoModel;
import io.quarkus.logging.Log;

@Path("/advance")
public class AdvanceMWController implements IAdvanceController {
	@Inject
	IAdvanceMWService service;
	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ValidateRequestService validateRequestService;
	@Inject
	IMarketWatchService codifiMwService;

	/**
	 * method to get advance market watch
	 * 
	 * @author sowmiya
	 */
	public RestResponse<ResponseModel> advanceMW(MWReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return service.advanceMW(reqModel, info);
	}

	/**
	 * method to get advance market watch scrips
	 * 
	 * @author sowmiya
	 */
	public RestResponse<ResponseModel> advanceMWScrips(MWReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return service.advanceMWScrips(reqModel, info);
	}
	
	/**
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> deletescrip(MwRequestModel pDto) {
		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return service.deletescrip(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to add the scrip into cache and data base
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> addscrip(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return service.addscrip(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to sort the scrip into cache and data base
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> sortMwScrips(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return service.sortMwScrips(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

}

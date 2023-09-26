package in.codifi.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.api.controller.spec.IPredefinedMwContoller;
import in.codifi.api.entity.primary.PredefinedMwEntity;
import in.codifi.api.model.LatestPreDefinedMWReq;
import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.PreDefMwReqModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IPredefinedMwService;
import in.codifi.api.util.AppConstants;
import in.codifi.api.util.AppUtil;
import in.codifi.api.util.PrepareResponse;
import in.codifi.api.util.StringUtil;
import in.codifi.cache.model.ClinetInfoModel;
import io.quarkus.logging.Log;

@Path("/pre-def/mw")
public class PredefinedMwContoller implements IPredefinedMwContoller {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IPredefinedMwService service;
	@Inject
	AppUtil utils;

	/**
	 * 
	 * Method to get all predefined market watch
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> getAllPrefedinedMwScrips(MwRequestModel pDto) {

		if (pDto == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		if (!utils.isValidUser(pDto.getUserId()))
//			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_USER_SESSION);
		return service.getAllPrefedinedMwScrips();

	}

	/**
	 * Method to add the script
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<ResponseModel> addscrip(PredefinedMwEntity predefinedEntity) {
		return service.addscrip(predefinedEntity);

	}

	/**
	 * Method to delete the script
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<ResponseModel> deletescrip(PredefinedMwEntity predefinedEntity) {
		return service.deletescrip(predefinedEntity);
	}

	/**
	 * Method to0 sort mw scrips
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<ResponseModel> sortMwScrips(PredefinedMwEntity predefinedEntity) {
		return service.sortMwScrips(predefinedEntity);
	}

	/**
	 * Method to get all predefined Market watch by market watch name
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param pDto
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> getPDMwScrips(PreDefMwReqModel pDto) {
		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return service.getPDMwScrips(pDto);
	}

	/**
	 * Method to get pre defined market watch name
	 * 
	 * @author DINESH KUMAR
	 *
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> getMwNameList() {
		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return service.getMwNameList();
	}
	
	/**
	 * Method to update Latest PreDefined MW
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> updateLatestPreDefinedMW(List<LatestPreDefinedMWReq> req) {
		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return service.updateLatestPreDefinedMW(req);
	}
	
}

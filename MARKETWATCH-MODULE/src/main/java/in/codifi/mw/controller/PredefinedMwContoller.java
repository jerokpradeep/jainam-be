package in.codifi.mw.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.controller.spec.IPredefinedMwContoller;
import in.codifi.mw.entity.primary.PredefinedMwEntity;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.service.spec.IPredefinedMwService;
import in.codifi.mw.utility.PrepareResponse;

@Path("/pre-def/mw")
public class PredefinedMwContoller implements IPredefinedMwContoller {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IPredefinedMwService service;

	/**
	 * Method to get all predefined market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAllPrefedinedMwScrips() {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return service.getAllPrefedinedMwScrips();
	}

	/**
	 * Method to add the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addscrip(PredefinedMwEntity predefinedEntity) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return service.addscrip(predefinedEntity);
	}

	/**
	 * Method to delete the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deletescrip(PredefinedMwEntity predefinedEntity) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return service.deletescrip(predefinedEntity);
	}

	/**
	 * Method to0 sort mw scrips
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> sortMwScrips(PredefinedMwEntity predefinedEntity) {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return service.sortMwScrips(predefinedEntity);
	}

}

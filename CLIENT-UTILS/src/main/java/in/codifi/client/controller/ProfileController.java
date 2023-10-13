package in.codifi.client.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.controller.spec.IProfileController;
import in.codifi.client.model.request.ClientDetailsReqModel;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.model.response.SessModel;
import in.codifi.client.service.spec.IProfileService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/profile")
public class ProfileController implements IProfileController {

	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IProfileService profileService;

	/**
	 * Method to invalidate web socket session
	 * 
	 * @author dinesh
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> invalidateWsSession(ClientDetailsReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return profileService.invalidateWsSession(reqModel, info);
	}

	/**
	 * Method to create web socket session
	 * 
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createWsSession(ClientDetailsReqModel reqModel) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return profileService.createWsSession(reqModel, info);
	}

	/**
	 * Method to get user session for WS
	 * 
	 * @author DINESH KUMAR
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDetails() {
		ClinetInfoModel info = appUtil.getClientInfo();
		
		System.out.println("getUser info user id -- "+info.getUserId());
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		/** Verify session **/
		String userSession = AppUtil.getUserSession(info.getUserId());
		System.out.println("getUser userSession -- "+userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();

		SessModel model = new SessModel();
		model.setKey(userSession);
		model.setUserId(info.getUcc());
		System.out.println("getUser model -- "+model);
		return prepareResponse.prepareSuccessResponseObject(model);
	}

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserProfile() {

		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return profileService.getUserProfile(info);
	}

	/**
	 * method to create web socket session test
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> createWsSessionTest(ClientDetailsReqModel reqModel) {
		return profileService.createWsSessionTest(reqModel);
	}

	/**
	 * method to invalid web socket session test
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> invalidateWsSessionTest(ClientDetailsReqModel reqModel) {
		return profileService.invalidateWsSessionTest(reqModel);
	}
}

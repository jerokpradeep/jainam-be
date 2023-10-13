package in.codifi.client.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.controller.spec.IPinToStartbarController;
import in.codifi.client.model.request.PinToStartbarModel;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.service.spec.IPinStartBarService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/startbar")
public class PinToStartbarController implements IPinToStartbarController{

	@Inject
	IPinStartBarService service;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil utils;

	/**
	 * Method to get pin to start bar details
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPinToStartBar() {

		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} 
		return service.getPinToStartBar(info);

	}

	/**
	 * Method to add pin to start bar details
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addPinToStartBar(PinToStartbarModel model) {
		if (model == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return service.addPinToStartBar(model, info);

	}
	
	/**
	 * Method to reload cache
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> reloadCache() {
		
		ClinetInfoModel info = utils.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return service.loadPinToStartBarIntoCache();

	}

}

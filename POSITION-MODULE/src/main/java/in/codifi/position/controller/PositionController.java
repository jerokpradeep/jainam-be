package in.codifi.position.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.position.controller.spec.IPositionController;
import in.codifi.position.model.request.OrderDetails;
import in.codifi.position.model.request.PositionConversionReq;
import in.codifi.position.model.request.UserSession;
import in.codifi.position.model.response.GenericResponse;
import in.codifi.position.service.spec.IPositionService;
import in.codifi.position.utility.AppConstants;
import in.codifi.position.utility.AppUtil;
import in.codifi.position.utility.PrepareResponse;
import in.codifi.position.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/positions")
public class PositionController implements IPositionController {

	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IPositionService positionService;

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getposition() {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return positionService.getposition(info);
	}

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDailyPosition() {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return positionService.getDailyPosition(info);
	}

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getExpiryPosition() {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return positionService.getExpiryPosition(info);
	}

	/**
	 * Method to get the position conversion
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionConversion(PositionConversionReq positionConversionReq) {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("114014");
		return positionService.positionConversion(positionConversionReq, info);
	}

	/**
	 * Method to user session in cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> putUserSession(UserSession session) {
		return positionService.putUserSession(session);
	}

	/**
	 * Method to Square Off position
	 * 
	 * @author Dinesh
	 * @modified author Nesan
	 * @param orderDetails
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails) {

		if (orderDetails == null || orderDetails.size() < 1)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		ClinetInfoModel info = new ClinetInfoModel();
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return positionService.positionSquareOff(orderDetails, info);
	}

}

package in.codifi.client.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.model.request.ClientDetailsReqModel;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.service.spec.IProfileService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import in.codifi.client.ws.service.ClientDetailsRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ProfileService implements IProfileService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	ClientDetailsRestService restService;

	/**
	 * Method to invalidate web socket session
	 * 
	 * @author dinesh
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> invalidateWsSession(ClientDetailsReqModel reqModel, ClinetInfoModel info) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(reqModel.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Verify session **/
			String accessToken = appUtil.getAcToken();
			if (StringUtil.isNullOrEmpty(accessToken))
				return prepareResponse.prepareUnauthorizedResponse();

			StringBuilder request = new StringBuilder();
			request.append("uid=" + info.getUserId() + "_" + reqModel.getSource());
			request.append("&usession=" + accessToken);
			request.append("&src=" + reqModel.getSource());
			request.append("&vcode=" + "STONE_AGE");
			return restService.invalidateWsSession(request.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to create web socket session
	 * 
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createWsSession(ClientDetailsReqModel reqModel, ClinetInfoModel info) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(reqModel.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Get session **/
			String accessToken = appUtil.getAcToken();
			if (StringUtil.isNullOrEmpty(accessToken))
				return prepareResponse.prepareUnauthorizedResponse();

			StringBuilder request = new StringBuilder();
			request.append("uid=" + info.getUserId() + "_" + reqModel.getSource());
			request.append("&usession=" + accessToken);
			request.append("&src=" + reqModel.getSource());
			request.append("&vcode=" + "STONE_AGE");
			return restService.createWsSession(request.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserProfile(ClinetInfoModel info) {
		try {
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFFQ0IxQjIzM0U0QzZFODE3QkIwMTFBQjAyNEZEIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMDI0NTc5OSwiaWF0IjoxNzAwMjIwNjkzfQ.BbsTqinZsMsEUCc7eLvGEN-4DgbPbaWsVLSzblTxCOQ";
			System.out.println("userSession--" + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return restService.getUserProfile(userSession, info);
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to create web socket session test
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> createWsSessionTest(ClientDetailsReqModel reqModel) {
		try {
			if (StringUtil.isNullOrEmpty(reqModel.getSource()) && StringUtil.isNullOrEmpty(reqModel.getUserId())
					&& StringUtil.isNullOrEmpty(reqModel.getToken()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			ObjectMapper mapper = new ObjectMapper();
			String request = mapper.writeValueAsString(reqModel);
			return restService.createWsSession(request.toString());
		} catch (Exception e) {
			Log.error("createWsSessionTest", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to invalidate web socket session test
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> invalidateWsSessionTest(ClientDetailsReqModel reqModel) {
		try {
			if (StringUtil.isNullOrEmpty(reqModel.getSource()) && StringUtil.isNullOrEmpty(reqModel.getUserId())
					&& StringUtil.isNullOrEmpty(reqModel.getToken()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			ObjectMapper mapper = new ObjectMapper();
			String request = mapper.writeValueAsString(reqModel);
			return restService.invalidateWsSession(request.toString());
		} catch (Exception e) {
			Log.error("invalidateWsSessionTest", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

package in.codifi.client.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.model.request.ClientDetailsReqModel;
import in.codifi.client.model.response.GenericResponse;

public interface IProfileService {

	/**
	 * Method to invalidate web socket session
	 * 
	 * @author dinesh
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> invalidateWsSession(ClientDetailsReqModel reqModel, ClinetInfoModel info);

	/**
	 * Method to create web socket session
	 * 
	 * @param model
	 * @return
	 */
	RestResponse<GenericResponse> createWsSession(ClientDetailsReqModel reqModel, ClinetInfoModel info);

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	RestResponse<GenericResponse> getUserProfile(ClinetInfoModel info);

	/**
	 * method to create web socket session test
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	RestResponse<GenericResponse> createWsSessionTest(ClientDetailsReqModel reqModel);

	/**
	 * method to invalidate web socket session test
	 * 
	 * @author SOWMIYA
	 * @param reqModel
	 * @return
	 */
	RestResponse<GenericResponse> invalidateWsSessionTest(ClientDetailsReqModel reqModel);

}

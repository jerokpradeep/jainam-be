package in.codifi.common.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.controller.spec.IndicesControllerSpec;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.service.PrepareResponse;
import in.codifi.common.service.spec.IndicesServiceSpec;
import in.codifi.common.utility.AppUtil;

@Path("/indices")
public class IndicesController implements IndicesControllerSpec {

	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IndicesServiceSpec indicesServiceSpec;

	/**
	 * Method to get indices details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getIndices() {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		}
//		ClinetInfoModel info = new ClinetInfoModel();
		return indicesServiceSpec.getIndices();
	}
	
	/**
	 * Method to Add indices data
	 * 
	 * @author Gowthaman M
	 * @return 
	 */
	@Override
	public RestResponse<GenericResponse> insertIndicesData() {
//		ClinetInfoModel info = appUtil.getClientInfo();
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error("Client info is null");
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		return indicesServiceSpec.insertIndicesData();
	}

}

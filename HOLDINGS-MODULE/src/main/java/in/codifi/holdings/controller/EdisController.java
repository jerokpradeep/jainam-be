package in.codifi.holdings.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.controller.spec.EdisControllerSpec;
import in.codifi.holdings.model.request.EdisHoldModel;
import in.codifi.holdings.model.request.EdisSummaryRequest;
import in.codifi.holdings.model.response.GenericResponse;
import in.codifi.holdings.service.spec.EdisServiceSpec;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.AppUtil;
import in.codifi.holdings.utility.PrepareResponse;
import in.codifi.holdings.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/edis")
public class EdisController implements EdisControllerSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	AppUtil appUtil;

	@Inject
	EdisServiceSpec service;

	/*
	 * @author Nesan
	 * 
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getRedirectUrl(List<EdisHoldModel> model) {
		if (model == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("117995");
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return service.getRedirectUrl(model, info.getUserId());

	}
	
	/**
	 * Method to Get EdisSummary
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getEdisSummary(EdisSummaryRequest req) {
		ClinetInfoModel info = appUtil.getClientInfo();
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return service.getEdisSummary(req, info);

	}

}

package in.codifi.common.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.controller.spec.NFOFutureControllerSpec;
import in.codifi.common.model.request.NFOFutureReqModel;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.service.spec.NFOFutureServiceSpec;

@Path("/nfo")
public class NFOFutureController implements NFOFutureControllerSpec {

	@Inject
	NFOFutureServiceSpec futureService;

	/**
	 * method to get nfo future details
	 * 
	 * @author sowmiya
	 * @param reqModel
	 * @return
	 */
	public RestResponse<GenericResponse> getNFOFutureDetails(NFOFutureReqModel reqModel) {
		return futureService.getNFOFutureDetails(reqModel);
	}

	/**
	 * method to load nfo future
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<GenericResponse> loadNFOFuture() {
		return futureService.loadNFOFuture();
	}

}

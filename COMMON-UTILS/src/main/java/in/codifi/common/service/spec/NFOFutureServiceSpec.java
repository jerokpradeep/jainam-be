package in.codifi.common.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.model.request.NFOFutureReqModel;
import in.codifi.common.model.response.GenericResponse;

public interface NFOFutureServiceSpec {

	/**
	 * method to get nfo future details
	 * 
	 * @author sowmiya
	 * @param reqModel
	 * @return
	 */
	RestResponse<GenericResponse> getNFOFutureDetails(NFOFutureReqModel reqModel);

	/**
	 * method load nfo future
	 * 
	 * @author sowmiya
	 * @return
	 */
	RestResponse<GenericResponse> loadNFOFuture();

}

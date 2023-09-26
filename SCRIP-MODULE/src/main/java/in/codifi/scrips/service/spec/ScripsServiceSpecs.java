package in.codifi.scrips.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.scrips.model.request.GetContractInfoReqModel;
import in.codifi.scrips.model.request.SearchScripReqModel;
import in.codifi.scrips.model.request.SecurityInfoReqModel;
import in.codifi.scrips.model.response.GenericResponse;

public interface ScripsServiceSpecs {

	/**
	 * Method to get all scrips by search
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getScrips(SearchScripReqModel reqModel);

	/**
	 * Method to get contract info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getContractInfo(GetContractInfoReqModel reqModel);

	/**
	 * Method to get security info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getSecurityInfo(SecurityInfoReqModel model, ClinetInfoModel info);
}

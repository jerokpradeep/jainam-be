package in.codifi.funds.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.model.response.GenericResponse;


public interface IFundsService {

	/**
	 * Method to get limits
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getLimits(ClinetInfoModel info);

}

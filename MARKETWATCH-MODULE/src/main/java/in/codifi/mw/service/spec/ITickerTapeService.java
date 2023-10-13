package in.codifi.mw.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.request.ReqModel;
import in.codifi.mw.model.response.GenericResponse;

public interface ITickerTapeService {
	
	/**
	 * Method to get stocks for client
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getTicketTapeScrips(ReqModel reqModel);

}

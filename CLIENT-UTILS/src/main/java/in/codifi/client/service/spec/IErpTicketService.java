package in.codifi.client.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.model.request.TicketRiseReq;
import in.codifi.client.model.response.GenericResponse;

public interface IErpTicketService {
	
	/**
	 * Method to Raise ticket
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> raiseTicket(TicketRiseReq req, ClinetInfoModel info);
	
	/**
	 * Method for preferred
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> preferred(String mobileNo, ClinetInfoModel info);

}

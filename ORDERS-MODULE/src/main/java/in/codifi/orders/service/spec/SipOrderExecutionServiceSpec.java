package in.codifi.orders.service.spec;

import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.model.request.SipOrderDetails;
import in.codifi.orders.model.response.GenericResponse;

public interface SipOrderExecutionServiceSpec {

	/**
	 * Method to execute SIP place orders
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<List<GenericResponse>> sipPlaceOrder(List<SipOrderDetails> orderDetails, ClinetInfoModel info);

	/**
	 * Method to sip Order Book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> sipOrderBook(ClinetInfoModel info);

	/**
	 * Method to cancel sip Order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> cancelSipOrder(SipOrderDetails orderDetails, ClinetInfoModel info);

}

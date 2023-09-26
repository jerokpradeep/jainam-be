package in.codifi.orders.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;

public interface OrderInfoServiceSpec {

	/**
	 * Method to get order book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getOrderBookInfo(ClinetInfoModel info);

	/**
	 * Method to get trade book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getTradeBookInfo(ClinetInfoModel info);
	
	/**
	 * Method to get Order History
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getOrderHistory(OrderReqModel req, ClinetInfoModel info);

}

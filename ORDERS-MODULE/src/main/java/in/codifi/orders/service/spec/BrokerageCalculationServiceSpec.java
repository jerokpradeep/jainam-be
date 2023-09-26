package in.codifi.orders.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.model.request.BrokerageCalculationReq;
import in.codifi.orders.model.response.GenericResponse;

public interface BrokerageCalculationServiceSpec {

	/**
	 * Method to calculate Brokerage
	 * 
	 * @author Gowthaman M
	 * @param BrokerageCalculationReq
	 * @return
	 */
	RestResponse<GenericResponse> calculateBrokerage(BrokerageCalculationReq calculationReq, ClinetInfoModel info);

}

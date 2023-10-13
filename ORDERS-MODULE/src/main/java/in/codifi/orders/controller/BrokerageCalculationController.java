package in.codifi.orders.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.controller.spec.BrokerageCalculationControllerSpec;
import in.codifi.orders.model.request.BrokerageCalculationReq;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.BrokerageCalculationServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/brokerage")
public class BrokerageCalculationController implements BrokerageCalculationControllerSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	BrokerageCalculationServiceSpec brokerageServiceSpec;

	/**
	 * Method to calculate Brokerage
	 * 
	 * @author Gowthaman M
	 * @param BrokerageCalculationReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> calculateBrokerage(BrokerageCalculationReq calculationReq) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return brokerageServiceSpec.calculateBrokerage(calculationReq, info);
	}

}

package in.codifi.orders.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.controller.spec.SipOrderExcutionControllerSpec;
import in.codifi.orders.model.request.SipOrderDetails;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.SipOrderExecutionServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/sipOrders")
public class SipOrderExcutionController implements SipOrderExcutionControllerSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	SipOrderExecutionServiceSpec sipExecutionServiceSpec;

	/**
	 * Method to execute SIP place orders
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> placeSIPOrder(List<SipOrderDetails> orderDetails) {

		if (orderDetails == null || orderDetails.size() < 1)
			return prepareResponse.prepareFailedResponseForList(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponseForList(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return sipExecutionServiceSpec.sipPlaceOrder(orderDetails, info);
	}

	/**
	 * Method to sip Order Book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> sipOrderBook() {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return sipExecutionServiceSpec.sipOrderBook(info);
	}

	/**
	 * Method to cancel sip Order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> cancelSipOrder(SipOrderDetails orderDetails) {

		if (orderDetails == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("111560");
		return sipExecutionServiceSpec.cancelSipOrder(orderDetails, info);
	}

}

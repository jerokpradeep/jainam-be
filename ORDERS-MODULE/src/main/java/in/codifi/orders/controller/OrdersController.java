package in.codifi.orders.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.controller.spec.OrdersControllerSpec;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.OrdersServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/order")
public class OrdersController implements OrdersControllerSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	OrdersServiceSpec ordersServiceSpec;

	@Inject
	AppUtil appUtil;

	/**
	 * Method to place order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> placeOrder(OrderReqModel orderReqModel) {
		System.out.println("I am here");
		if (orderReqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (StringUtil.isNullOrEmpty(info.getUserId())) { // || info == null -- TODO
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return ordersServiceSpec.placeOrder(orderReqModel, info);
	}

	/**
	 * Method to modify order
	 * 
	 * @author Nesan
	 *
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> modifyOrder(OrderReqModel orderReqModel) {
		if (orderReqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}

		return ordersServiceSpec.modifyOrder(orderReqModel, info);
	}

	/**
	 * Method to cancel order
	 */
	@Override
	public RestResponse<GenericResponse> cancelOrder(OrderReqModel orderReqModel) {
		if (orderReqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return ordersServiceSpec.cancelOrder(orderReqModel, info);
	}

}

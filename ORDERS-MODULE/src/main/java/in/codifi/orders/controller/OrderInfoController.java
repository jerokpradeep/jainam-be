package in.codifi.orders.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.controller.spec.OrderInfoControllerSpec;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.OrderInfoServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/info")
public class OrderInfoController implements OrderInfoControllerSpec {

	@Inject
	AppUtil appUtil;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	OrderInfoServiceSpec orderInfoService;

	/**
	 * Method to get order book details
	 * 
	 * @author Gowthaman M
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getOrderBookInfo() {
		
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("wcm549");
		return orderInfoService.getOrderBookInfo(info); 
	}

	/**
	 * Method to get trade book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTradeBookInfo() {
		
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return orderInfoService.getTradeBookInfo(info);
	}
	
	/**
	 * Method to get Order History
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getOrderHistory(OrderReqModel req) {
		
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return orderInfoService.getOrderHistory(req, info);
	}
	
	/**
	 * Method to get Gtd order book details
	 * 
	 * @author Gowthaman M
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getGtdOrderBookInfo() {
		
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("117995");
		return orderInfoService.getGtdOrderBookInfo(info); 
	}

}

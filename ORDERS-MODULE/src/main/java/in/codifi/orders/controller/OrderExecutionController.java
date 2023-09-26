package in.codifi.orders.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.controller.spec.OrderExecutionControllerSpec;
import in.codifi.orders.model.request.MarginReqModel;
import in.codifi.orders.model.request.OrderDetails;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.OrderExecutionServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import io.quarkus.logging.Log;

@Path("/orders")
public class OrderExecutionController implements OrderExecutionControllerSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	OrderExecutionServiceSpec serviceSpec;

	@Inject
	AppUtil appUtil;

	/**
	 * Method to execute place orders
	 * 
	 * @author Nesan
	 *
	 * @param
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> placeOrder(List<OrderDetails> orderDetails) {

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
//		info.setUserId("C00008");
		return serviceSpec.placeOrder(orderDetails, info);
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
	public RestResponse<GenericResponse> modifyOrder(OrderDetails orderReqModel) {
		if (orderReqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("117995");
		return serviceSpec.modifyOrder(orderReqModel, info);
	}

	/**
	 * Method to modify order
	 * 
	 * @author Dinesh
	 * @modified author Nesan
	 * @param orderReqModel
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> cancelOrder(List<OrderDetails> orderDetails) {

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
		return serviceSpec.cancelOrder(orderDetails, info);
	}

	/**
	 * Method to execute basket orders
	 * 
	 * @author Dinesh
	 *
	 * @param orderDetails
	 * @return
	 */
	@Override
	public List<GenericResponse> executeBasketOrder(List<OrderDetails> orderDetails) {
		ObjectMapper om = new ObjectMapper();
		try {
			Log.error("executeBasketOrder is payload " + om.writeValueAsString(orderDetails));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		List<GenericResponse> responseList = new ArrayList<>();
		GenericResponse response = new GenericResponse();
		if (orderDetails == null || orderDetails.size() < 1) {
			response.setStatus(AppConstants.STATUS_NOT_OK);
			response.setMessage(AppConstants.INVALID_PARAMETER);
			responseList.add(response);
			return responseList;
		}
//		ClinetInfoModel info = new ClinetInfoModel();
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			response.setStatus(AppConstants.STATUS_NOT_OK);
			response.setMessage(AppConstants.FAILED_STATUS);
			responseList.add(response);
			return responseList;
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			response.setStatus(AppConstants.STATUS_NOT_OK);
			response.setMessage(AppConstants.GUEST_USER_ERROR);
			responseList.add(response);
		}
		return serviceSpec.executeBasketOrder(orderDetails, info);
	}

	/**
	 * Method to Square Off position
	 * 
	 * @author Dinesh
	 * @modified author Nesan
	 * @param orderDetails
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails) {

		if (orderDetails == null || orderDetails.size() < 1)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		ClinetInfoModel info = new ClinetInfoModel();
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
		return serviceSpec.positionSquareOff(orderDetails, info, Thread.currentThread().getName());
	}

	/**
	 * Method to square off All positions
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> positionSquareOffAll() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return serviceSpec.positionSquareOffAll(info);
	}

	/**
	 * 
	 * Method to Get Order Margin
	 * 
	 * @author Dinesh Kumar
	 * @modified author Nesan
	 * 
	 * @param orderDetails
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getOrderMargin(MarginReqModel marginReqModel) {

		if (marginReqModel == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
		}

//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("C00008");
		return serviceSpec.getOrderMargin(marginReqModel, info);
	}

}

package in.codifi.basket.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.controller.spec.IBasketOrderController;
import in.codifi.basket.model.request.BasketOrderReq;
import in.codifi.basket.model.request.ExecuteBasketOrderReq;
import in.codifi.basket.model.request.SpanMarginReq;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.service.spec.IBasketOrderService;
import in.codifi.basket.utility.AppConstants;
import in.codifi.basket.utility.AppUtil;
import in.codifi.basket.utility.PrepareResponse;
import in.codifi.basket.utility.StringUtil;
import in.codifi.cache.model.ClinetInfoModel;
import io.quarkus.logging.Log;

@Path("/basketorder")
public class BasketOrderController implements IBasketOrderController {

	@Inject
	IBasketOrderService basketOrderService;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;

	/**
	 * method to Create Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createBasketName(BasketOrderReq basketOrderReq) {
		if (basketOrderReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.createBasketName(basketOrderReq, info);
	}

	/**
	 * method to Get All Basket Names
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getBasketNames() {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.basketdetails(info);
	}

	/**
	 * method to Rename Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateBasketName(BasketOrderReq basketOrderReq) {
		if (basketOrderReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.updateBasketName(basketOrderReq, info);
	}

	/**
	 * method to Delete the whole Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteBasket(int basketId) {
		if (basketId < 0)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.deleteBasket(basketId, info);
	}

	/**
	 * method to Add Scrips to Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addBasketScrip(BasketOrderReq basketOrderReq) {
		if (basketOrderReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.addBasketScrip(basketOrderReq, info);
	}

	/**
	 * method to Delete Scrips in Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteBasketScrip(BasketOrderReq basketOrderReq) {
		if (basketOrderReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.deleteBasketScrip(basketOrderReq, info);
	}

	/**
	 * Method to update basket scrips
	 * 
	 * @author Gowthaman M
	 * @param basketOrderReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateBasketScrip(BasketOrderReq basketOrderReq) {
		if (basketOrderReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.updateBasketScrip(basketOrderReq, info);
	}

	/**
	 * method to Retrieve Scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> retrieveScrips(int basketId) {

		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.retrieveScrips(basketId, info);
	}

	/**
	 * Method to execute basket orders
	 * 
	 * @author Gowthaman M
	 * @param executeBasketOrderReq
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> excuteBasketOrder(ExecuteBasketOrderReq executeBasketOrderReq) {
		Log.error("executeBasketOrderReq  is " + executeBasketOrderReq);
		if (executeBasketOrderReq == null)
			return prepareResponse.prepareFailedResponseForList(AppConstants.INVALID_PARAMETER);
//		ClinetInfoModel info = new ClinetInfoModel();
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);
		}
		System.out.println("ExcuteBasketOrder userId--" + info.getUserId());
		return basketOrderService.excuteBasketOrder(executeBasketOrderReq, info);
	}

	/**
	 * Method to reset execution status
	 * 
	 * @author Gowthaman M
	 * @param basketOrderReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> resetExecutionStatus(int basketId) {
		if (basketId < 0)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.resetExecutionStatus(basketId, info);
	}

	/**
	 * Method to get span margin
	 * 
	 * @author Gowthaman M
	 * @param spanMarginEntity
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSpanMargin(List<SpanMarginReq> spanMarginReq) {
		if (spanMarginReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return basketOrderService.getSpanMargin(spanMarginReq, info);
	}

}

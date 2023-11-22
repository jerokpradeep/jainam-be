package in.codifi.orders.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.model.request.OrderDetails;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.OrderInfoServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.OrdersRestService;
import in.codifi.orders.ws.model.OrderBookReqModel;
import in.codifi.orders.ws.service.OrdersRestServiceOdin;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OrderInfoService implements OrderInfoServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	OrdersRestServiceOdin restService;
	@Inject
	OrdersRestService ordersRestService;
	@Inject
	RestServiceProperties props;

	/**
	 * Method to get order book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getOrderBookInfo(ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFFRkEzQzIwOTVEODQ4NzQ5OEFCN0E5QTFBNDI0IiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMDMzMjE5OSwiaWF0IjoxNzAwMjk0OTAxfQ.C4kqkZZowPqLO6aeFuLSHXaHG_uNZTfBG7a1OqAz4qY";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** prepare request body */
			String req = prepareOrderBookReq(null, userSession, info);
			if (req == null)
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Get Order Book **/
			return ordersRestService.getOrderBookInfo(req, userSession, info);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get order book request
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private String prepareOrderBookReq(OrderDetails orderDetails, String session, ClinetInfoModel info) {

		String request = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			OrderBookReqModel reqModel = new OrderBookReqModel();
			reqModel.setUid(info.getUserId());

			/** If product exist **/
			if (orderDetails != null && StringUtil.isNotNullOrEmpty(orderDetails.getProduct())) {
				String productType = HazelcastConfig.getInstance().getProductTypes().get(AppConstants.PRODUCT_TYPE);
				if (StringUtil.isNullOrEmpty(productType)) {
					Log.error("Product type is empty. Not able to map for get order book request");
					return request;
				}
				String restOrderType = HazelcastConfig.getInstance().getOrderTypes().get(AppConstants.ORDER_TYPE);
				if (StringUtil.isNullOrEmpty(restOrderType)) {
					Log.error("Order type is empty to map for get order book request");
					return request;
				}
				String orderType = orderDetails.getOrderType().trim();
				if (orderType.equalsIgnoreCase(AppConstants.AMO) || orderType.equalsIgnoreCase(AppConstants.REGULAR)) {
					reqModel.setPrd(productType);
				} else if (orderType.equalsIgnoreCase(AppConstants.BRACKET)
						|| orderType.equalsIgnoreCase(AppConstants.COVER)) {
					reqModel.setPrd(restOrderType);
				} else {
					Log.error("Invalid order type. Not able to map for get order book request");
					return request;
				}
				reqModel.setPrd(orderDetails.getProduct());
			}

			String json = mapper.writeValueAsString(reqModel);
			request = AppConstants.JDATA + AppConstants.SYMBOL_EQUAL + json + AppConstants.SYMBOL_AND
					+ AppConstants.JKEY + AppConstants.SYMBOL_EQUAL + session;
		} catch (Exception e) {
			Log.error(e);
		}
		return request;
	}

	/**
	 * Method to get trade book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTradeBookInfo(ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IldDTTU0OSIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFDMzhGQTM1RkJDQjBCOTY1NkYzMzg4MjJFMkE2IiwidXNlckNvZGUiOiJPRkhZVyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5OTAzNjE5OSwiaWF0IjoxNjk5MDE4Njk5fQ.ExTLGTYk7OHRuaLnHEWKfm3nuw_x-qwsKisiPpxvaOM";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Get Trade book **/
			return ordersRestService.getTradeBookInfo(userSession, info);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get Order History
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getOrderHistory(OrderReqModel req, ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjE0MDQiLCJncm91cElkIjoiSE8iLCJ1c2VySWQiOiJBUElURVNUIiwidGVtcGxhdGVJZCI6IlNUQVRJQzEiLCJ1ZElkIjoiNWMzNDBiZDctNWJkMy00YWFmLTlkMDMtYjI2NjI2YjE2ODU4Iiwib2NUb2tlbiI6IjB4MDFEMDdDRTdBMTM0QTcxOURDQ0IwMkVEN0JDMTZGIiwidXNlckNvZGUiOiJOV1NZRiIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMTQwNCIsImV4cCI6MTY4OTA0NDg4MCwiaWF0IjoxNjU3NTA4OTIxfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NzAyNjU5OSwiaWF0IjoxNjg3MDA2MDAwfQ.XRVHJNddlqckXPKF8-5UL4KoH7yGj6m6MP8J10PaUFs";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Get Trade book **/
			return ordersRestService.getOrderHistory(req, userSession, info);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
	
	/**
	 * Method to get Gtd order book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getGtdOrderBookInfo(ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxMzczMTIxMTBBQzE4RjkyQTRDNTM3QzM1NDNDOCIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODY5MDU5OSwiaWF0IjoxNjk4NjY4MDM2fQ.27QxEj_JjgHH7XiFq2gmxv8KTHPRV3ptm3eCCGJfoXM";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Get Order Book **/
			return ordersRestService.getGtdOrderBookInfo(userSession, info);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

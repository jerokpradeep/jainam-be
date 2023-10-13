package in.codifi.orders.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.model.request.BrokerageCalculationReq;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.BrokerageCalculationServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.OrdersRestService;
import in.codifi.orders.ws.model.OrderMarginInfoLegDetails;
import in.codifi.orders.ws.model.OrderMarginInfoReq;

@ApplicationScoped
public class BrokerageCalculationService implements BrokerageCalculationServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	OrdersRestService ordersRestService;
	@Inject
	RestServiceProperties props;

	/**
	 * Method to calculate Brokerage
	 * 
	 * @author Gowthaman M
	 * @param BrokerageCalculationReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> calculateBrokerage(BrokerageCalculationReq calculationReq,
			ClinetInfoModel info) {
		try {
			String validation = validateBrokerage(calculationReq);
			if (!validation.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
				return prepareResponse.prepareFailedResponse(validation);
			}

			/** Verify session **/
			Log.info("Orders  userId - " + info.getUserId());
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDE5MzY0MDFCM0IyRTI2OUFENUIwQTBDMUZGQTZBIiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjkxNTE5Mzk5LCJpYXQiOjE2OTE0ODk5NjF9.aFUKWSABd7qkgBGlbb0tZ2AywlBW_ROTnazKBmXJrOY";
			Log.info("Orders  userSession - " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			String orderMarginInfoReq = prepareOrderMarginInfoReq(calculationReq);
			System.out.println("orderMarginInfoReq -- " + orderMarginInfoReq);
			String brokerage = ordersRestService.getOrderMarginInfoBrokerage(orderMarginInfoReq, userSession, info);

			return calculateBrokerage(calculationReq, userSession, info, brokerage);
		} catch (Exception e) {
			Log.error("calculateBrokerage" + e.getMessage());
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate brokerage calculation
	 * 
	 * @author Gowthaman M
	 * @param pCalculationReq
	 * @return
	 */
	public String validateBrokerage(BrokerageCalculationReq pCalculationReq) {
		String response = AppConstants.SUCCESS_STATUS;
		try {
			if (StringUtil.isNullOrEmpty(pCalculationReq.getExchange())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getProductType())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getTransType())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getQty())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getPrice())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getLegIndicator())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getMode())
					|| StringUtil.isNullOrEmpty(pCalculationReq.getToken())) {
				return AppConstants.INVALID_PARAMETER;
			}

			if (pCalculationReq.getMode().equalsIgnoreCase("Modify")) {
				if (StringUtil.isNullOrEmpty(pCalculationReq.getOldPrice())
						|| StringUtil.isNullOrEmpty(pCalculationReq.getOldQty())) {
					return AppConstants.INVALID_PARAMETER;
				}

			}

			if (pCalculationReq.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
				pCalculationReq.setExchange("1");
			} else if (pCalculationReq.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
				pCalculationReq.setExchange("2");
			} else if (pCalculationReq.getExchange().equalsIgnoreCase(AppConstants.NFO)) {
				pCalculationReq.setExchange("3");
			} else {
				return AppConstants.INVALID_EXCH;
			}

			if (pCalculationReq.getTransType().equalsIgnoreCase(AppConstants.BUY)) {
				pCalculationReq.setTransType(AppConstants.ONE);
			} else if (pCalculationReq.getTransType().equalsIgnoreCase(AppConstants.SELL)) {
				pCalculationReq.setTransType(AppConstants.TWO);
			} else {
				return AppConstants.INVALID_TRANS_TYPE;
			}

			if (pCalculationReq.getLegIndicator().equalsIgnoreCase(AppConstants.LEG_INDICATOR_NORMAL)) {
				pCalculationReq.setLegIndicator(AppConstants.ONE);
			} else if (pCalculationReq.getLegIndicator().equalsIgnoreCase(AppConstants.LEG_INDICATOR_SPREAD)) {
				pCalculationReq.setLegIndicator(AppConstants.FOUR);
			} else if (pCalculationReq.getLegIndicator().equalsIgnoreCase(AppConstants.LEG_INDICATOR_2LEG)) {
				pCalculationReq.setLegIndicator(AppConstants.FIVE);
			} else if (pCalculationReq.getLegIndicator().equalsIgnoreCase(AppConstants.LEG_INDICATOR_3LEG)) {
				pCalculationReq.setLegIndicator(AppConstants.SIX);
			} else {
				return AppConstants.INVALID_TRANS_TYPE;
			}

		} catch (Exception e) {
			Log.error("validateBrokerage -- " + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Method to get Brokerage from odin rest service
	 * 
	 * @author Gowthaman M
	 * @param pCalculationReq
	 * @param pUserSession
	 * @param pInfo
	 * @return
	 */
	public RestResponse<GenericResponse> calculateBrokerage(BrokerageCalculationReq pCalculationReq,
			String pUserSession, ClinetInfoModel pInfo, String brokerage) {
		try {
			String productType = HazelcastConfig.getInstance().getProductTypes().get(pCalculationReq.getProductType());

			String brokerageUrl = AppConstants.PRODUCT_EQUAL_TO + AppConstants.BROKERAGE_AERO
					+ AppConstants.THEME_EQUAL_TO + AppConstants.BROKERAGE_THEME + AppConstants.USERID_EQUAL_TO
					+ pInfo.getUserId() + AppConstants.GROUPID_EQUAL_TO + AppConstants.BROKERAGE_GROUPID
					+ AppConstants.MKTSEGMENTID_EQUAL_TO + pCalculationReq.getExchange() + AppConstants.SERIES_EQUAL_TO
					+ AppConstants.BROKERAGE_SERIES_EQ + AppConstants.PRODUCT_TYPE_EQUAL_TO + productType
					+ AppConstants.TRANSACTION_TYPE_EQUAL_TO + pCalculationReq.getTransType()
					+ AppConstants.QUANTITY_EQUAL_TO + pCalculationReq.getQty() + AppConstants.PRICE_EQUAL_TO
					+ pCalculationReq.getPrice() + AppConstants.BROKERAGE_EQUAL_TO + brokerage
					+ AppConstants.LEGINDICATOR_EQUAL_TO + "1" + AppConstants.SESSION_ID_EQUAL_TO + pUserSession
					+ AppConstants.INSTRUMENT_EQUAL_TO + AppConstants.BROKERAGE_INSTRUMENT_EQUITIES
					+ AppConstants.PRICELOCATOR_EQUAL_TO + AppConstants.ONE;

			System.out.println("Before encode -- " + brokerageUrl);
			String encodedUrl = Base64.getUrlEncoder().encodeToString(brokerageUrl.getBytes());
			System.out.println("After encode -- " + encodedUrl);

			return ordersRestService.getBrokerageCalculation(pInfo, encodedUrl, pUserSession);

		} catch (Exception e) {
			Log.error("calculateBrokerage -- " + e.getMessage());
			e.printStackTrace();
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare OrderMargin Info Request
	 * 
	 * @author Gowthaman M
	 * @param calculationReq
	 * @return
	 */
	public String prepareOrderMarginInfoReq(BrokerageCalculationReq calculationReq) {
		OrderMarginInfoReq req = new OrderMarginInfoReq();
		ObjectMapper mapper = new ObjectMapper();
		String response = "";
		String price = "";
		try {
			if (calculationReq.getPrice().contains(".")) {
				price = calculationReq.getPrice().replace(".", "");
			}

			req.setNoOfLegs(Integer.parseInt(calculationReq.getLegIndicator()));
			if (calculationReq.getMode().equalsIgnoreCase("Modify")) {
				req.setMode("M");
			} else {
				req.setMode("N");
			}
			req.setFeTraceId(AppConstants.FE_TRACE_ID);

			List<OrderMarginInfoLegDetails> infoLegDetailslist = new ArrayList<>();

			OrderMarginInfoLegDetails infoLegDetails = new OrderMarginInfoLegDetails();

			infoLegDetails.setLegNo(Integer.parseInt(calculationReq.getLegIndicator()));
			infoLegDetails.setBuyOrSell(Integer.parseInt(calculationReq.getTransType()));
			infoLegDetails.setMktSegmentId(Integer.parseInt(calculationReq.getExchange()));
			infoLegDetails.setToken(Integer.parseInt(calculationReq.getToken()));
			infoLegDetails.setQty(Integer.parseInt(calculationReq.getQty()));
			infoLegDetails.setPrice(Integer.parseInt(price));
			infoLegDetails.setMktFlag(AppConstants.PAYMENT_PRIORITY);
			if (req.getMode().equalsIgnoreCase("M")) {
				infoLegDetails.setOldPrice(Integer.parseInt(calculationReq.getOldPrice()));
				infoLegDetails.setOldQuantity(Integer.parseInt(calculationReq.getOldQty()));
			}
			infoLegDetails.setProductType("M");
			infoLegDetails.setLegIndicator(Integer.parseInt(calculationReq.getLegIndicator()));
			infoLegDetailslist.add(infoLegDetails);

			req.setLegDetails(infoLegDetailslist);

			response = mapper.writeValueAsString(req);
		} catch (Exception e) {
			Log.error("prepare OrderMarginInfo Req-- " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

}

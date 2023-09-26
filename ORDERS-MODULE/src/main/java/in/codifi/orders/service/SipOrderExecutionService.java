package in.codifi.orders.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.model.request.SipOrderDetails;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.service.spec.SipOrderExecutionServiceSpec;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.model.SipFrequencyDetails;
import in.codifi.orders.ws.model.SipOrderRestReq;
import in.codifi.orders.ws.model.SipScripInfo;
import in.codifi.orders.ws.service.SipOrderRestServiceOdin;
import io.quarkus.logging.Log;

@Component
public class SipOrderExecutionService implements SipOrderExecutionServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	SipOrderRestServiceOdin restServiceOdin;

	/**
	 * Method to execute SIP place orders
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> sipPlaceOrder(List<SipOrderDetails> orderDetails, ClinetInfoModel info) {
		try {
			/** Verify session **/
			Log.info("Orders  userId - " + info.getUserId());
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDEyNTA4M0U4NEVFODZEQTkzNDdCOTY2Q0VDNkQ0IiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjkwMDUwNTk5LCJpYXQiOjE2OTAwMjc0MDh9.U6Je4jIb7mGYQ29ffQLOpEhO5SyfQl4I8OYiGxdJFSs";
			Log.info("Orders  userSession - " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponseForList();

			List<GenericResponse> response = executeSipOrdersSimultaneously(orderDetails, userSession, info);
			return prepareResponse.prepareSuccessRespForList(response);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to execute Sip Orders Simultaneously
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<GenericResponse> executeSipOrdersSimultaneously(List<SipOrderDetails> ordersReq, String userSession,
			ClinetInfoModel info) {

		List<Callable<GenericResponse>> tasks = new ArrayList<>();

		for (SipOrderDetails orderDetails : ordersReq) {
			Callable<GenericResponse> task = () -> {
				/** Validate Request **/
				if (!validateExecuteOrderReq(orderDetails))
					return prepareResponse.prepareFailedResponseBody(AppConstants.INVALID_PARAMETER);

				if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.NSE)) {
					orderDetails.setExchange(AppConstants.NSE_EQ);
				} else if (orderDetails.getExchange().equalsIgnoreCase(AppConstants.BSE)) {
					orderDetails.setExchange(AppConstants.BSE_EQ);
				} else {
					return prepareResponse.prepareFailedResponseBody(AppConstants.INVALID_EXCH);
				}

				String req = prepareSipOrderReq(orderDetails, userSession, info);
				if (StringUtil.isNullOrEmpty(req))
					return prepareResponse.prepareFailedResponseBody(AppConstants.FAILED_STATUS);

				return restServiceOdin.executeSipOrder(req, userSession, info.getUserId());

			};
			tasks.add(task);
		}
		return tasks.stream().map(callable -> {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Method to validate sip place order request
	 * 
	 * @param orderDetails
	 * @return
	 */
	private boolean validateExecuteOrderReq(SipOrderDetails orderDetails) {
		if (StringUtil.isNotNullOrEmpty(orderDetails.getExchange())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getSymbol())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getQtyOrValue())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getTransType())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getCapPrice())
				&& StringUtil.isNotNullOrEmpty(orderDetails.getToken())) {
			return true;
		}
		return false;
	}

	/**
	 * Method to prepare SipOrder Req
	 * 
	 * @param orderDetails
	 * @return
	 */
	private String prepareSipOrderReq(SipOrderDetails orderDetails, String userSession, ClinetInfoModel info) {
		SipOrderRestReq req = new SipOrderRestReq();
		ObjectMapper mapper = new ObjectMapper();
		String placeSipOrder = "";
		try {

			SipScripInfo sipScripInfo = new SipScripInfo();

			sipScripInfo.setExchange(orderDetails.getExchange());
			sipScripInfo.setExpiryDate(orderDetails.getExpiryDate());
			sipScripInfo.setOptionType(orderDetails.getOptionType());
			sipScripInfo.setScripToken(Integer.parseInt(orderDetails.getToken()));
			sipScripInfo.setSeries(orderDetails.getSeries());
			sipScripInfo.setStrikePrice(orderDetails.getStrikePrice());
			sipScripInfo.setSymbol(orderDetails.getSymbol());
			req.setSipScripInfo(sipScripInfo);

			SipFrequencyDetails frequencyDetails = new SipFrequencyDetails();
			frequencyDetails.setFrequencyDays(orderDetails.getFrequencyDays());
			frequencyDetails.setFrequencyMonthlyOption(orderDetails.getFrequencyMonthlyOption());
			frequencyDetails.setFrequencySpecificDate(orderDetails.getFrequencySpecificDate());
			frequencyDetails.setFrequencyStartDate(orderDetails.getFrequencyStartDate());
			frequencyDetails.setFrequencyType(orderDetails.getFrequencyType());
			frequencyDetails.setNoOfInstallments(orderDetails.getNoOfInstallments());
			req.setSipFrequencyDetails(frequencyDetails);

			req.setBasketName(orderDetails.getBasketName());
			req.setCapPrice(orderDetails.getCapPrice());
			req.setCompanyName(orderDetails.getCompanyName());
			req.setInvestedBy(orderDetails.getInvestedBy());
			req.setLtp(orderDetails.getLtp());
			req.setQtyOrValue(orderDetails.getQtyOrValue());
			req.setSipId(orderDetails.getSipId());
			req.setStatus(orderDetails.getStatus());
			req.setTransType(orderDetails.getTransType());

			placeSipOrder = mapper.writeValueAsString(req);

		} catch (Exception e) {
			Log.error(e);
		}
		return placeSipOrder;
	}

	/**
	 * Method to sip Order Book
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> sipOrderBook(ClinetInfoModel info) {
		try {
			/** Verify session **/
			Log.info("Orders  userId - " + info.getUserId());
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFGQTlBOTg1MDVGRjI3OTg0NjA0NzgyNjdDMjU3IiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjkwMjIzMzk5LCJpYXQiOjE2OTAxNzU3NDl9.mOK6gB65etQh3Cnlet_si5uhY2N8-oMgaB1vMKYzeDw";

			Log.info("Orders  userSession - " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return restServiceOdin.executeSipOrderBook(userSession, info);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to cancel sip Order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> cancelSipOrder(SipOrderDetails orderDetails, ClinetInfoModel info) {
		try {
			/** Verify session **/
			Log.info("Orders  userId - " + info.getUserId());
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExMTU2MCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDExMEQ3MDlDNkU4MzczRjAzNjQ5Qzk4OTIxRkNDIiwidXNlckNvZGUiOiJBRVVQQSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjkwMzk2MTk5LCJpYXQiOjE2OTAzNjcwNjJ9.s-gXWazQWP-XFJanpN_pnfIqO4pF94uIfp5vqUcpJxw";
			Log.info("Orders  userSession - " + userSession);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			return restServiceOdin.cancelSipOrder(orderDetails, userSession, info);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

}

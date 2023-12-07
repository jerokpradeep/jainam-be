package in.codifi.holdings.service;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.common.util.Encode;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.config.HazelcastConfig;
import in.codifi.holdings.config.RestServiceProperties;
import in.codifi.holdings.entity.logs.RestAccessLogModel;
import in.codifi.holdings.model.request.EdisHoldModel;
import in.codifi.holdings.model.response.GenericResponse;
import in.codifi.holdings.repository.AccessLogManager;
import in.codifi.holdings.service.spec.EdisServiceSpec;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.AppUtil;
import in.codifi.holdings.utility.PrepareResponse;
import in.codifi.holdings.utility.StringUtil;
import in.codifi.holdings.ws.service.EdisRestService;
import in.codifi.holdings.ws.service.HoldingsRestService;
import io.quarkus.logging.Log;

@Component
public class EdisService implements EdisServiceSpec {

	@Inject
	EdisRestService restService;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	RestServiceProperties restProp;
	@Inject
	AccessLogManager accessLogManager;
	@Inject
	HoldingsRestService holdingsRestService;

	/**
	 * Method to prepare scrip details for edis URL formation
	 * 
	 * @author Nesan
	 * 
	 * @param edisSummaryResModel
	 */
	private String prepareScripDetailReq(List<EdisHoldModel> edisHoldList) {
		ObjectMapper om = new ObjectMapper();

		String output = null;
		try {
//			for (EdisHoldModel model : edisHoldList) {
//				reModel = new EdisReModel();
//				reModel.setIsin(model.getIsin());
//				reModel.setIsinName(model.getIsinName());
//				reModel.setQty(model.getQty());
//				reModel.setSettlmtCycle(model.getSettlmtCycle());
//				reEdisList.add(reModel);
//			}
			output = om.writeValueAsString(edisHoldList);
		} catch (Exception e) {
			Log.error(e);
		}
		return output;

	}

	/**
	 * method to get edis redirect URL take some variables from cache
	 * 
	 * @author Nesan
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RestResponse<GenericResponse> getRedirectUrl(List<EdisHoldModel> model, String userId) {
		boolean isvalid = validateEdisPayload(model);
		if (isvalid) {
			String req = prepareScripDetailReq(model);
			if (StringUtil.isNullOrEmpty(req))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			String url = prepareRedirectUrlUsingcache(model, req, userId);
			if (StringUtil.isNotNullOrEmpty(url)) {
				JSONObject jobj = new JSONObject();
				jobj.put("redirectURL", url);
				return prepareResponse.prepareSuccessResponseObject(jobj);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}

	}

	private boolean validateEdisPayload(List<EdisHoldModel> models) {
		boolean status = false;

		for (EdisHoldModel model : models) {

			if (StringUtil.isNotNullOrEmpty(model.getIsin()) && StringUtil.isNotNullOrEmpty(model.getIsinName())
					&& model.getQty() > 0) {
				status = true;
			}
		}
		return status;

	}

	/**
	 * Method to prepare Redirect url using cache variable
	 * 
	 * @author Nesan
	 */
	private String prepareRedirectUrlUsingcache(List<EdisHoldModel> model, String req, String userId) {
		System.out.println("clientCode " + userId);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ClientDetailsModel cacheModel = HazelcastConfig.getInstance().getClientDetails().get(userId);// TODO
		String redirectUrl = null;
		System.out.println("clientCode " + cacheModel);
		if (cacheModel != null) {
			try {
//				String clientCode = "AEUPA";// TODO Take from cache
//				String dpId = "048800";// TODO
//				String clientId = "1204880000363785";// TODO
//				String depository = "CDSL";// TODO

				String clientCode = cacheModel.getDpAccountNumber().get(0).getEdisClientCode();
				String dpId = cacheModel.getDpAccountNumber().get(0).getDpId();
//				dpId = dpId.substring(2, dpId.length());
				String clientId = cacheModel.getDpAccountNumber().get(0).getBoId();
				String depository = cacheModel.getDpAccountNumber().get(0).getRepository();

				String groupId = "HO";// TODO
				String productType = "D";// TODO
				StringBuilder sb = new StringBuilder();

				String toAppend = AppConstants.USERCODE + clientCode + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.MANAGER_IP + AppConstants.MANAGER_IP_VALUE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.SESSION_ID + AppConstants.SESSION_ID_VALUE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.CHANNEL + AppConstants.MOB + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				if (depository.equalsIgnoreCase("CDSL")) {
					toAppend = AppConstants.ISIN + "" + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
					toAppend = AppConstants.ISIN_NAME + "" + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				} else {
					toAppend = AppConstants.ISIN + model.get(0).getIsin() + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
					toAppend = AppConstants.ISIN_NAME + model.get(0).getIsinName() + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				}
				toAppend = AppConstants.EXCHANGE_CD + AppConstants.EXCHANGE_CD_VALUE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.PRODUCT + AppConstants.INT_ONE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.INSTRUMENT + AppConstants.INSTRUMENT_VALUE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				if (depository.equalsIgnoreCase("CDSL")) {
					dpId = dpId.substring(2, dpId.length());
					toAppend = AppConstants.QUANTITY + "" + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
					toAppend = AppConstants.DP_ID + dpId + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				} else {
					toAppend = AppConstants.QUANTITY + model.get(0).getQty() + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
					toAppend = AppConstants.DP_ID + dpId + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				}
				toAppend = AppConstants.CLIENTID_ID + clientId + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.DEPOSITORY + depository + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.PRODUCT_CODE + AppConstants.PRODUCT_CODE_VALUE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.MARKET_SEG_ID + AppConstants.INT_ONE + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				if (depository.equalsIgnoreCase("CDSL")) {
					toAppend = AppConstants.SCRIPDETAILS + Encode.encodeString(req) + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				} else {
					toAppend = AppConstants.SCRIPDETAILS + AppConstants.SYMBOL_AND;
					sb.append(toAppend);
				}

//				toAppend = AppConstants.SCRIPDETAILS + AppConstants.SYMBOL_AND;

//			toAppend = AppConstants.SCRIPDETAILS
//					+ "%5B%7B%22ISIN%22%3A%22INE012A01025%22%2C%22Quantity%22%3A0%2C%22ISINName%22%3A%22ACC+LIMITED%22%2C%22SettlmtCycle%22%3A%22T1%22%7D%5D"
//					+ AppConstants.SYMBOL_AND;

//				toAppend = AppConstants.USER_ID + clientId + AppConstants.SYMBOL_AND;
//				sb.append(toAppend);
				toAppend = AppConstants.USER_ID + userId + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.GROUP_ID + groupId + AppConstants.SYMBOL_AND;
				sb.append(toAppend);
				toAppend = AppConstants.PRODUCT_TYPE + productType + AppConstants.SYMBOL_AND;
				sb.append(toAppend);

				toAppend = AppConstants.AMO.toLowerCase() + AppConstants.SYMBOL_EQUAL + AppConstants.CHAR_N.toString()
						+ AppConstants.SYMBOL_AND;
//			toAppend = AppConstants.AMO.toLowerCase() + AppConstants.SYMBOL_EQUAL
//					+ (model.getOrderType() == "AMO" ? AppConstants.CHAR_Y.toString() : AppConstants.CHAR_N.toString())
//					+ AppConstants.SYMBOL_AND;//TODO
				sb.append(toAppend);
				toAppend = AppConstants.SETTLMT_CYCLE + AppConstants.SETTLMT_CYCLE_VALUE;
				sb.append(toAppend);
				System.out.println("Before encode " + sb);

				String encodedUrl = Base64.getUrlEncoder().encodeToString(sb.toString().getBytes());
				sb.setLength(AppConstants.INT_ZERO);
//				sb.insert(AppConstants.INT_ZERO, restProp.getReDirectUrl());
				sb.insert(AppConstants.INT_ZERO, "https://edis.cdslindia.com/eDIS/VerifyDIS/");
				sb.append(encodedUrl);

				accessLogModel.setMethod("redirectUrl");
				accessLogModel.setModule(AppConstants.MODULE_HOLDINGS);
				accessLogModel.setUrl(restProp.getReDirectUrl());
				accessLogModel.setUserId(userId);
				accessLogModel.setReqBody(encodedUrl);
				accessLogModel.setInTime(new Timestamp(new Date().getTime()));

				System.out.println("After encode  " + sb);
				redirectUrl = sb.toString();

				accessLogModel.setResBody(redirectUrl);
				insertRestAccessLogs(accessLogModel);
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return redirectUrl;
	}

	/**
	 * Method to get EdisSummary
	 * 
	 * @author Gowthaman
	 * @return
	 */
	public RestResponse<GenericResponse> getEdisSummary(ClinetInfoModel info) {
//		if (StringUtil.isNotNullOrEmpty(req.getProductType()) && StringUtil.isNotNullOrEmpty(req.getSegId())
//				&& StringUtil.isNotNullOrEmpty(req.getSellQty())
//				&& StringUtil.isNotNullOrEmpty(req.getSummaryMktSegId())
//				&& StringUtil.isNotNullOrEmpty(req.getToken())) {
//
//			String userSession = AppUtil.getUserSession(info.getUserId());
////			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxODFDQTYzRTNGRjUwRDdFNDc4RUJFMzgzQUY4QSIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NTc1Mjk5OSwiaWF0IjoxNjk1NzQzOTY4fQ.0LUvc_JBZJaMpkC5_EyXwY-_GaeMMSuwBa8sf6KlIbs";
//			if (StringUtil.isNullOrEmpty(userSession))
//				return prepareResponse.prepareUnauthorizedResponse();
//
//			return holdingsRestService.getEdisSummary(req, info, userSession);
//
//		} else {
//			prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		}

		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFFQ0IxQjIzM0U0QzZFODE3QkIwMTFBQjAyNEZEIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMDI0NTc5OSwiaWF0IjoxNzAwMjIwNjkzfQ.BbsTqinZsMsEUCc7eLvGEN-4DgbPbaWsVLSzblTxCOQ";
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return holdingsRestService.getEdisSummarys(info, userSession);

	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gowthaman M
	 * @param accessLogModel
	 */
	public void insertRestAccessLogs(RestAccessLogModel accessLogModel) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					accessLogManager.insertRestAccessLog(accessLogModel);
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

}

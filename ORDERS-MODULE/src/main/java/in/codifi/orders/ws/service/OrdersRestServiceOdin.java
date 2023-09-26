package in.codifi.orders.ws.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

//import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ContractMasterModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.config.RestServiceProperties;
import in.codifi.orders.model.response.GenericResponse;
import in.codifi.orders.model.transformation.GenericOrderBookResp;
import in.codifi.orders.model.transformation.GenericOrderResp;
import in.codifi.orders.model.transformation.GenericTradeBookResp;
import in.codifi.orders.utility.AppConstants;
import in.codifi.orders.utility.AppUtil;
import in.codifi.orders.utility.CodifiUtil;
import in.codifi.orders.utility.PrepareResponse;
import in.codifi.orders.utility.StringUtil;
import in.codifi.orders.ws.model.OrderBookFailModel;
import in.codifi.orders.ws.model.OrderBookSuccess;
import in.codifi.orders.ws.model.PlaceOrderReqModel;
import in.codifi.orders.ws.model.TradeBookSuccess;
import in.codifi.ws.model.odin.ErrorModel;
import in.codifi.ws.model.odin.OrderResponse;
import in.codifi.ws.service.spec.OrdersRestSpecOdin;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OrdersRestServiceOdin {

//	@Inject
//	@RestClient
	OrdersRestSpecOdin rest;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AppUtil appUtil;

	/**
	 * 
	 * Method to execute place order
	 * 
	 * @author nesan
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public RestResponse<GenericResponse> executePlaceorder(String authHeader, PlaceOrderReqModel req)
			throws IOException {
		ObjectMapper om = new ObjectMapper();
		OrderResponse resp = null;
		try {
			String jobj = om.writeValueAsString(req);
			resp = rest.executePlaceorder(authHeader, "He55zrpvWn9ml0fkGv1Zq5GljGVBxUv5NFieVGAa", jobj);
			if (resp != null) {
				/* Prepare generic response */
				GenericOrderResp genericOrderResp = prepareAsGenericResponse(resp);
				if (genericOrderResp != null)
					return prepareResponse.prepareResponse(resp);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (WebApplicationException webApplicationException) {
			Response response = webApplicationException.getResponse();
			System.out.println(response.getStatus());
			int responseCode = response.getStatus();
			if (responseCode == 401)
				return prepareResponse.prepareUnauthorizedResponse();

			BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) response.getEntity()));
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			ErrorModel errorModel = om.readValue(sb.toString(), ErrorModel.class);
			Log.error(errorModel.getMessage());
			System.out.println(sb.toString());
			return prepareResponse.prepareFailedResponse(errorModel.getMessage());
		} catch (Exception e) {
			Log.error(e);
		}

		return null;
	}

	/**
	 * Method to create response as in generic format for sucessfull place order
	 * 
	 * @param resp
	 */
	private GenericOrderResp prepareAsGenericResponse(OrderResponse resp) {
		GenericOrderResp genericOrderResp = new GenericOrderResp();

		try {
			genericOrderResp.setOrderNo(resp.getData().getOrderId());
			genericOrderResp.setRequestTime(null);// TODO
		} catch (Exception e) {
			Log.error(e);
		}
		return genericOrderResp;
	}

	/**
	 * Method to get order book
	 * 
	 * @author Gowthaman M
	 * @param req
	 * @return
	 **/
	public RestResponse<GenericResponse> getOrderBookInfo(String request, String userId) {
		ObjectMapper mapper = new ObjectMapper();
		List<OrderBookSuccess> success = null;
		OrderBookFailModel fail = null;
		Log.info("Order book req-" + request);
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getOrderBookUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.TEXT_PLAIN);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.startsWith("[{")) {
						success = mapper.readValue(output,
								mapper.getTypeFactory().constructCollectionType(List.class, OrderBookSuccess.class));
						List<GenericOrderBookResp> extract = bindOrderBookData(success, userId);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else {
						fail = mapper.readValue(output, OrderBookFailModel.class);
						if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
							return prepareResponse.prepareFailedResponseForRestService(fail.getEmsg());
					}
				}
			} else {
				System.out.println("Error Connection in Order book api");
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					fail = mapper.readValue(output, OrderBookFailModel.class);
					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
						return prepareResponse.prepareFailedResponseForRestService(fail.getEmsg());
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to map Generic OrderBook Response with Odin core API values response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<GenericOrderBookResp> bindOrderBookData(List<OrderBookSuccess> success, String userId) {
		List<GenericOrderBookResp> responseList = new ArrayList<>();
		try {
			for (OrderBookSuccess model : success) {
				GenericOrderBookResp extract = new GenericOrderBookResp();
				String restExch = model.getExch();
				String exch = "";

				if (restExch.equalsIgnoreCase("NSE_EQ")) {
					exch = "NSE";
				} else if (restExch.equalsIgnoreCase("BSE_EQ")) {
					exch = "BSE";
				} else if (restExch.equalsIgnoreCase("NSE_FO")) {
					exch = "NFO";
				}

				String token = model.getScripToken();
				ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
				if (coModel != null) {
					String scripName = StringUtil.isNotNullOrEmpty(coModel.getFormattedInsName())
							? coModel.getFormattedInsName()
							: "";
					extract.setFormattedInsName(scripName);
				}

				extract.setOrderNo(model.getOrderId());
				extract.setUserId(userId);
				extract.setActId(userId);
				extract.setExchange(exch);
				extract.setTradingSymbol(model.getSymbol());
				extract.setQty(model.getTotalQty());
				extract.setTransType(model.getTransType());
				extract.setRet(model.getValidity());
				extract.setToken(model.getScripToken());
				extract.setLotSize(model.getMktLot());
//				extract.setTickSize(model.getTi()); //TODO
				extract.setPrice(model.getOrderPrice());
//				extract.setRPrice(model.getRprc()); //TODO
//				extract.setAvgTradePrice(model.getAvgprc()); //TODO
				extract.setDisclosedQty(model.getDisclosedQty());
				extract.setOrderStatus(model.getStatus());
//				extract.setFillShares(model.getFillshares()); //TODO
				extract.setExchUpdateTime(model.getExchTime());
				extract.setExchOrderId(model.getExchOrderNo()); // TODO
//				extract.setRQty(model.getRqty()); //TODO
				extract.setRejectedReason(model.getErrorReason());
				extract.setTriggerPrice(model.getTriggerPrice());
//				extract.setMktProtection(model.getMktProtection()); //TODO
//				extract.setTarget(model.getBlPrc()); //TODO
//				extract.setStopLoss(model.getBpPrc()); //TODO
//				extract.setTrailingPrice(model.getTrailPrc()); //TODO
				extract.setOrderTime(model.getOrderTime());
				boolean isAmo = model.isAmo();
				extract.setProduct(HazelcastConfig.getInstance().getProductTypes().get(model.getProductType()));
				extract.setPriceType(HazelcastConfig.getInstance().getPriceTypes().get(model.getOrderType()));
				if (isAmo) {
					extract.setOrderType(AppConstants.AMO);
				}
				responseList.add(extract);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return responseList;
	}

	/**
	 * Method to get trade book details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getTradeBookInfo(String request) {
		ObjectMapper mapper = new ObjectMapper();
		List<TradeBookSuccess> success = null;
//		TradeBookFailModel fail = null;
		Log.info("Trade book req-" + request);
		try {
			CodifiUtil.trustedManagement();
			URL url = new URL(props.getTradeBookUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.TEXT_PLAIN);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.startsWith("[{")) {
						success = mapper.readValue(output,
								mapper.getTypeFactory().constructCollectionType(List.class, TradeBookSuccess.class));
						List<GenericTradeBookResp> extract = bindTradeBookData(success);
						return prepareResponse.prepareSuccessResponseObject(extract);
					} else {
//						fail = mapper.readValue(output, TradeBookFailModel.class);
//						if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
//							return prepareResponse.prepareFailedResponseForRestService(fail.getEmsg());
					}
				}
			} else {
				System.out.println("Error Connection in Order book api");
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
//				if (StringUtil.isNotNullOrEmpty(output)) {
//					fail = mapper.readValue(output, TradeBookFailModel.class);
//					if (StringUtil.isNotNullOrEmpty(fail.getEmsg()))
//						return prepareResponse.prepareFailedResponseForRestService(fail.getEmsg());
//				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to map Generic TradeBookResp layer with ODIN core API values api
	 * response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<GenericTradeBookResp> bindTradeBookData(List<TradeBookSuccess> success) {
		List<GenericTradeBookResp> responseList = new ArrayList<>();
		try {
			for (TradeBookSuccess model : success) {
				GenericTradeBookResp extract = new GenericTradeBookResp();
				String exch = model.getExch();
				String token = model.getScripToken();
//				ContractMasterModel coModel = AppUtil.getContractMaster(exch, token);
				ContractMasterModel coModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
				if (coModel != null) {
					String scripName = StringUtil.isNotNullOrEmpty(coModel.getFormattedInsName())
							? coModel.getFormattedInsName()
							: "";
					extract.setFormattedInsName(scripName);
				}

				extract.setOrderNo(model.getOrderId());
//				extract.setUserId(model.getUid()); //TODO
//				extract.setActId(model.getActid()); //TODO
				extract.setExchange(model.getExch());
//				extract.setRet(model.getRet()); //TODO
//				extract.setFillId(model.getFlid()); //TODO
//				extract.setFillTime(model.getFltm()); //TODO
				extract.setTransType(model.getTransType());
				extract.setTradingSymbol(model.getSymbol());
				extract.setQty(model.getTradeQty()); // TODO
				extract.setToken(model.getScripToken());
//				extract.setFillshares(model.getFillshares()); //TODO
//				extract.setFillqty(model.getFlqty()); //TODO
//				extract.setPricePrecision(model.getPp()); //TODO
//				extract.setLotSize(model.getLs()); //TODO
//				extract.setTickSize(model.getTi()); //TODO
//				extract.setPrice(model.getPrc()); //TODO
//				extract.setPrcftr(model.getPrcftr()); //TODO
//				extract.setFillprc(model.getFlprc()); //TODO
//				extract.setExchUpdateTime(model.getExchTm()); //TODO
				extract.setExchOrderId(model.getExchOrderNo());
				extract.setOrderTime(model.getTradeTime()); // TODO
//				extract.setPriceType(appUtil.getPriceType(model.getPrctyp())); //TODO

				String productType = "";
				if (StringUtil.isNotNullOrEmpty(model.getProductType())
						&& model.getProductType().equalsIgnoreCase(AppConstants.REST_BRACKET)) {
					extract.setOrderType(AppConstants.BRACKET);
					if (HazelcastConfig.getInstance().getProductTypes().containsKey(AppConstants.PRODUCT_TYPE)) {
						String productMasterModels = HazelcastConfig.getInstance().getProductTypes()
								.get(AppConstants.PRODUCT_TYPE);
						if (productMasterModels.equalsIgnoreCase(AppConstants.REST_PRODUCT_MIS)) {
							productType = HazelcastConfig.getInstance().getProductTypes().get(model.getProductType());
						}
					}
//					extract.setProduct(appUtil.getProductType(AppConstants.REST_PRODUCT_MIS));
					extract.setProduct(productType);

				} else if (StringUtil.isNotNullOrEmpty(model.getProductType())
						&& model.getProductType().equalsIgnoreCase(AppConstants.REST_COVER)) {
					extract.setOrderType(AppConstants.COVER);
					if (HazelcastConfig.getInstance().getProductTypes().containsKey(AppConstants.PRODUCT_TYPE)) {
						String productMasterModels = HazelcastConfig.getInstance().getProductTypes()
								.get(AppConstants.PRODUCT_TYPE);
						if (productMasterModels.equalsIgnoreCase(AppConstants.REST_PRODUCT_MIS)) {
							productType = HazelcastConfig.getInstance().getProductTypes().get(model.getProductType());
						}
					}
//					extract.setProduct(appUtil.getProductType(AppConstants.REST_PRODUCT_MIS));
					extract.setProduct(productType);

				} else {
					extract.setOrderType(AppConstants.REGULAR);

					if (HazelcastConfig.getInstance().getProductTypes().containsKey(AppConstants.PRODUCT_TYPE)) {
						String productMasterModels = HazelcastConfig.getInstance().getProductTypes()
								.get(AppConstants.PRODUCT_TYPE);
						if (productMasterModels.equalsIgnoreCase(model.getProductType())) {
							productType = HazelcastConfig.getInstance().getProductTypes().get(model.getProductType());
						}
					}
//					extract.setProduct(appUtil.getProductType(model.getPrd()));
					extract.setProduct(productType);

				}

				responseList.add(extract);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException();
		}
		return responseList;
	}

}

//package in.codifi.client.ws.service;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
//
//import org.jboss.resteasy.reactive.RestResponse;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import in.codifi.cache.model.ClinetInfoModel;
//import in.codifi.client.config.RestServiceProperties;
//import in.codifi.client.entity.logs.RestAccessLogModel;
//import in.codifi.client.model.request.TicketRiseReq;
//import in.codifi.client.model.response.GenericResponse;
//import in.codifi.client.repository.AccessLogManager;
//import in.codifi.client.utility.AppConstants;
//import in.codifi.client.utility.CodifiUtil;
//import in.codifi.client.utility.PrepareResponse;
//import io.quarkus.logging.Log;
//
//@ApplicationScoped
//public class ErpRestService {
//
//	@Inject
//	PrepareResponse prepareResponse;
//	@Inject
//	RestServiceProperties props;
//	@Inject
//	AccessLogManager accessLogManager;
//
//	public RestResponse<GenericResponse> raiseTicket(TicketRiseReq req, ClinetInfoModel info) {
//		ObjectMapper mapper = new ObjectMapper();
//		String output = null;
//		try {
//			CodifiUtil.trustedManagement();
//			String request = props.getRiseTicket() + AppConstants.SUBJECT + AppConstants.SYMBOL_EQUAL + req.getSubject()
//					+ AppConstants.SYMBOL_AND + AppConstants.RAISED_BY + AppConstants.SYMBOL_EQUAL + req.getUserId()
//					+ AppConstants.SYMBOL_AND + AppConstants.DESCRIPTION + req.getDescription();
//			URL url = new URL(request);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod(AppConstants.GET_METHOD);
//			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.ERP_AUTHORIZATION_TOKEN);
//			conn.setDoOutput(true);
//			int responseCode = conn.getResponseCode();
//			System.out.println("raise Ticket response Code--" + responseCode);
//
//			RestAccessLogModel accessLogModel = new RestAccessLogModel();
//			accessLogModel.setMethod("raiseTicket");
//			accessLogModel.setModule(AppConstants.MODULE_CLIENT);
//			accessLogModel.setUrl(url.toString());
//			accessLogModel.setReqBody(url.toString());
//			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
//
//			BufferedReader bufferedReader;
//			if (responseCode == 401) {
//				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
//				insertRestAccessLogs(accessLogModel);
//				return prepareResponse.prepareUnauthorizedResponse();
//			} else if (responseCode == 200) {
//				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//				output = bufferedReader.readLine();
//				accessLogModel.setResBody(output);
//				insertRestAccessLogs(accessLogModel);
//			} else {
//				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//				output = bufferedReader.readLine();
//				accessLogModel.setResBody(output);
//				insertRestAccessLogs(accessLogModel);
//				return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
//			}
//
//		} catch (Exception e) {
//			Log.error(e);
//		}
//
//		return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
//	}
//
//	/**
//	 * 
//	 * Method to insert rest service access logs
//	 * 
//	 * @author Dinesh Kumar
//	 *
//	 * @param accessLogModel
//	 */
//	public void insertRestAccessLogs(RestAccessLogModel accessLogModel) {
//
//		ExecutorService pool = Executors.newSingleThreadExecutor();
//		pool.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					accessLogManager.insertRestAccessLog(accessLogModel);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		pool.shutdown();
//	}
//
//}

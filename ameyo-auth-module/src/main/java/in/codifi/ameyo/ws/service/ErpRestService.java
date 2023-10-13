package in.codifi.ameyo.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.ameyo.entity.logs.RestAccessLogModel;
import in.codifi.ameyo.model.response.ErpRedirectResp;
import in.codifi.ameyo.repository.AccessLogManager;
import in.codifi.ameyo.utility.AppConstants;
import in.codifi.ameyo.utility.CodifiUtil;
import in.codifi.ameyo.utility.PrepareResponse;
import in.codifi.ameyo.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ErpRestService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AccessLogManager accessLogManager;

	public String getErpAuthorization(String request, String userId) {
		ErpRedirectResp response = new ErpRedirectResp();
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		try {
			accessLogModel.setMethod("getErpAuthorization");
			accessLogModel.setModule(AppConstants.MODULE_EMP_LOGIN);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(request);
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.GET_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON);
			conn.setDoOutput(true);
			int responseCode = conn.getResponseCode();
			System.out.println("get Erp Authorization responseCode -- " + responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in Odin get Erp Authorization api");
				accessLogModel.setResBody(AppConstants.UNAUTHORIZED);
				insertRestAccessLogs(accessLogModel);
//				return prepareResponse.prepareUnauthorizedResponse();
				return AppConstants.UNAUTHORIZED;
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("Odin get Erp Authorization response" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					if (output.contains("Authentication Success")) {
//						response = mapper.readValue(output, ErpRedirectResp.class);
//						return prepareResponse.prepareSuccessMessage(response.getMessage().getError().getMessage());
						return "Authentication Success";
					}
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					Log.error("getAlert output -- " + output);
//					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
					return AppConstants.FAILED_STATUS;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("get Erp Authorization -- " + e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

	/**
	 * 
	 * Method to insert rest service access logs
	 * 
	 * @author Dinesh Kumar
	 *
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
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

}

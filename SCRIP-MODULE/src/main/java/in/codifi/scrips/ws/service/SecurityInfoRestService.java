package in.codifi.scrips.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.scrips.config.RestProperties;
import in.codifi.scrips.entity.logs.RestAccessLogModel;
import in.codifi.scrips.model.response.GenericResponse;
import in.codifi.scrips.repository.AccessLogManager;
import in.codifi.scrips.utility.AppConstants;
import in.codifi.scrips.utility.CodifiUtil;
import in.codifi.scrips.utility.PrepareResponse;
import in.codifi.scrips.utility.StringUtil;
import in.codifi.scrips.ws.model.SecurityInfoRestReq;
import in.codifi.scrips.ws.model.SecurityInfoRestSuccessRespModel;
import in.codifi.scrips.ws.remodeling.SecurityInfoRemodeling;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SecurityInfoRestService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestProperties props;
	@Inject
	SecurityInfoRemodeling securityRemodeling;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to get security info
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> getSecurityInfo(String userSession, SecurityInfoRestReq model,
			ClinetInfoModel info) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		SecurityInfoRestSuccessRespModel securityInfoModel = new SecurityInfoRestSuccessRespModel();
		try {
			String request = mapper.writeValueAsString(model);
			accessLogModel.setMethod("securityInfo");
			accessLogModel.setModule(AppConstants.MODULE_SCRIPS);
			accessLogModel.setReqBody(mapper.writeValueAsString(model));
			accessLogModel.setUserId(info.getUserId());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			CodifiUtil.trustedManagement();
			URL url = new URL(props.getGetSecurityInfo());
			accessLogModel.setUrl(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
			conn.setRequestProperty(AppConstants.AUTHORIZATION, AppConstants.BEARER_WITH_SPACE + userSession);
			conn.setRequestProperty(AppConstants.X_API_KEY_NAME, props.getXApiKey());
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.toString().getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			System.out.println(responseCode);
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			if (responseCode == 401) {
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();

			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				System.out.println("output--" + output);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					securityInfoModel = mapper.readValue(output, SecurityInfoRestSuccessRespModel.class);
					/** Bind the response to generic response **/
					if (securityInfoModel.isStatus()) {
						if (StringUtil.isListNotNullOrEmpty(securityInfoModel.getResult().getSecInfo())) {
//							SecurityInfoRespModel extract = securityRemodeling.bindSecurityInfo(securityInfoModel);
							SecurityInfoRestSuccessRespModel extract = securityRemodeling.bindExtractSecurityInfo(securityInfoModel);
							return prepareResponse.prepareSuccessResponseObject(extract.getResult());
						} else {
							return prepareResponse.prepareFailedResponseForRestService(AppConstants.NOT_FOUND);
						}

					} else if (!securityInfoModel.isStatus()) {
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
					}
				}
			} else {
				System.out.println("Error Connection in scrips api. Rsponse code - " + responseCode);
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					securityInfoModel = mapper.readValue(output, SecurityInfoRestSuccessRespModel.class);
					if (securityInfoModel.isStatus())
						return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				} else {
					return prepareResponse.prepareFailedResponseForRestService(AppConstants.FAILED_STATUS);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		accessLogModel.setResBody("Failed");
		insertRestAccessLogs(accessLogModel);
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to insert rest service access logs
	 * 
	 * @author Gpwthaman M
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

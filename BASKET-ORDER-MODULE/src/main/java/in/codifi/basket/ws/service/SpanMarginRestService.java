package in.codifi.basket.ws.service;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.basket.config.RestServiceProperties;
import in.codifi.basket.entity.logs.RestAccessLogModel;
import in.codifi.basket.repository.AccessLogManager;
import in.codifi.basket.utility.AppConstants;
import in.codifi.basket.utility.PrepareResponse;
import in.codifi.basket.utility.StringUtil;
import in.codifi.basket.ws.model.SpanMarginRestResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SpanMarginRestService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to connect the API
	 * 
	 * @author SOWMIYA
	 * @param baseUrl
	 * @return
	 */
	public String getSpanMargin(String request) {
		Log.info("span margin Request" + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		String response = AppConstants.FAILED_STATUS;
		try {
			accessLogModel.setMethod("getSpanMargin");
			accessLogModel.setModule(AppConstants.MODULE_BASKET);
			accessLogModel.setUrl(props.getSpanMarginUrl());
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));
			URL url = new URL(props.getSpanMarginUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.TEXT_PLAIN);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			BufferedReader bufferedReader;
			String output = null;
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			if (conn.getResponseCode() == 401) {
				accessLogModel.setResBody("Unauthorized");
				Log.error("Unauthorized error in span margin api");
			} else if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				Log.info("span margin response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {

					ObjectMapper mapper = new ObjectMapper();
					SpanMarginRestResp spanResponseModel = mapper.readValue(output, SpanMarginRestResp.class);
					if (StringUtil.isNotNullOrEmpty(spanResponseModel.getStat())
							&& spanResponseModel.getStat().equalsIgnoreCase(AppConstants.REST_STATUS_OK)) {

						double spanTrade = 0;
						double expoTrade = 0;
						double totalSpan = 0;
						if (StringUtil.isNotNullOrEmpty(spanResponseModel.getSpan_trade())) {
							spanTrade = Double.valueOf(spanResponseModel.getSpan_trade());
						}
						if (StringUtil.isNotNullOrEmpty(spanResponseModel.getExpo_trade())) {
							expoTrade = Double.valueOf(spanResponseModel.getExpo_trade());
						}
						totalSpan = spanTrade + expoTrade;
						return String.valueOf(totalSpan);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;

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
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

}

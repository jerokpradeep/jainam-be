package in.codifi.funds.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.funds.config.HazelcastConfig;
import in.codifi.funds.config.PaymentsProperties;
import in.codifi.funds.entity.logs.RestAccessLogModel;
import in.codifi.funds.repository.AccessLogManager;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.BankDetailsRestResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class RazorpayRestService {

	@Inject
	PaymentsProperties props;
	@Inject
	AccessLogManager accessLogManager;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * Method to get bank code from IFSC Details
	 * 
	 * @author Dinesh Kumar
	 * @param ifsc
	 * @return
	 */
	public BankDetailsRestResp getBankDetails(String ifsc) {
		BankDetailsRestResp response = new BankDetailsRestResp();
		try {
			URL url = new URL(props.getRazorpayIfscUrl() + ifsc);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output = bufferedReader.readLine();
			if (StringUtil.isNotNullOrEmpty(output)) {
				if (output.startsWith("Not Found")) {
					Log.error("Failed to get Bank code - Invalid IFSC code - " + ifsc);
				} else {
					ObjectMapper mapper = new ObjectMapper();
					response = mapper.readValue(output, BankDetailsRestResp.class);
					if (response != null && StringUtil.isNotNullOrEmpty(response.getBankcode())) {
						HazelcastConfig.getInstance().getIfscCodeMapping().put(ifsc, response.getBankcode());
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
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
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}
}

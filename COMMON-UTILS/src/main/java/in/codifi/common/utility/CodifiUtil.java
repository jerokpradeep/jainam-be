package in.codifi.common.utility;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.model.response.ResponsecodeStatusModel;
import io.quarkus.logging.Log;

public class CodifiUtil {

	public static String encryptWithSHA256(String input) {
		String response = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			response = DatatypeConverter.printHexBinary(hash).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String random256Key() {
		int size = 256;
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		String num = sb.toString();
		return num;
	}

	public static void trustedManagement() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to save response code count in cache
	 * 
	 * @author Gowthaman
	 * @param statusModel
	 */
	public static void responsecodeInCache(ResponsecodeStatusModel statusModel) {
		try {
			List<ResponsecodeStatusModel> statusList = HazelcastConfig.getInstance().getResponsecodeCount()
					.get("errorCount");
			if (StringUtil.isListNotNullOrEmpty(statusList)) {
				boolean insert = false;
				for (ResponsecodeStatusModel model : statusList) {
					if (statusModel.getMethodAndModel().equalsIgnoreCase(model.getMethodAndModel())
							&& statusModel.getResponsecode() == model.getResponsecode()) {
						int count = model.getCount() + 1;
						model.setCount(count);
						insert = true;
					}
				}
				if (!insert) {
					ResponsecodeStatusModel resp = new ResponsecodeStatusModel();
					resp.setCount(1);
					resp.setMethodAndModel(statusModel.getMethodAndModel());
					resp.setResponsecode(statusModel.getResponsecode());
					statusList.add(resp);
				}
				HazelcastConfig.getInstance().getResponsecodeCount().put("errorCount", statusList);
			} else {
				List<ResponsecodeStatusModel> status = new ArrayList<>();
				ResponsecodeStatusModel resp = new ResponsecodeStatusModel();
				resp.setCount(1);
				resp.setMethodAndModel(statusModel.getMethodAndModel());
				resp.setResponsecode(statusModel.getResponsecode());
				status.add(resp);
				HazelcastConfig.getInstance().getResponsecodeCount().put("errorCount", status);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

}

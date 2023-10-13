package in.codifi.odn.utility;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

@ApplicationScoped
public class CommonUtils {


	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	static Random randomNumberGenerator = new Random();

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
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

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

	/**
	 * 
	 * Method to generate random keys
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param count
	 * @return
	 */
	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	/**
	 * 
	 * Method to generate OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public String generateOTP() {
		int otp = randomNumberGenerator.nextInt(999999);
		return String.format("%06d", otp);
	}

	/**
	 * Method to send OTP
	 * 
	 * @param otp
	 * @param mobile
	 * @param userName
	 */
//	public void sendOTPMessage(String otp, String mobile, String userName) {
//		ExecutorService pool = Executors.newSingleThreadExecutor();
//		pool.execute(new Runnable() {
//			@Override
//			public void run() {
//				int randomNum = (int) Math.random();
//				if (randomNum % 2 == 0) {
//					sendMtakzMessage(otp, mobile, userName);
//				} else {
//					sendMtakzMessage(otp, mobile, userName);
//				}
//			}
//		});
//		pool.shutdown();
//	}

	
	
	/**
	 * Method to encrypt the user API key and encrypted key using SHA256 algorithm
	 * and store it in caches
	 * 
	 * @param pObject
	 * @return
	 * @author Dinesh
	 */
	public String generateSHAKey(String pUserId, String authCode, String apiSecret) {
		String encSHAKey = null;
		try {
			String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			StringBuilder builder = new StringBuilder();
			int count = 32;
			while (count-- != 0) {
				int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
				builder.append(ALPHA_NUMERIC_STRING.charAt(character));
			}
			String data = pUserId + authCode + apiSecret;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes("UTF-8"));
			encSHAKey = DatatypeConverter.printHexBinary(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encSHAKey;
	}
}

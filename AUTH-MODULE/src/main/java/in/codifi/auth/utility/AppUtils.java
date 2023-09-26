package in.codifi.auth.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import in.codifi.auth.config.FilePropertiesConfig;
import in.codifi.auth.entity.primary.TotpDetailsEntity;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AppUtils {

	@Inject
	FilePropertiesConfig filePropsConfig;

	/**
	 * 
	 * Method to get cache key
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @param source
	 * @return
	 */
	public String getuserIdAndSourceKey(String userId, String source) {
		return userId + "_" + source;
	}

	public static String generatealpanumericNew(int size) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		String num = sb.toString();
		return num;
	}

	/**
	 * 
	 * Method to generate scanner
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 */
	public TotpDetailsEntity createScanner(String userId) {
		TotpDetailsEntity entity = new TotpDetailsEntity();
		try {
			String secretKey = generatealpanumericNew(32).toUpperCase();
			String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, userId, AppConstants.COMPANY_NAME);
			String qrCodePath = filePropsConfig.getQrCodePath();
			File qrCodeDirectory = new File(qrCodePath);
			if (!qrCodeDirectory.exists()) {
				qrCodeDirectory.mkdirs();
			}
			String filePath = qrCodePath + File.separator + secretKey + "_" + userId + ".png";
			String qrCode = createQRCode(barCodeUrl, filePath, 400, 400);
			if (StringUtil.isNotNullOrEmpty(qrCode)) {
				entity.setCompanyName(AppConstants.COMPANY_NAME);
				entity.setSecretKey(secretKey);
				entity.setImg(qrCode);
				entity.setUserId(userId);
				entity.setCreatedBy(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return entity;
	}

	private static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
		try {
			return "otpauth://totp/" + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
					+ "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20") + "&issuer="
					+ URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	private String createQRCode(String barCodeData, String filePath, int height, int width)
			throws IOException, WriterException {
		String base64EncodedImageBytes = "";
		BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			MatrixToImageWriter.writeToStream(matrix, "png", out);
			Path pathToImage = Paths.get(filePath);
			// 1. Convert image to an array of bytes
			byte[] imageBytes = Files.readAllBytes(pathToImage);
			// 2. Encode image bytes[] to Base64 encoded String
			base64EncodedImageBytes = Base64.getEncoder().encodeToString(imageBytes);
			base64EncodedImageBytes = "data:image/png;base64," + base64EncodedImageBytes;
		}
		return base64EncodedImageBytes;
	}
	
	/**
	 * Method to validate give input is mobile number
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param input
	 * @return
	 */
	public boolean isMobileNumber(String input) {
		Pattern pattern = Pattern.compile("^\\d{10}$"); // Regular expression for a 10-digit mobile number
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	
	/**
	 * Method to validate give input is Email
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param input
	 * @return
	 */
	public boolean isEmail(String input) {
		Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"); // Regular expression for an email
																					// address
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

}

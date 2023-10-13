package in.codifi.admin.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppUtils {

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

	public boolean isPan(String input) {
		Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

}

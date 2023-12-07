package in.codifi.auth.controller;
import java.security.SecureRandom;
import java.util.Base64;
public class Test1 {
	

	    public static void main(String[] args) {
	        int length = 49; // Specify the length of the random string

	        // Generate random bytes
	        byte[] randomBytes = new byte[length];
	        SecureRandom secureRandom = new SecureRandom();
	        secureRandom.nextBytes(randomBytes);

	        // Encode the random bytes as a Base64 string
	        String randomString = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

	        System.out.println(randomString);
	    }

}

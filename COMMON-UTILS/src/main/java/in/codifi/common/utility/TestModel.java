package in.codifi.common.utility;

import java.io.IOException;
import java.util.Random;

public class TestModel {


    public static void main(String[] args) throws IOException {
    	
    	int size = 100;
    	String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		String num = sb.toString();
		System.out.println(num);
    	
    	
//    		int size = 64;
//    		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
//    		StringBuilder sb = new StringBuilder(size);
//    		for (int i = 0; i < size; i++) {
//    			int index = (int) (AlphaNumericString.length() * Math.random());
//    			sb.append(AlphaNumericString.charAt(index));
//    		}
//    		String num = sb.toString();
//    		System.out.println(num);
    	
//    	String price = "123";
//    	System.out.println("Input price -- "+price);
//    	float floatValue = Float.parseFloat(price);
//    	
//    	float value = floatValue *= 100;
//    	
//    	System.out.println("value -- "+value);
    	
//    	if (price.contains(".")) {
//			p = price.replace(".", "");
//			System.out.println("p -- "+p);
//			System.out.println(" -- "+Float.parseFloat(price));
//		} else {
//			System.out.println(" -- "+Float.parseFloat(price));
//		}
    	
    }

}

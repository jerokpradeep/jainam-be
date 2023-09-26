package in.codifi.orders.utility;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import io.quarkus.logging.Log;

public class Test {

	public static void main(String[] args) {

		try {
			String testUrl = "%5B%7B%22isin%22%3A%22INE669E01016%22%2C%22qty%22%3A1%2C%22isInName%22%3A%22IDEA%22%7D%5D";
			testUrl = "%5B%7B%22settlmtCycle%22%3A%22T1%22%2C%22isin%22%3A%22INE669E01016%22%2C%22isinname%22%3A%22IDEA%22%2C%22quantity%22%3A1%7D%5D";
			System.out.println(URLDecoder.decode(testUrl, StandardCharsets.UTF_8.toString()));
		} catch (Exception e) {
			Log.error(e);
		}

	}

}

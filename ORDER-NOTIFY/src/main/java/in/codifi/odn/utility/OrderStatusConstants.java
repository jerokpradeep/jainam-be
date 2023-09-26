/**
* auther babin
* created on 18-Sep-2023
* 
*/
package in.codifi.odn.utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author babin
 *
 */
public class OrderStatusConstants {
	
	public static final Map<Integer, String> orderStatusMap = initOrderStatusMap();
	public static final Map<Integer, String> gtdOrderMap = gtdOrderMap();


	private static Map<Integer, String> initOrderStatusMap() {
		Map<Integer, String> map = new HashMap<>();
		 map.put(5, "PENDING");
		 map.put(6, "CANCELLED");
		 map.put(7, "COMPLETED");
		 map.put(9, "REJECTED");
		 map.put(10, "REJECTED");
		 map.put(11, "REJECTED");
		 map.put(12, "REJECTED");
		 map.put(14, "A.ACCEPT");
		 map.put(15, "REJECTED");
		 map.put(16, "MODIFIED");
		 map.put(17, "CANCELLED");
		 map.put(18, "AMO SUBMITTED");
		 map.put(19, "AMO CANCELLED");
		 map.put(20, "COMPLETED");
		 map.put(21, "STOPPED");
		 map.put(22, "CONVERTED");
		return Collections.unmodifiableMap(map);
	}
	
	private static Map<Integer, String> gtdOrderMap() {
		Map<Integer, String> map = new HashMap<>();
		 map.put(1, "ACTIVE");
		 map.put(2, "COMPLETED");
		 map.put(3, "WITHDRAWN");
		 map.put(4, "EXPIRED");
		 map.put(5, "REJECTED");
		return Collections.unmodifiableMap(map);
	}
}
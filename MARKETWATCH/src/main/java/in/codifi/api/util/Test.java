//package in.codifi.api.util;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.json.simple.JSONObject;
//
//public class Test {
//	public static void main(String[] args) {
//		// Assuming you have a list of JSONObjects
//		List<JSONObject> jsonList = new ArrayList<>();
//
//		// Populate the list with JSONObjects
//		// For example:
//		JSONObject object = new JSONObject();
//		object.put("name", "sowmiya");
//		object.put("name", "abi");
//		object.put("name", "puni");
//		jsonList.add(object);
//		// Sort the list based on the "name" field in each JSONObject
//		Collections.sort(jsonList, new Comparator<JSONObject>() {
//			@Override
//			public int compare(JSONObject obj1, JSONObject obj2) {
//				String name1 = obj1.getString("name");
//				String name2 = obj2.getString("name");
//				return name1.compareTo(name2);
//			}
//		});
//
//		// After sorting, the list will be in alphabetical order of names
//		for (JSONObject jsonObject : jsonList) {
//			System.out.println(jsonObject.getString("name"));
//		}
//	}
//}

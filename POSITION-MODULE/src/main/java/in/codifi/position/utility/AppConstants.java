package in.codifi.position.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AppConstants {

	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String AUTHORIZATION = "Authorization";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	public static final String PUT_METHOD = "PUT";
	public static final String DELETE_METHOD = "DELETE";
	public static final String UTF_8 = "utf-8";
	public static final String ACCEPT = "Accept";
	public static final String BEARER_WITH_SPACE = "Bearer ";

	public static final String SUCCESS_STATUS = "Success";
	public static final String FAILED_STATUS = "Failed";
	public static final String STATUS_OK = "Ok";
	public static final String STATUS_NOT_OK = "Not ok";
	public static final String FAILED_CODE = "400";
	public static final String SUCCESS_CODE = "200";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final List<JSONObject> EMPTY_ARRAY = new ArrayList<>();

	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String GUEST_USER_ERROR = "Guest User";
	public static final String ALL = "ALL";

	// AppUtil
	public static final String HAZEL_KEY_REST_SESSION = "_REST_SESSION";

	// Rest URL
	public static final String JDATA = "jData";
	public static final String JKEY = "jKey";
	public static final String SYMBOL_EQUAL = "=";
	public static final String SYMBOL_AND = "&";
	
	public static final String NSE = "NSE";
	public static final String NFO = "NFO";
	public static final String CDS = "CDS";
	public static final String BSE = "BSE";
	public static final String BFO = "BFO";
	public static final String BCD = "BCD";
	public static final String MCX = "MCX";
	public static final String NSE_EQ = "NSE_EQ";
	public static final String BSE_EQ = "BSE_EQ";
	public static final String NSE_FO = "NSE_FO";
	public static final String NSE_CUR = "NSE_CUR";

	// Rest connection
	public static final String BASE_URL = "http://103.210.195.138/NestHtml5MobileTech/rest/";
	public static final String LIMITS = "Limits";
	public static final String JSESSONID = "jsessionid";

	// Rest
	public static final String REST_STATUS_SUCCESS = "success";
	public static final String REST_STATUS_NOT_OK = "not_ok";
	public static final String REST_STATUS_OK = "Ok";
	public static final String REST_NO_DATA = "no data";
	public static final String NO_RECORD_FOUND = "No records are found";
	public static final String PRODUCT_TYPE = "Product Type";
	public static final String ORDER_TYPE = "Order Type";
	public static final String PRICE_TYPE = "Price Type";
	public static final String REST_STATUS_ERROR = "error";
	public static final String AMO = "AMO";

	// log
	public static final String MODULE_POSITIONS = "Positions";
	public static final String USER_AGENT = "User-Agent";

	// Request
	public static final String COLON = ":";
	public static final String TYPE = "type";
	public static final String QUESTION_MARK = "?";
	public static final String INTROPSTATUS = "intropStatus";
	public static final String ONE = "1";

//	public static final String X_API_KEY = "gZkbrXhn8A4UKEjN799BC9KOWbPzvSSq8Ta1Np0O";
	public static final String X_API_KEY_NAME = "x-api-key";

	public static final String CONTENT_LENGTH_KEY = "Content-Length";
	public static final String CONTENT_LENGTH_VALUE = "<calculated when request is sent>";

}

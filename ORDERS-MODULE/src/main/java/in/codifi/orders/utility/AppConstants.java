package in.codifi.orders.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AppConstants {

	public static final String MODULE_ORDERS = "MODULE_ORDERS";

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
	public static final String TRUE = "true";
	public static final String BEARER_WITH_SPACE = "Bearer ";
	public static final int PAYMENT_PRIORITY = 1;
	public static final int ORDER_PRIORITY = 2;

	public static final String OPERATOR_TERNARY = "?";
	public static final String OPERATOR_EQUALTO = "=";
	public static final String OPERATOR_SLASH = "/";
	public static final String OPERATOR_AND = "&";
	public static final String SELL = "SELL";

	public static final Character CHAR_B = 'B';
	public static final Character CHAR_N = 'N';// New order
	public static final Character CHAR_M = 'M';// Modify order
	public static final Character CHAR_S = 'S';
	public static final Character CHAR_D = 'D';

	public static final String STR_M = "M";
	public static final String STR_D = "D";
	public static final String STR_MF = "MF";
	public static final String STR_PT = "PT";

	public static final String COVER_MARGINPLUS = "MARGINPLUS";
	public static final String RL_MKT = "RL-MKT";

	public static final String SUCCESS_STATUS = "Success";
	public static final String FAILED_STATUS = "Failed";
	public static final String STATUS_OK = "Ok";
	public static final String STATUS_NOT_OK = "Not ok";
	public static final String FAILED_CODE = "400";
	public static final String SUCCESS_CODE = "200";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final List<JSONObject> EMPTY_ARRAY = new ArrayList<>();
	public static final String YES = "Yes";
	public static final String NO = "No";

	public static final String ERROR_MIN_CHAR = "Minimum 2 characters required";
	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String NOT_FOUND = "No record found";
	public static final String RECORD_DELETED = "Record Deleted";
	public static final String DELETE_FAILED = "Failed to deleted";
	public static final String NO_DATA = "No Data";
	public static final String TOKEN_NOT_EXISTS = "The token does not exists";
	public static final String INVALID_USER_SESSION = "Invalid user session";
	public static final String GUEST_USER_ERROR = "Guest User";

	public static final String INVALID_TRANS_TYPE = "Invalid transcation type";
	public static final String INVALID_EXCH = "Invalid exchange";
	public static final String INVALID_LEG_INDICATOR = "Invalid LegIndicator";

	public static final String LEG_INDICATOR_NORMAL = "Normal";
	public static final String LEG_INDICATOR_SPREAD = "Spread";
	public static final String LEG_INDICATOR_2LEG = "2Leg";
	public static final String LEG_INDICATOR_3LEG = "3Leg";

	/**
	 * For Cache
	 */
	public static final String FETCH_DATA_FROM_CACHE = "fetchDataFromCache";
	public static final String HAZEL_KEY_REST_SESSION = "_REST_SESSION";

	public static final String BUY = "BUY";
	public static final String TRANS_TYPE_BUY = "B";
	public static final String TRANS_TYPE_SELL = "S";
	public static final String PRODUCT_TYPE = "Product Type";
	public static final String ORDER_TYPE = "Order Type";
	public static final String PRICE_TYPE = "Price Type";
	public static final String AMO = "AMO";
	public static final String BRACKET = "Bracket";
	public static final String COVER = "Cover";
	public static final String REGULAR = "Regular";
	public static final String REST_BRACKET = "B";
	public static final String REST_COVER = "H";
	public static final String PRODUCT_MIS = "MIS";
	public static final String PRODUCT_CNC = "CNC";
	public static final String PRODUCT_NRML = "NRML";
	public static final String PRODUCT_MTF = "MTF";
	public static final String REST_PRODUCT_MIS = "I";
	public static final String REST_PRODUCT_CNC = "C";
	public static final String REST_PRODUCT_NRML = "M";
	public static final String REST_PRODUCT_MTF = "MF";
	public static final String REST_PRODUCT_BTST = "PT";
	public static final String CONTRACT_LOAD_SUCESS = "Contract loaded sucessfully";
	public static final String CONTRACT_LOAD_FAILED = "Failed to load contract";
	public static final String CACHE_LOADED = "Cache loaded sucessfully";

	public static final String JDATA = "jData";
	public static final String JKEY = "jKey";
	public static final String SYMBOL_EQUAL = "=";
	public static final String SYMBOL_AND = "&";

	public static final String REST_STATUS_NOT_OK = "not_ok";
	public static final String REST_STATUS_OK = "Ok";
	public static final String REST_STATUS_SUCCESS = "success";
	public static final String REST_STATUS_ERROR = "error";
	public static final String REST_NO_DATA = "no data";
	public static final String NO_RECORD_FOUND = "No records are found";

//	public static final String X_API_KEY = "gZkbrXhn8A4UKEjN799BC9KOWbPzvSSq8Ta1Np0O";
	public static final String X_API_KEY_NAME = "x-api-key";
	public static final String SLASH_SYMBOL = "/";
	public static final String QUESTION_MARK = "?";
	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";
	public static final String LIMIT_1000 = "1000";
	public static final String ORDER_STATUS = "orderStatus";
	public static final String NEGATIVE_ONE = "-1";
	public static final String ONE = "1";
	public static final String TWO = "2";
	public static final String FOUR = "4";
	public static final String FIVE = "5";
	public static final String SIX = "6";
	public static final String HOST = "<calculated when request is sent>";
	public static final String HUNDRED = "100";
	public static final String ORDER_ID = "order_id";
	public static final String ORDER_IDS = "order_ids";
	public static final String MODULE_ORDER = "Orders";

//	public static final String USER_SESSION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjE0MDQiLCJncm91cElkIjoiSE8iLCJ1c2VySWQiOiJBUElURVNUIiwidGVtcGxhdGVJZCI6IlNUQVRJQzEiLCJ1ZElkIjoiNWMzNDBiZDctNWJkMy00YWFmLTlkMDMtYjI2NjI2YjE2ODU4Iiwib2NUb2tlbiI6IjB4MDFDRDlCRTc3QzU2RjEzRkRFQTJDQkE2MjU4MzkxIiwidXNlckNvZGUiOiJOV1NZRiIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMTQwNCIsImV4cCI6MTY4OTA0NDg4MCwiaWF0IjoxNjU3NTA4OTIxfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NDQzNDU5OSwiaWF0IjoxNjg0Mzg4ODU0fQ.pW4fDMUE_E0P40yTRMykBZ-YE74bFP9TuAD1n0_ULV0";

	public static final String FE_TRACE_ID = "DEMO31_Sa212022190445";
	public static final String NEW_ORDER = "NEW";
	public static final String MODIFY_ORDER = "MODIFY";

	public static final String INTROPSTATUS = "intropStatus";

	public static final String CLIENT_INFO_IS_NULL = "Client info is null";

	public static final String NSE = "NSE";
	public static final String BSE = "BSE";
	public static final String NFO = "NFO";
	public static final String NSE_EQ = "NSE_EQ";
	public static final String BSE_EQ = "BSE_EQ";
	public static final String NSE_FO = "NSE_FO";
	public static final String CDS = "CDS";
	public static final String NSE_CUR = "NSE_CUR";
	public static final String MCX = "MCX";
	public static final String MCX_FO = "MCX_FO";
	public static final String BCD = "BCD";
	public static final String BSE_CUR = "BSE_CUR";
	public static final String BFO = "BFO";

	public static final String NRML = "NRML";
	public static final String DELIVERY = "DELIVERY";

	// brokerage
	public static final String PRODUCT_EQUAL_TO = "Product=";
	public static final String THEME_EQUAL_TO = "&Theme=";
	public static final String USERID_EQUAL_TO = "&UserId=";
	public static final String GROUPID_EQUAL_TO = "&GroupId=";
	public static final String MKTSEGMENTID_EQUAL_TO = "&MktSegmentId=";
	public static final String SERIES_EQUAL_TO = "&Series=";
	public static final String PRODUCT_TYPE_EQUAL_TO = "&ProductType=";
	public static final String TRANSACTION_TYPE_EQUAL_TO = "&TransactionType=";
	public static final String QUANTITY_EQUAL_TO = "&Quantity=";
	public static final String PRICE_EQUAL_TO = "&Price=";
	public static final String BROKERAGE_EQUAL_TO = "&Brokerage=";
	public static final String LEGINDICATOR_EQUAL_TO = "&LegIndicator=";
	public static final String SESSION_ID_EQUAL_TO = "&SessionId=";
	public static final String INSTRUMENT_EQUAL_TO = "&Instrument=";
	public static final String PRICELOCATOR_EQUAL_TO = "&PriceLocator=";
	public static final String BROKERAGE_QUERY_PARAMS = "?type=1&enct=";

	public static final String BROKERAGE_AERO = "AERO";
	public static final String BROKERAGE_THEME = "L";
	public static final String BROKERAGE_GROUPID = "HO";
	public static final String BROKERAGE_SERIES_EQ = "EQ";
	public static final String BROKERAGE_INSTRUMENT_EQUITIES = "EQUITIES";

	public static final String GTD = "GTD";

}

package in.codifi.holdings.utility;

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
	public static final char OPERATOR_SLASH = '/';
	public static final String BEARER_WITH_SPACE = "Bearer ";

	public static final String SUCCESS_STATUS = "Success";
	public static final String FAILED_STATUS = "Failed";
	public static final String STATUS_OK = "Ok";
	public static final String STATUS_NOT_OK = "Not ok";
	public static final String FAILED_CODE = "400";
	public static final String SUCCESS_CODE = "200";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final List<JSONObject> EMPTY_ARRAY = new ArrayList<>();

	public static final int INT_ZERO = 0;
	public static final int INT_ONE = 1;

	public static final Character CHAR_B = 'B';
	public static final Character CHAR_N = 'N';// New order
	public static final Character CHAR_Y = 'Y';// New order
	public static final Character CHAR_M = 'M';// Modify order
	public static final Character CHAR_S = 'S';

	public static final String ERROR_MIN_CHAR = "Minimum 2 characters required";
	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String NOT_FOUND = "No record found";
	public static final String RECORD_DELETED = "Record Deleted";
	public static final String DELETE_FAILED = "Failed to deleted";
	public static final String NO_DATA = "No Data";
	public static final String TOKEN_NOT_EXISTS = "The token does not exists";
	public static final String INVALID_USER_SESSION = "Invalid user session";
	public static final String GUEST_USER_ERROR = "Guest User";

	public static final String REST_SUCCESS_STATUS = "Success";
	public static final String REST_ERROR_STATUS = "error";

	/**
	 * For Cache
	 */
	public static final String FETCH_DATA_FROM_CACHE = "fetchDataFromCache";

	public static final String HAZEL_KEY_REST_SESSION = "_REST_SESSION";

	public static final String CONTRACT_LOAD_SUCESS = "Contract loaded sucessfully";
	public static final String CONTRACT_LOAD_FAILED = "Failed to load contract";
	public static final String CACHE_LOADED = "Cache loaded sucessfully";

	public static final String JDATA = "jData";
	public static final String JKEY = "jKey";
	public static final String SYMBOL_EQUAL = "=";
	public static final String SYMBOL_AND = "&";

	public static final String REST_STATUS_NOT_OK = "not_ok";
	public static final String REST_NO_DATA = "no data";
	public static final String NO_RECORD_FOUND = "No records are found";

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
	public static final String REST_PRODUCT_MTF = "F";

//	public static final String X_API_KEY = "He55zrpvWn9ml0fkGv1Zq5GljGVBxUv5NFieVGAa";
//	public static final String X_API_KEY = "gZkbrXhn8A4UKEjN799BC9KOWbPzvSSq8Ta1Np0O";
	public static final String X_API_KEY_NAME = "x-api-key";
	public static final String USER_SESSION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjE0MDQiLCJncm91cElkIjoiSE8iLCJ1c2VySWQiOiJBUElURVNUIiwidGVtcGxhdGVJZCI6IlNUQVRJQzEiLCJ1ZElkIjoiNWMzNDBiZDctNWJkMy00YWFmLTlkMDMtYjI2NjI2YjE2ODU4Iiwib2NUb2tlbiI6IjB4MDFDRDlCRTc3QzU2RjEzRkRFQTJDQkE2MjU4MzkxIiwidXNlckNvZGUiOiJOV1NZRiIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMTQwNCIsImV4cCI6MTY4OTA0NDg4MCwiaWF0IjoxNjU3NTA4OTIxfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NDQzNDU5OSwiaWF0IjoxNjg0Mzg4ODU0fQ.pW4fDMUE_E0P40yTRMykBZ-YE74bFP9TuAD1n0_ULV0";

	public static final String USER_CODE = "NXWAD";
	public static final String USERCODE = "userCode=";
	public static final String SESSION_ID = "sessionID=";
	public static final String SESSION_ID_VALUE = "0x0118F8A5F2B9BEA58BA37936572EC2";
	public static final String CHANNEL = "channel=";
	public static final String MOB = "MOB";
	public static final String SCRIPDETAILS = "ScripDetails=";
	public static final String MANAGER_IP = "managerIP=";
	public static final String MANAGER_IP_VALUE = "172.25.102.233";
	public static final String ISIN = "isin=";
	public static final String ISIN_NAME = "isinName=";
	public static final String EXCHANGE_CD = "exchangeCd=";
	public static final String EXCHANGE_CD_VALUE = "NSE";
	public static final String PRODUCT = "product=";

	public static final String INSTRUMENT = "instrument=";
	public static final String INSTRUMENT_VALUE = "Equity";
	public static final String QUANTITY = "quantity=";
	public static final String DP_ID = "dpId=";
	public static final String CLIENTID_ID = "clientId=";
	public static final String DEPOSITORY = "depository=";
	public static final String PRODUCT_CODE = "productcode=";
	public static final String PRODUCT_CODE_VALUE = "WAVE";
	public static final String MARKET_SEG_ID = "MarketSegId=";
	public static final String USER_ID = "userId=";
	public static final String GROUP_ID = "groupId=";
	public static final String PRODUCT_TYPE = "producttype=";
	public static final String SETTLMT_CYCLE = "SettlmtCycle=";
	public static final String SETTLMT_CYCLE_VALUE = "T1";

	// Log

	public static final String MODULE = "Auth";
	public static final String USER_AGENT = "User-Agent";
	public static final String[] SECURED_METHODS = { "/access/pwd/reset", "/access/otp/send", "access/otp/validate",
			"/access/scanner/generate", "/access/scanner/get", "/access/topt/enable", "/access/topt/verify" };
	public static final String HAZEL_KEY_OTP_SESSION = "_OTP_SESSION";
	public static final String MODULE_HOLDINGS = "Holdings";

}

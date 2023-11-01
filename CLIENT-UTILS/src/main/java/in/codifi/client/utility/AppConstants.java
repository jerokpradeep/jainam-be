package in.codifi.client.utility;

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

	public static final String ERROR_MIN_CHAR = "Minimum 2 characters required";
	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String NO_RECORDS_FOUND = "No Records Found";
	public static final String RECORD_DELETED = "Record Deleted";
	public static final String DELETE_FAILED = "Failed to deleted";
	public static final String NO_DATA = "No Data";
	public static final String TOKEN_NOT_EXISTS = "The token does not exists";
	public static final String INVALID_USER_SESSION = "Invalid user session";
	public static final String GUEST_USER_ERROR = "Guest User";

	public static final String REST_SUCCESS_STATUS = "success";
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
	public static final String REST_STATUS_OK = "Ok";
	public static final String REST_NO_DATA = "no data";
	public static final String NO_RECORD_FOUND = "No records are found";
	
	public static final String TEMP_USER_ID = "CD-ADMIN";
	public static final String CLIENT_UTILIS = "CLIENT_UTILIS_";
	
	public static final String SOURCE_MOB = "MOB";
	public static final String SOURCE_WEB = "WEB";
	public static final String MASTER_PREFERENCES = "MASTER_PREFERENCES_";
	public static final String LOAD_SUCCESS = "Data loaded Sucessfully";
	
	public static final String HAZEL_KEY_USER_DETAILS = "_USER_DETAILS";
	public static final String REST_BRACKET = "B";
	public static final String BRACKET = "Bracket";
	public static final String REST_COVER = "H";
	public static final String COVER = "Cover";
	public static final String PRODUCT_TYPE = "Product Type";
	public static final String ORDER_TYPE = "Order Type";
	public static final String PRICE_TYPE = "Price Type";
	
	public static final String CACHE_LOAD_SUCCESS = "Cache data loaded Sucessfully";
	public static final String CACHE_CLEAR_SUCCESS = "Cache data cleared Sucessfully";
	public static final String INVALID_DATA = "Invalid data";
	public static final String ADDED = "Added successfully";
	
	public static final String MODULE_CLIENT = "Client";
	public static final String USER_PROFILE = "user profile";
	
	public static final String X_API_KEY = "gZkbrXhn8A4UKEjN799BC9KOWbPzvSSq8Ta1Np0O";
//	public static final String X_API_KEY = "He55zrpvWn9ml0fkGv1Zq5GljGVBxUv5NFieVGAa";
	public static final String X_API_KEY_NAME = "x-api-key";
	
	public static final String CLIENT_INFO_IS_NULL = "Client info is null";
	
	//ERP
	public static final String ERP_AUTHORIZATION_TOKEN = "token 64ffb3da7ea0934:4bda8b7c2bca9cd";
	public static final String SUBJECT = "subject";
	public static final String RAISED_BY = "raised_by";
	public static final String DESCRIPTION = "description";
	public static final String INVALID_MOBILE = "Invalid Mobile Number";
	
}

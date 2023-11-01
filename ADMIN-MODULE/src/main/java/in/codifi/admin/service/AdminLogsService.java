package in.codifi.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONObject;

import in.codifi.admin.model.request.LoggedInRequestModel;
import in.codifi.admin.model.request.UrlRequestModel;
import in.codifi.admin.model.response.AccesslogResponseModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.model.response.LogDetailsResponseModel;
import in.codifi.admin.model.response.UrlResponsetModel;
import in.codifi.admin.repository.AdminLogsDAO;
import in.codifi.admin.req.model.MobUserReqModel;
import in.codifi.admin.service.spec.AdminLogsServiceSpec;
import in.codifi.admin.utility.AppConstants;
import in.codifi.admin.utility.PrepareResponse;
import in.codifi.admin.utility.StringUtil;
import in.codifi.cache.CacheController;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AdminLogsService implements AdminLogsServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AdminLogsDAO adminDAO;

	/**
	 * Method to get the total logged in details for past days
	 * 
	 * @author LOKESH
	 * @return
	 */
	public RestResponse<GenericResponse> userLogDetails() {
		List<LogDetailsResponseModel> result = adminDAO.getUserLogDetails();
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * method to get the user based records from the data base (TOP 10 USER)
	 * 
	 * @author LOKESH
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RestResponse<GenericResponse> getUserbasedRecords(AccesslogResponseModel accessModel) {
		List<JSONObject> result = new ArrayList<JSONObject>();
		if (accessModel.getCount().equalsIgnoreCase("all")) {
			for (Map.Entry<String, String> map : CacheController.getUserRec().entrySet()) {
				String key = map.getKey();
				String[] a = key.split("_");
				String user = a[0];
				String deviceIp = a[1];
				String count = map.getValue();
				JSONObject js = new JSONObject();
				js.put("user", user);
				js.put("device_ip", deviceIp);
				js.put("count", count);
				result.add(js);
			}
		} else {
			for (Map.Entry<String, String> map : CacheController.getUserRec().entrySet().stream().limit(25)
					.collect(Collectors.toList())) {
				String key = map.getKey();
				String[] a = key.split("_");
				String user = a[0];
				String deviceIp = a[1];
				String count = map.getValue();
				JSONObject js = new JSONObject();
				js.put("user", user);
				js.put("device_ip", deviceIp);
				js.put("count", count);
				result.add(js);
			}
		}
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * method to get the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlBasedRecords() {
		List<JSONObject> result = adminDAO.getUrlBasedRecords();
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

//	/**
//	 * Method to get last 12 hour login count
//	 * 
//	 * @author LOKESH
//	 * @return
//	 */
//	@Override
//	public RestResponse<GenericResponse> getLast12hourLoginCount() {
//		List<JSONObject> result = adminDAO.getLast12hourLoginCount();
//		if (!result.isEmpty()) {
//			return prepareResponse.prepareSuccessResponseObject(result);
//		} else {
//			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
//		}
//	}

	/**
	 * Method to get distinct url for drop down
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDistinctUrl() {
		List<String> result = adminDAO.getDistinctUrl();
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * method to get the url based records1
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlBasedRecords1() {
		List<UrlResponsetModel> result = adminDAO.getUrlBasedRecords1();
		if (result != null && result.size() > 0) {
			boolean logDetails = adminDAO.insertLogDetails(result);
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * method to get the url record
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlRecord(UrlRequestModel model) {
		List<UrlResponsetModel> result = adminDAO.getUrlRecord(model);
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * method to Insert the Login record per day
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getLoginRecord() {
		LoggedInRequestModel result = adminDAO.getSourceRecord();
		LoggedInRequestModel result1 = adminDAO.getSourceRecord1();

		if (result != null && result1 != null) {
			boolean result2 = adminDAO.InsertLoggedInRecord1(result, result1);
			if (result2 == false) {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.SUCCESS_STATUS);
			}
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get user record mob
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordMOb() {
		try {
			List<MobUserReqModel> getUserRecordMobile = adminDAO.getMobUser();
			if (StringUtil.isListNotNullOrEmpty(getUserRecordMobile)) {
				return prepareResponse.prepareSuccessResponseObject(getUserRecordMobile);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get user record web
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordWeb() {
		try {
			List<MobUserReqModel> getUserRecordWeb = adminDAO.getWebUser();
			if (StringUtil.isListNotNullOrEmpty(getUserRecordWeb)) {
				return prepareResponse.prepareSuccessResponseObject(getUserRecordWeb);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get user record API
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordApi() {
		try {
			List<MobUserReqModel> getUserRecordApi = adminDAO.getApiUser();
			if (StringUtil.isListNotNullOrEmpty(getUserRecordApi)) {
				return prepareResponse.prepareSuccessResponseObject(getUserRecordApi);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
	
	/**
	 * method to get Unique UserId
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUniqueUserId() {
		try {
			List<MobUserReqModel> getUserRecordApi = adminDAO.getUniqueUserId();
			if (StringUtil.isListNotNullOrEmpty(getUserRecordApi)) {
				return prepareResponse.prepareSuccessResponseObject(getUserRecordApi);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

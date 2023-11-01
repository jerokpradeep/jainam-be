package in.codifi.admin.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.UrlRequestModel;
import in.codifi.admin.model.response.AccesslogResponseModel;
import in.codifi.admin.model.response.GenericResponse;

public interface AdminLogsServiceSpec {

	/**
	 * Method to get the total logged in details for past days
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> userLogDetails();

	/**
	 * method to get the user based records from the data base (TOP 10 USER)
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUserbasedRecords(AccesslogResponseModel accessModel);

	/**
	 * method to get the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUrlBasedRecords();

//	/**
//	 * Method to get last 12 hour login count
//	 * 
//	 * @author LOKESH
//	 * @return
//	 */
//	RestResponse<GenericResponse> getLast12hourLoginCount();

	/**
	 * Method to get distinct url for drop down
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getDistinctUrl();

	/**
	 * method to get the url based records1
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUrlBasedRecords1();

	/**
	 * method to get the url record
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUrlRecord(UrlRequestModel model);

	/**
	 * method to Insert the Login record per day
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getLoginRecord();

	/**
	 * method to get user record mob
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUserRecordMOb();

	/**
	 * method to get user record web
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUserRecordWeb();

	/**
	 * method to get user record API
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUserRecordApi();
	
	/**
	 * method to get Unique UserId
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getUniqueUserId();

}

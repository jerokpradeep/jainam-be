package in.codifi.admin.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.controller.spec.AdminLogsControllerSpec;
import in.codifi.admin.model.request.UrlRequestModel;
import in.codifi.admin.model.response.AccesslogResponseModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.service.spec.AdminLogsServiceSpec;

@Path("/admin")
public class AdminLogsController implements AdminLogsControllerSpec {

	@Inject
	AdminLogsServiceSpec service;

	/**
	 * Method to get the total logged in details for past days
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> userLogDetails() {
		return service.userLogDetails();
	}

	/**
	 * method to get the user based records from the data base (TOP 10 USER)
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserbasedRecords(AccesslogResponseModel accessModel) {
		return service.getUserbasedRecords(accessModel);
	}

	/**
	 * method to get the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlBasedRecords() {
		return service.getUrlBasedRecords();
	}

//	/**
//	 * Method to get last 12 hour login count
//	 * 
//	 * @author LOKESH
//	 * @return
//	 */
//	@Override
//	public RestResponse<GenericResponse> getLast12hourLoginCount() {
//		return service.getLast12hourLoginCount();
//	}

	/**
	 * Method to get distinct url for drop down
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getDistinctUrl() {
		return service.getDistinctUrl();
	}

	/**
	 * method to get the url based records1
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlBasedRecords1() {
		return service.getUrlBasedRecords1();
	}

	/**
	 * method to get the url records
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUrlRecord(UrlRequestModel model) {
		return service.getUrlRecord(model);
	}

	/**
	 * method to Insert the Login record per day
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getLoginRecord() {
		return service.getLoginRecord();
	}

	/**
	 * method to get user record mob
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordMOb() {
		return service.getUserRecordMOb();
	}

	/**
	 * method to get user record web
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordWeb() {
		return service.getUserRecordWeb();
	}

	/**
	 * method to get user record API
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUserRecordApi() {
		return service.getUserRecordApi();
	}
	
	/**
	 * method to get Unique UserId
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUniqueUserId() {
		return service.getUniqueUserId();
	}
}

package in.codifi.admin.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.IndexRequestModel;
import in.codifi.admin.model.request.PreferenceRequestModel;
import in.codifi.admin.model.response.GenericResponse;

public interface AdminProductEnableServiceSpec {

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> updatePreference(PreferenceRequestModel model);

	/**
	 * method to get Preference value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> getPreference();

	/**
	 * Method to load Admin Preference
	 * 
	 * @author gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> loadAdminPreference();

	/**
	 * method to update index value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> updateIndexValue(IndexRequestModel model);

	/**
	 * method to get index value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> getIndexValue();

	/**
	 * method to truncate index value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> truncateIndexValue();

}

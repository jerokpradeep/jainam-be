package in.codifi.admin.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.controller.spec.AdminProductEnableControllerSpec;
import in.codifi.admin.model.request.IndexRequestModel;
import in.codifi.admin.model.request.PreferenceRequestModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.service.spec.AdminProductEnableServiceSpec;

@Path("/product")
public class AdminProductEnableController implements AdminProductEnableControllerSpec {

	@Inject
	AdminProductEnableServiceSpec service;

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> updatePreference(PreferenceRequestModel model) {
		return service.updatePreference(model);
	}

	/**
	 * method to get Preference value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getPreference() {
		return service.getPreference();
	}

	/**
	 * Method to load Admin Preference
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loadAdminPreference() {
		return service.loadAdminPreference();
	}

	/**
	 * method to update index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> updateIndexValue(IndexRequestModel model) {
		return service.updateIndexValue(model);
	}

	/**
	 * method to get index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getIndexValue() {
		return service.getIndexValue();
	}

	/**
	 * method to truncate index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> truncateIndexValue() {
		return service.truncateIndexValue();
	}

}

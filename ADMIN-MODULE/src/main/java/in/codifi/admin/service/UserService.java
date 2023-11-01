package in.codifi.admin.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONObject;

import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.model.response.UsersLoggedInModel;
import in.codifi.admin.repository.UserLogManager;
import in.codifi.admin.service.spec.UserServiceSpec;
import in.codifi.admin.utility.AppConstants;
import in.codifi.admin.utility.PrepareResponse;

@ApplicationScoped
public class UserService implements UserServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	UserLogManager userLogManager;

	/**
	 * method to get users logged in details
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getUserLoggedInDetails() {
		UsersLoggedInModel model = new UsersLoggedInModel();
		model = userLogManager.getCountBySource();
		int totalCount = userLogManager.getTotalUserLoggedInDetails();
		if (totalCount > 0) {
			model.setTotalCount(totalCount);
			return prepareResponse.prepareSuccessResponseObject(model);
		}
		List<String> distinctVendor = userLogManager.findDistinctVendors();
		if (distinctVendor != null && !distinctVendor.isEmpty()) {
			List<JSONObject> ssoCountByVendor = userLogManager.getCountByVendor(distinctVendor);

			if (ssoCountByVendor != null) {
				model.setSso(ssoCountByVendor);
				model.setTotalCount(totalCount);
				return prepareResponse.prepareSuccessResponseObject(model);
			}
		} else {
			return prepareResponse.prepareSuccessResponseObject(model);
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to Truncate user logged in details
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> truncateUserLoggedInDetails() {
		return userLogManager.truncateUserLoggedInDetails();
	}

}

package in.codifi.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.ameyo.config.HazelcastConfig;
import in.codifi.ameyo.model.response.GenericResponse;
import in.codifi.ameyo.utility.AppConstants;
import in.codifi.ameyo.utility.PrepareResponse;

@ApplicationScoped
public class CacheService {

	@Inject
	PrepareResponse prepareResponse;

	/**
	 * 
	 * Method to clear cache
	 * 
	 * @author Dinesh Kumar
	 *
	 */
	public void clearCache() {
		HazelcastConfig.getInstance().getKeycloakAdminSession().clear();

		/** To clear Kambala rest session **/
		HazelcastConfig.getInstance().getRestUserSession().clear();
		HazelcastConfig.getInstance().getIsRestUserSessionActive().clear();
		HazelcastConfig.getInstance().getUserSessionDetails().clear();
	}

	/**
	 * 
	 * Method to clear all kambala rest session
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public RestResponse<GenericResponse> clearAllRestSession() {
		HazelcastConfig.getInstance().getRestUserSession().clear();
		HazelcastConfig.getInstance().getIsRestUserSessionActive().clear();
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	/**
	 * 
	 * Method to clear user kambala rest session
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @return
	 */
	public RestResponse<GenericResponse> clearRestSessionById(String userId) {
		HazelcastConfig.getInstance().getRestUserSession().remove(userId + AppConstants.HAZEL_KEY_REST_SESSION);
		HazelcastConfig.getInstance().getIsRestUserSessionActive().remove(userId + AppConstants.HAZEL_KEY_REST_SESSION);
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	public RestResponse<GenericResponse> clearAllRestApiClientDetails() {
		HazelcastConfig.getInstance().getRestApiClinetInfo().clear();
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	public RestResponse<GenericResponse> clearRestApiClientDetailsById(String userId) {
		HazelcastConfig.getInstance().getRestApiClinetInfo().remove(userId);
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

}

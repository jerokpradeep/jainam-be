package in.codifi.alerts.utility;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.alerts.config.HazelcastConfig;
import in.codifi.alerts.controller.DefaultRestController;
import in.codifi.cache.model.ClientInfoModel;

@ApplicationScoped
public class AppUtil extends DefaultRestController {

	/**
	 * 
	 * Method to get client info
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public ClientInfoModel getClientInfo() {
		ClientInfoModel model = clientInfo();
		return model;
	}

	/*
	 * method to get user session id
	 * 
	 */
	public static String getUserSession(String userId) {
		String userSession = "";
		String hzUserSessionKey = userId + AppConstants.HAZEL_KEY_REST_SESSION;
		userSession = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
		return userSession;
	}

}

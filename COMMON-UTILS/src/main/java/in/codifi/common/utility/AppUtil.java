package in.codifi.common.utility;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.controller.DefaultRestController;

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
	public ClinetInfoModel getClientInfo() {
		ClinetInfoModel model = clientInfo();
		return model;
	}

	public static String getUserSession(String userId) {
		String userSession = "";
		String hzUserSessionKey = userId + AppConstants.HAZEL_KEY_REST_SESSION;
		System.out.println("hzUserSessionKey -- " + hzUserSessionKey);
		userSession = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
		return userSession;
	}

}
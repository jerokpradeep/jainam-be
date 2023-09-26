package in.codifi.orders.utility;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.orders.config.HazelcastConfig;
import in.codifi.orders.controller.DefaultRestController;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AppUtil extends DefaultRestController {

	public static String getUserSession(String userId) {
		String userSession = "";
		String hzUserSessionKey = userId + AppConstants.HAZEL_KEY_REST_SESSION;
		userSession = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
		return userSession;
	}

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

	/**
	 * 
	 * Method to validate the userId
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param reqUserID
	 * @return
	 */
	public boolean isValidUser(String userID) {

		try {
			String userIdFromToken = getUserId();
			if (StringUtil.isNotNullOrEmpty(userID) && StringUtil.isNotNullOrEmpty(userIdFromToken)) {
				if (userID.equalsIgnoreCase(userIdFromToken)) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return false;
	}

	/**
	 * Method to get contract master
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param exch
	 * @param token
	 * @return
	 */
	public static ContractMasterModel getContractMaster(String exch, String token) {
		ContractMasterModel contractMasterModel = new ContractMasterModel();
		contractMasterModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
		return contractMasterModel;
	}

}
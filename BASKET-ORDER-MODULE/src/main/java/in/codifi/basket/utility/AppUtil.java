package in.codifi.basket.utility;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.basket.config.HazelcastConfig;
import in.codifi.basket.controller.DefaultRestController;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
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
	 * Method to get access token
	 * 
	 * @author DINESH KUMAR
	 *
	 * @return
	 */
	public String getAccessToken() {
		String token = "";
		try {
			token = getAcToken();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return token;
	}

	/**
	 * Method to get contract info
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param exch
	 * @param token
	 * @return
	 */
	public ContractMasterModel getContractInfo(String exch, String token) {
		ContractMasterModel contractMasterModel = new ContractMasterModel();
		try {
			if (HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token) != null) {
				contractMasterModel = HazelcastConfig.getInstance().getContractMaster().get(exch + "_" + token);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return contractMasterModel;
	}

}
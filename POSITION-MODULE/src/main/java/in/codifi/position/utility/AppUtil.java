package in.codifi.position.utility;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.position.config.HazelcastConfig;
import in.codifi.position.controller.DefaultRestController;
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
	 * To get PDC from cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param token
	 * @param exch
	 * @return
	 */
	public static String getPdc(String exch, String token) {
		String pdc = "0";

		if (HazelcastConfig.getInstance().getContractMaster().containsKey(exch + "_" + token)) {
			ContractMasterModel contractMasterModel = HazelcastConfig.getInstance().getContractMaster()
					.get(exch + "_" + token);
			if (contractMasterModel != null && StringUtil.isNotNullOrEmpty(contractMasterModel.getPdc())) {
				pdc = contractMasterModel.getPdc();
			}
		} else {
			Log.error("Not able to get PDC from cache");
		}
		return pdc;
	}

	/**
	 * 
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
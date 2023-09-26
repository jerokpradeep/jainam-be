package in.codifi.auth.ws.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.config.KeyCloakConfig;
import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.KcConstants;
import in.codifi.auth.utility.StringUtil;
import in.codifi.auth.ws.model.kc.BlockAndUnblockUserReq;
import in.codifi.auth.ws.model.kc.GetUserInfoResp;
import in.codifi.auth.ws.model.kc.RestPasswordReq;
import in.codifi.auth.ws.service.spec.KcAdminRestSpec;

@ApplicationScoped
public class KcAdminRest {

	@Inject
	@RestClient
	KcAdminRestSpec kcAdminRestSpec;

	@Inject
	KeyCloakConfig props;

	@Inject
	KcTokenRest kcTokenRest;

	/**
	 * 
	 * Method to get id by userId
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @return
	 */
	private String getKeycloakId(String userId) {
		String id = "";
		try {
			List<GetUserInfoResp> infoResps = new ArrayList<>();
			if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(userId)) {
				infoResps = HazelcastConfig.getInstance().getKeycloakUserDetails().get(userId);
			} else {
				infoResps = getUserInfo(userId);
			}
			for (GetUserInfoResp getUserInfoResp : infoResps) {
				if (getUserInfoResp.getUsername().equalsIgnoreCase(userId)) {
					return getUserInfoResp.getId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 
	 * Method to get user by userName to check whether user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userName
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public List<GetUserInfoResp> getUserInfo(String userName) throws ClientWebApplicationException {
		List<GetUserInfoResp> response = new ArrayList<>();
		try {

			String token = "Bearer " + getAccessToken();
			response = kcAdminRestSpec.getUserInfo(token, userName.toLowerCase(), KcConstants.EXACT_TRUE);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	private String getAccessToken() {

		if (HazelcastConfig.getInstance().getKeycloakAdminSession().containsKey(props.getAdminClientId())) {
			return HazelcastConfig.getInstance().getKeycloakAdminSession().get(props.getAdminClientId());
		}
		return kcTokenRest.getAdminAccessToken();
	}

	/**
	 * 
	 * Method to get user by attribute to check whether user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param key
	 * @param value
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public List<GetUserInfoResp> getUserInfoByAttribute(String key, String value) throws ClientWebApplicationException {
		List<GetUserInfoResp> response = new ArrayList<>();
		try {
			String token = "Bearer " + getAccessToken();
			String request = key + ":" + value;
			response = kcAdminRestSpec.getUserInfoByAttribute(token, request);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String resetPassword(AuthReq authReq) throws ClientWebApplicationException {
		String response = AppConstants.FAILED_STATUS;
		try {
			String token = "Bearer " + getAccessToken();
			RestPasswordReq request = new RestPasswordReq();
			request.setType("password");
			request.setValue(authReq.getPassword());
			String id = getKeycloakId(authReq.getUserId());
			if (StringUtil.isNullOrEmpty(id))
				return AppConstants.FAILED_STATUS;
			kcAdminRestSpec.resetPassword(token, id, request);
			return AppConstants.SUCCESS_STATUS;
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 * Method to unblock account
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String unblockAccount(String userId) throws ClientWebApplicationException {
		String response = AppConstants.FAILED_STATUS;
		try {
			String token = "Bearer " + getAccessToken();
			BlockAndUnblockUserReq request = new BlockAndUnblockUserReq();
			request.setEnabled(true);
			String id = getKeycloakId(userId);
			if (StringUtil.isNullOrEmpty(id))
				return AppConstants.FAILED_STATUS;
			kcAdminRestSpec.blockAndUnblock(token, id, request);
			return AppConstants.SUCCESS_STATUS;
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Method to block user
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param userId
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String blockAccount(String userId) throws ClientWebApplicationException {
		String response = AppConstants.FAILED_STATUS;
		try {
			String token = "Bearer " + getAccessToken();
			BlockAndUnblockUserReq request = new BlockAndUnblockUserReq();
			request.setEnabled(false);
			String id = getKeycloakId(userId);
			if (StringUtil.isNullOrEmpty(id))
				return AppConstants.FAILED_STATUS;
			kcAdminRestSpec.blockAndUnblock(token, id, request);
			return AppConstants.SUCCESS_STATUS;
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 * Method to change password
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authReq
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String changePassword(AuthReq authReq) throws ClientWebApplicationException {
		String response = AppConstants.FAILED_STATUS;
		try {
			String token = "Bearer " + getAccessToken();
			RestPasswordReq request = new RestPasswordReq();
			request.setType("password");
			request.setValue(authReq.getNewPassword());
			String id = getKeycloakId(authReq.getUserId());
			if (StringUtil.isNullOrEmpty(id))
				return AppConstants.FAILED_STATUS;
			kcAdminRestSpec.resetPassword(token, id, request);
			return AppConstants.SUCCESS_STATUS;
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazelcastConfig.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}
}

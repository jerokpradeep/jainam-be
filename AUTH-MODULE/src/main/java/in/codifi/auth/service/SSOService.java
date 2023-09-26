package in.codifi.auth.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.entity.primary.VendorEntity;
import in.codifi.auth.entity.primary.VendorSubcriptionEntity;
import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.model.request.VendorReqModel;
import in.codifi.auth.model.response.AuthRespModel;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.model.response.SsoAuthRespModel;
import in.codifi.auth.model.response.VendorAppRespModel;
import in.codifi.auth.model.response.VendorDetailsRespModel;
import in.codifi.auth.model.response.VendorRespModel;
import in.codifi.auth.repository.VendorRepository;
import in.codifi.auth.repository.VendorSubcriptionRepository;
import in.codifi.auth.service.spec.SSOServiceSpec;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.CodifiUtil;
import in.codifi.auth.utility.KcConstants;
import in.codifi.auth.utility.PrepareResponse;
import in.codifi.auth.utility.StringUtil;
import in.codifi.auth.ws.model.kc.GetIntroSpectResponse;
import in.codifi.auth.ws.model.kc.GetTokenResponse;
import in.codifi.auth.ws.service.KcTokenRest;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SSOService implements SSOServiceSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	VendorRepository vendorRepository;

	@Inject
	VendorSubcriptionRepository subcriptionRepository;

	@Inject
	CodifiUtil codifiUtil;

	@Inject
	KcTokenRest kcTokenRest;

	/**
	 * Method to authorize Vendor
	 * 
	 * @author dinesh
	 * @param vendorReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> ssoAuthorizeVendor(VendorReqModel authReq) {
		try {
			if (StringUtil.isNullOrEmpty(authReq.getVendor()) || StringUtil.isNullOrEmpty(authReq.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			String apiKey = authReq.getVendor().toUpperCase();
			// Check the Vendor details with this API Key
			List<VendorEntity> vendorEntities = vendorRepository
					.findAllByApiKeyAndAuthorizationStatusAndActiveStatus(apiKey, 1, 1);
			if (vendorEntities == null || vendorEntities.size() <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);

			VendorEntity vendorDetails = vendorEntities.get(0);

			List<VendorSubcriptionEntity> vendorSubcriptionEntities = subcriptionRepository
					.findAllByUserIdAndAppIdAndActiveStatus(authReq.getUserId(), vendorDetails.getId(), 1);
//			if (vendorSubcriptionEntities == null || vendorSubcriptionEntities.size() <= 0)
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

//			VendorSubcriptionEntity tempVendorSubcriptionEntity = vendorSubcriptionEntities.get(0);
			VendorRespModel vendorRespModel = new VendorRespModel();
			if (vendorSubcriptionEntities != null && vendorSubcriptionEntities.size() > 0
					&& vendorSubcriptionEntities.get(0).getAuthorizationStatus() == 1) {

				String authCode = CodifiUtil.randomAlphaNumeric(20).toUpperCase();
				String temAuthCode = apiKey + "_" + authCode;
				String shaKey = codifiUtil.generateSHAKey(authReq.getUserId(), authCode, vendorDetails.getApiSecret());
				HazelcastConfig.getInstance().getVendorAuthCode().put(shaKey, authReq.getUserId());
				HazelcastConfig.getInstance().getVendorAuthCode().put(temAuthCode.toUpperCase(), authReq.getUserId());
				vendorRespModel.setRedirectUrl(
						vendorDetails.getRedirectUrl() + "?authCode=" + authCode + "&userId=" + authReq.getUserId());
				return prepareResponse.prepareSuccessResponseObject(vendorRespModel);
			} else {

				/**
				 * Insert in to data base as the user authorized the vendor
				 */
				VendorSubcriptionEntity subcriptionEntity = new VendorSubcriptionEntity();
				subcriptionEntity.setAppId(vendorDetails.getId());
				subcriptionEntity.setUserId(authReq.getUserId());
				subcriptionEntity.setAuthorizationStatus(1);
				subcriptionEntity.setCreatedBy(authReq.getUserId());
				VendorSubcriptionEntity subcriptionEntityNew = subcriptionRepository.saveAndFlush(subcriptionEntity);
				if (subcriptionEntityNew != null) {
					String authCode = CodifiUtil.randomAlphaNumeric(20).toUpperCase();
					String temAuthCode = apiKey + "_" + authCode;
					String shaKey = codifiUtil.generateSHAKey(authReq.getUserId(), authCode,
							vendorDetails.getApiSecret());
					HazelcastConfig.getInstance().getVendorAuthCode().put(shaKey, authReq.getUserId());
					HazelcastConfig.getInstance().getVendorAuthCode().put(temAuthCode.toUpperCase(),
							authReq.getUserId());
					vendorRespModel.setRedirectUrl(vendorDetails.getRedirectUrl() + "?authCode=" + authCode + "&userId="
							+ authReq.getUserId());
					return prepareResponse.prepareSuccessResponseObject(vendorRespModel);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to check Vendor Authorization
	 * 
	 * @author dinesh
	 * @param vendorReqModel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> checkVendorAuthorization(VendorReqModel authReq) {

		try {

			if (StringUtil.isNullOrEmpty(authReq.getVendor()) || StringUtil.isNullOrEmpty(authReq.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			String apiKey = authReq.getVendor().toUpperCase();
			// Check the Vendor details with this API Key
			List<VendorEntity> vendorEntities = vendorRepository
					.findAllByApiKeyAndAuthorizationStatusAndActiveStatus(apiKey, 1, 1);
			if (vendorEntities == null || vendorEntities.size() <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			VendorEntity vendorDetails = vendorEntities.get(0);

			List<VendorSubcriptionEntity> vendorSubcriptionEntities = subcriptionRepository
					.findAllByUserIdAndAppIdAndActiveStatus(authReq.getUserId(), vendorDetails.getId(), 1);
//			if (vendorSubcriptionEntities == null || vendorSubcriptionEntities.size() <= 0)
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			VendorRespModel vendorRespModel = new VendorRespModel();

			String authCode = CodifiUtil.randomAlphaNumeric(20).toUpperCase();
			String temAuthCode = apiKey + "_" + authCode;
			String shaKey = codifiUtil.generateSHAKey(authReq.getUserId(), authCode, vendorDetails.getApiSecret());
			HazelcastConfig.getInstance().getVendorAuthCode().put(shaKey, authReq.getUserId());
			HazelcastConfig.getInstance().getVendorAuthCode().put(temAuthCode.toUpperCase(), authReq.getUserId());

//			VendorSubcriptionEntity tempVendorSubcriptionEntity = vendorSubcriptionEntities.get(0);
//			if (tempVendorSubcriptionEntity.getAuthorizationStatus() == 1) {

			if (vendorSubcriptionEntities != null && vendorSubcriptionEntities.size() > 0
					&& vendorSubcriptionEntities.get(0).getAuthorizationStatus() == 1) {
				vendorRespModel.setRedirectUrl(
						vendorDetails.getRedirectUrl() + "?authCode=" + authCode + "&userId=" + authReq.getUserId());
				vendorRespModel.setAuthorized(true);
			} else {
				vendorRespModel.setAuthorized(false);
			}
			return prepareResponse.prepareSuccessResponseObject(vendorRespModel);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	@Override
	public RestResponse<GenericResponse> getUserDetails(VendorReqModel authReq) {
		try {
			if (StringUtil.isNullOrEmpty(authReq.getCheckSum()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/**
			 * Get user by the given auth code
			 */
			if (StringUtil.isNullOrEmpty(
					HazelcastConfig.getInstance().getVendorAuthCode().get(authReq.getCheckSum().toUpperCase())))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_AUTH_CODE);

			String clientId = HazelcastConfig.getInstance().getVendorAuthCode()
					.get(authReq.getCheckSum().toUpperCase());
			/**
			 * TODO : Check the per hour count and set into the cache
			 */

			String userSessionId = getStringUserSessionIdNew(clientId);
			if (StringUtil.isNotNullOrEmpty(userSessionId)) {
//				AuthRespModel authRespModel = new AuthRespModel();
				SsoAuthRespModel respModel = new SsoAuthRespModel();
				respModel.setAccessToken(userSessionId);
				respModel.setClientId(clientId);
				return prepareResponse.prepareSuccessResponseObject(respModel);
			} else {
				Log.error("Failed to get access token");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to generate the new access token
	 * 
	 * @author Gowrisankar
	 * @param pUserId
	 * @return
	 */
	private String getStringUserSessionIdNew(String pUserId) {
		String accessToken = "";
		try {

			AuthReq authmodel = new AuthReq();
			authmodel.setUserId(pUserId);
			authmodel.setSource("WEB");
			AuthRespModel respModel = keyCloakLoginByRefershToken(authmodel);
			if (respModel != null && StringUtil.isNotNullOrEmpty(respModel.getAccessToken())) {
				accessToken = respModel.getAccessToken();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return accessToken;
	}

	@Override
	public RestResponse<GenericResponse> getUserDetailsByAuth(VendorReqModel authReq) {
		try {
			if (StringUtil.isNullOrEmpty(authReq.getVendor()) || StringUtil.isNullOrEmpty(authReq.getAuthCode()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/**
			 * Check with the valid vendor or not
			 */
			List<VendorEntity> vendorEntities = vendorRepository
					.findAllByApiKeyAndAuthorizationStatusAndActiveStatus(authReq.getVendor(), 1, 1);
			if (vendorEntities == null || vendorEntities.size() <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_VENDOR);

			VendorEntity vendorDetails = vendorEntities.get(0);
			String tempAuthCode = vendorDetails.getApiKey() + "_" + authReq.getAuthCode();
			/**
			 * Get user by the given auth code
			 */
			if (StringUtil
					.isNullOrEmpty(HazelcastConfig.getInstance().getVendorAuthCode().get(tempAuthCode.toUpperCase())))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_AUTH_CODE);

			String clientId = HazelcastConfig.getInstance().getVendorAuthCode().get(tempAuthCode.toUpperCase());
			/**
			 * TODO : Check the per hour count and set into the cache
			 */

			String userSessionId = getStringUserSessionIdNew(clientId);
			if (StringUtil.isNotNullOrEmpty(userSessionId)) {
				AuthRespModel authRespModel = new AuthRespModel();
				authRespModel.setAccessToken(userSessionId);
				authRespModel.setClientId(clientId);
				return prepareResponse.prepareSuccessResponseObject(authRespModel);
			} else {
				Log.error("Failed to get access token");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * KC login while logging with vendor
	 * 
	 * @author Gowrisankar
	 * @param authmodel
	 * @return
	 */
	public AuthRespModel keyCloakLoginByRefershToken(AuthReq authmodel) {
		AuthRespModel respModel = new AuthRespModel();
		try {

			/** Get refresh token **/
			String hazelKey = authmodel.getUserId() + "_" + authmodel.getSource();
			if (HazelcastConfig.getInstance().getKeycloakSession().get(hazelKey) != null) {

				GetTokenResponse cacheKcTokenResp = HazelcastConfig.getInstance().getKeycloakSession().get(hazelKey);
				if (StringUtil.isNullOrEmpty(cacheKcTokenResp.getRefreshToken())) {
					Log.error("KC-LoginByRefershToken - Refresh Token is null");
					return null;
				}

				GetTokenResponse kcTokenResp = kcTokenRest
						.getUserTokenByRefereshToken(cacheKcTokenResp.getRefreshToken());

				if (kcTokenResp != null) {

					/** Return if failed to login on key clock **/
					if (StringUtil.isNotNullOrEmpty(kcTokenResp.getError())) {
						Log.error(kcTokenResp.getErrorDescription());
						return null;
					}

					if (StringUtil.isNotNullOrEmpty(kcTokenResp.getAccessToken())) {

						/** To get user roles by requesting user Introspect API **/
						GetIntroSpectResponse introSpectResponse = kcTokenRest.getIntroSpect(authmodel,
								kcTokenResp.getAccessToken());

//					if (introSpectResponse != null && introSpectResponse.getResourceAccess() != null
//							&& introSpectResponse.getResourceAccess().getClientRoles() != null
//							&& introSpectResponse.getActive() != null) {
//
//						if (!introSpectResponse.getActive()) {
						if (introSpectResponse != null && introSpectResponse.getClientRoles() != null
								&& introSpectResponse.getActive() != null) {

							if (!introSpectResponse.getActive()) {
								Log.error("KC-Login - " + AppConstants.USER_BLOCKED);
								return null;

							}

							/** Add new session into Distributed Cache **/
							String hazelKeyForApi = authmodel.getUserId() + "_" + "API";
							HazelcastConfig.getInstance().getKeycloakSession().put(hazelKeyForApi, kcTokenResp);
							HazelcastConfig.getInstance().getKeycloakUserInfo().put(hazelKeyForApi, introSpectResponse);

							respModel.setAccessToken(kcTokenResp.getAccessToken());
							respModel.setRefreshToken(kcTokenResp.getRefreshToken());

//						List<String> resourceAccessRole = introSpectResponse.getResourceAccess().getClientRoles()
//								.getRoles();
							List<String> resourceAccessRole = introSpectResponse.getClientRoles();

							if (resourceAccessRole.contains(KcConstants.ACTIVE_USER)) {
								respModel.setKcRole(KcConstants.ACTIVE_USER);
							} else if (resourceAccessRole.contains(KcConstants.GUEST_USER)) {
								respModel.setKcRole(KcConstants.GUEST_USER);
							}

							return respModel;

						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to get vendor app deatils
	 * 
	 * @author Dinesh Kumar
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getVendorAppDetails(VendorReqModel authReq) {
		VendorDetailsRespModel response = new VendorDetailsRespModel();
		try {
			if (StringUtil.isNullOrEmpty(authReq.getVendor()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/**
			 * Check with the valid vendor or not
			 */
			List<VendorEntity> vendorEntities = vendorRepository
					.findAllByApiKeyAndAuthorizationStatusAndActiveStatus(authReq.getVendor(), 1, 1);
			if (vendorEntities == null || vendorEntities.size() <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_VENDOR);

			response.setImageUrl(vendorEntities.get(0).getIconUrl());
			response.setAppName(vendorEntities.get(0).getAppName());

			return prepareResponse.prepareSuccessResponseObject(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare response for vendor app model
	 * 
	 * @author Dinesh Kumar
	 * @param vendorAppEntities
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<VendorAppRespModel> prepareResponse(List<VendorEntity> vendorAppEntities) {

		List<VendorAppRespModel> responseModel = new ArrayList<>();
		try {

			for (VendorEntity entity : vendorAppEntities) {
				VendorAppRespModel result = new VendorAppRespModel();
				result.setAppId(entity.getId());
				result.setAppName(entity.getAppName());
				result.setApiKey(entity.getApiKey());
				result.setApiSecret(entity.getApiSecret());
				result.setClientId(entity.getClientId());
				result.setRedirectUrl(entity.getRedirectUrl());
				result.setPostbackUrl(entity.getPostbackUrl());
				result.setDescription(entity.getDescription());
				result.setAuthorizationStatus(entity.getAuthorizationStatus());
				result.setContactName(entity.getContactName());
				result.setMobieNo(entity.getMobileNo());
				result.setEmail(entity.getEmail());
				result.setType(entity.getType());
				responseModel.add(result);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return responseModel;
	}
}

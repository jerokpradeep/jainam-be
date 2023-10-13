package in.codifi.auth.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;

import de.taimos.totp.TOTP;
import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.config.RestProperties;
import in.codifi.auth.entity.primary.DeviceMappingEntity;
import in.codifi.auth.entity.primary.TotpDetailsEntity;
import in.codifi.auth.entity.primary.TwoFAPreferenceEntity;
import in.codifi.auth.entity.primary.VendorEntity;
import in.codifi.auth.entity.primary.VendorSubcriptionEntity;
import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.model.request.ForgetPassReq;
import in.codifi.auth.model.request.LoginRestReq;
import in.codifi.auth.model.request.UnblockReq;
import in.codifi.auth.model.response.AuthRespModel;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.model.response.OtpRespModel;
import in.codifi.auth.model.response.TotpResponseModel;
import in.codifi.auth.model.response.UsersLoggedInRespModel;
import in.codifi.auth.model.response.ValidatePwdOtpResp;
import in.codifi.auth.model.response.VerifyClientResp;
import in.codifi.auth.repository.AccessLogManager;
import in.codifi.auth.repository.DeviceMappingRepository;
import in.codifi.auth.repository.TotpRepository;
import in.codifi.auth.repository.TwoFAPreferenceRepository;
import in.codifi.auth.repository.VendorRepository;
import in.codifi.auth.repository.VendorSubcriptionRepository;
import in.codifi.auth.service.spec.AuthServiceSpec;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.AppUtils;
import in.codifi.auth.utility.CodifiUtil;
import in.codifi.auth.utility.KcConstants;
import in.codifi.auth.utility.PrepareResponse;
import in.codifi.auth.utility.StringUtil;
import in.codifi.auth.ws.model.kc.GetIntroSpectResponse;
import in.codifi.auth.ws.model.kc.GetTokenResponse;
import in.codifi.auth.ws.model.kc.GetUserInfoResp;
import in.codifi.auth.ws.model.login.LoginRestResp;
import in.codifi.auth.ws.service.KcAdminRest;
import in.codifi.auth.ws.service.KcTokenRest;
import in.codifi.auth.ws.service.LoginRestService;
import in.codifi.auth.ws.service.SmsRestService;
import in.codifi.orders.ws.model.OdinSsoModel;
import in.codifi.orders.ws.model.PlainMessage;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AuthService implements AuthServiceSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	KcAdminRest kcAdminRest;

	@Inject
	KcTokenRest kcTokenRest;

	@Inject
	TotpRepository totpRepository;

	@Inject
	AppUtils appUtils;

	@Inject
	TwoFAPreferenceRepository twoFARepo;

	@Inject
	DeviceMappingRepository deviceMappingRepository;

	@Inject
	CodifiUtil codifiUtil;

	@Inject
	LoginRestService loginRestService;

	@Inject
	SmsRestService smsRestService;

	@Inject
	RestProperties props;

	@Inject
	VendorRepository vendorRepository;

	@Inject
	VendorSubcriptionRepository subcriptionRepository;
	@Inject
	AccessLogManager accessLogManager;

	/**
	 * Method to verfify our token from ODIN req
	 * 
	 * @author Nesan
	 * @return
	 */
	@Override
	public RestResponse<OdinSsoModel> verifyToken(String token) {
		PlainMessage plainMessage = new PlainMessage();
		DateFormat formatter = new SimpleDateFormat("YYY-MM-DD HH:mm:ss");
		try {
			Date currentDate = new Date();
			if (StringUtil.isNullOrEmpty(token))
				return prepareResponse.prepareOdinResponse(AppConstants.CODE_200, AppConstants.INVALID_PARAMETER,
						plainMessage);

			String ucc = HazelcastConfig.getInstance().getRestUserSsoToken().get(token);// TODO
			if (StringUtil.isNotNullOrEmpty(ucc)) {
				plainMessage.setUcc(ucc);
				plainMessage.setCallbackUrl("");
				return prepareResponse.prepareOdinResponse(AppConstants.CODE_200, AppConstants.TOKEN_VERIFIED,
						plainMessage);
			}
			plainMessage.setTimestamp(formatter.format(currentDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareOdinResponse(AppConstants.CODE_200, AppConstants.TOKEN_NOT_VERIFIED,
				plainMessage);
	}

	/**
	 * 
	 * Method to check client exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> verifyClient(AuthReq authmodel) {
		try {

			if (StringUtil.isNullOrEmpty(authmodel.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			if (appUtils.isMobileNumber(authmodel.getUserId())) {
				return verifyClientByAttribute(AppConstants.ATTRIBUTE_MOBILE, authmodel.getUserId());
			}

			if (appUtils.isEmail(authmodel.getUserId())) {
				return verifyClientByAttribute(AppConstants.ATTRIBUTE_MAIL, authmodel.getUserId());
			}

			VerifyClientResp verifyClientResp = new VerifyClientResp();
			List<GetUserInfoResp> userInfo = kcAdminRest.getUserInfo(authmodel.getUserId());
			if (StringUtil.isListNotNullOrEmpty(userInfo)) {
				if (userInfo.get(0) != null && userInfo.get(0).getEnabled() != null) {
					if (!userInfo.get(0).getEnabled())
						return prepareResponse.prepareFailedResponse(AppConstants.USER_BLOCKED);
				}
				HazelcastConfig.getInstance().getKeycloakUserDetails().remove(authmodel.getUserId());
				HazelcastConfig.getInstance().getKeycloakUserDetails().put(authmodel.getUserId(), userInfo);
				verifyClientResp.setIsExist(AppConstants.YES);
				if (userInfo.get(0).getAttributes().getMobile().get(0) != null) {
					String mobile = userInfo.get(0).getAttributes().getMobile().get(0);
					verifyClientResp.setMobileNo(mobile.replaceAll(".(?=.{4})", "*"));
				}
				if (userInfo.get(0).getEmail() != null) {
					verifyClientResp.setEmailID(userInfo.get(0).getEmail().replaceAll(".(?=.{12})", "*"));
				}
				if (userInfo.get(0).getFirstName() != null) {
					verifyClientResp.setName(userInfo.get(0).getFirstName());
				}
				if (userInfo.get(0).getUsername() != null) {
					verifyClientResp.setUserId(userInfo.get(0).getUsername());
				}
				return prepareResponse.prepareSuccessResponseObject(verifyClientResp);
			} else {
				verifyClientResp.setIsExist(AppConstants.NO);
//				return prepareResponse.prepareSuccessResponseObject(verifyClientResp);
				return prepareResponse.prepareFailedResponse(AppConstants.PLEASE_ENTER_VALID_CREDENTIALS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to verify client by attribute
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	private RestResponse<GenericResponse> verifyClientByAttribute(String key, String value) {

		try {

			VerifyClientResp verifyClientResp = new VerifyClientResp();
			List<GetUserInfoResp> userInfo = kcAdminRest.getUserInfoByAttribute(key, value);
			if (StringUtil.isListNotNullOrEmpty(userInfo)) {
				/** If attribute linked with multiple userId return message **/
				if (userInfo.size() > 1)
					return prepareResponse.prepareFailedResponse("Given " + key + AppConstants.MULTIPLE_USER_LINKED);

				String userId = userInfo.get(0).getUsername().toUpperCase();
				HazelcastConfig.getInstance().getKeycloakUserDetails().remove(userId);
				HazelcastConfig.getInstance().getKeycloakUserDetails().put(userId, userInfo);
//				verifyClientResp.setIsExist(AppConstants.YES);
//				verifyClientResp.setUserId(userId);
				verifyClientResp.setIsExist(AppConstants.YES);
				if (userInfo.get(0).getAttributes().getMobile().get(0) != null) {
					String mobile = userInfo.get(0).getAttributes().getMobile().get(0);
					verifyClientResp.setMobileNo(mobile.replaceAll(".(?=.{4})", "*"));
				}
				if (userInfo.get(0).getEmail() != null) {
					verifyClientResp.setEmailID(userInfo.get(0).getEmail().replaceAll(".(?=.{12})", "*"));
				}
				if (userInfo.get(0).getFirstName() != null) {
					verifyClientResp.setName(userInfo.get(0).getFirstName());
				}
				if (userInfo.get(0).getUsername() != null) {
					verifyClientResp.setUserId(userInfo.get(0).getUsername());
				}
				return prepareResponse.prepareSuccessResponseObject(verifyClientResp);
			} else {
				verifyClientResp.setIsExist(AppConstants.NO);
				return prepareResponse.prepareSuccessResponseObject(verifyClientResp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate password
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validatePassword(AuthReq authmodel) {

		/** Validate Request **/
		if (StringUtil.isNullOrEmpty(authmodel.getUserId()) || StringUtil.isNullOrEmpty(authmodel.getPassword())
				|| StringUtil.isNullOrEmpty(authmodel.getSource()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		String key = authmodel.getUserId() + "_" + authmodel.getSource();
		try {
			GetTokenResponse kcTokenResp = kcTokenRest.getToken(authmodel);

			if (kcTokenResp != null) {

				/** Return if failed to login on key clock **/
				if (StringUtil.isNotNullOrEmpty(kcTokenResp.getError()))
					return prepareResponse.prepareFailedResponse(kcTokenResp.getErrorDescription());

				if (StringUtil.isNotNullOrEmpty(kcTokenResp.getAccessToken())) {

					AuthRespModel authRespModel = new AuthRespModel();
					OtpRespModel otpRespModel = new OtpRespModel();
					/** To get user roles by requesting user Introspect API **/
					GetIntroSpectResponse introSpectResponse = kcTokenRest.getIntroSpect(authmodel,
							kcTokenResp.getAccessToken());

					if (introSpectResponse != null && introSpectResponse.getClientRoles() != null
							&& introSpectResponse.getActive() != null) {

						if (!introSpectResponse.getActive())
							return prepareResponse.prepareFailedResponse(AppConstants.USER_BLOCKED);

						/** Put the user info into intermediate cache **/
						HazelcastConfig.getInstance().getKeycloakMedianUserInfo().remove(key);
						HazelcastConfig.getInstance().getKeycloakMedianUserInfo().put(key, introSpectResponse);

						List<String> resourceAccessRole = introSpectResponse.getClientRoles();

						if (resourceAccessRole.contains(KcConstants.ACTIVE_USER)) {
							otpRespModel = getSessionFor2FA(authmodel);
							loadKcIntermediateCache(authmodel, kcTokenResp);
							validateRestSession(authmodel);
							return prepareResponse.prepareSuccessResponseObject(otpRespModel);
						} else if (resourceAccessRole.contains(KcConstants.GUEST_USER)) {
							authRespModel = prepareRespForGuest(kcTokenResp, introSpectResponse, authmodel);
							return prepareResponse.prepareSuccessResponseObject(authRespModel);
						}

					}
				}
			}
		} catch (ClientWebApplicationException ex) {
			if (ex.getResponse().getStatus() == 401) {

				int retryCount = 1;
				if (HazelcastConfig.getInstance().getPasswordRetryCount()
						.containsKey(authmodel.getUserId() + AppConstants.HAZEL_KEY_PWD_RETRY_COUNT)) {
					retryCount = HazelcastConfig.getInstance().getPasswordRetryCount()
							.get(authmodel.getUserId() + AppConstants.HAZEL_KEY_PWD_RETRY_COUNT) + 1;
				}
				HazelcastConfig.getInstance().getPasswordRetryCount().put(
						authmodel.getUserId() + AppConstants.HAZEL_KEY_PWD_RETRY_COUNT, retryCount, 300,
						TimeUnit.SECONDS);
				if (retryCount < 4) {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_CREDENTIALS);
				} else {
					blockAccount(authmodel.getUserId());
					HazelcastConfig.getInstance().getPasswordRetryCount()
							.remove(authmodel.getUserId() + AppConstants.HAZEL_KEY_PWD_RETRY_COUNT);
					return prepareResponse.prepareFailedResponse(AppConstants.USER_BLOCKED);
				}

			}
			if (ex.getResponse().getStatus() == 400)
				return prepareResponse.prepareFailedResponse(AppConstants.USER_BLOCKED);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS_VALIDATE);
	}

	/**
	 * 
	 * Method to generate intermediate session to authenticate OTP validation
	 * service
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	private OtpRespModel getSessionFor2FA(AuthReq authmodel) {
		OtpRespModel respModel = new OtpRespModel();
		try {
			String sessionId = CodifiUtil.randomAlphaNumeric(256);

			if (StringUtil.isNotNullOrEmpty(sessionId)) {
				String key = authmodel.getUserId() + "_" + authmodel.getSource() + AppConstants.HAZEL_KEY_OTP_SESSION;
				HazelcastConfig.getInstance().getUserSessionOtp().put(key, sessionId);

				if (HazelcastConfig.getInstance().getTwoFAUserPreference().containsKey(authmodel.getUserId())) {
					String type = HazelcastConfig.getInstance().getTwoFAUserPreference().get(authmodel.getUserId());
					if (type.equalsIgnoreCase(AppConstants.TOTP)) {
						respModel.setTotpAvailable(true);
					}
				}
				respModel.setToken(sessionId);
				respModel.setKcRole(KcConstants.ACTIVE_USER);
				/** To clear OTP cache for resend **/
				String hzKey = authmodel.getUserId() + "_" + authmodel.getSource();
				removeOTPCache(hzKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respModel;
	}

	/**
	 * 
	 * Method to load intermediate key cloak cache.
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @param response
	 */
	private void loadKcIntermediateCache(AuthReq authmodel, GetTokenResponse response) {

		String hazelKey = authmodel.getUserId() + "_" + authmodel.getSource();
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					HazelcastConfig.getInstance().getKeycloakMedianSession().put(hazelKey, response);
					/** Check if user has session for the source **/
					if (HazelcastConfig.getInstance().getKeycloakSession().get(hazelKey) != null) {
						// TODO Sent Web Socket notification with Info
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * 
	 * Method to validate rest user session for active user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @param session
	 */
	private void validateRestSession(AuthReq authReq) {

		String hzUserSessionKey = authReq.getUserId() + AppConstants.HAZEL_KEY_REST_SESSION;
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boolean sessionActive = false;
					/** Check if user has session for the source **/
					if (HazelcastConfig.getInstance().getRestUserSession().containsKey(hzUserSessionKey)) {
						String session = HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey);
						System.out.println("validateRestSession session -- " + session);
//						String req = prepareClientDetails(authReq.getUserId(), session);
//						if (StringUtil.isNotNullOrEmpty(req)) {
//							String response = kambalaRestServices.getUserDetails(req, authReq.getSource());
//							if (StringUtil.isNotNullOrEmpty(response)
//									&& response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
//								sessionActive = true;
//							}
//						}
					}
					HazelcastConfig.getInstance().getIsRestUserSessionActive().put(hzUserSessionKey, sessionActive);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

//	private String prepareClientDetails(String userId, String userSession) {
//		ObjectMapper mapper = new ObjectMapper();
//		String request = "";
//		try {
//			UserDetailsRestReqModel reqModel = new UserDetailsRestReqModel();
//			reqModel.setUid(userId);
//			String json = mapper.writeValueAsString(reqModel);
//			request = AppConstants.JDATA + json + AppConstants.SYMBOL_AND + AppConstants.JKEY + userSession;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return request;
//
//	}

	/**
	 * 
	 * Method to prepare response for guest user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param response
	 * @param role
	 * @return
	 */
	private AuthRespModel prepareRespForGuest(GetTokenResponse response, GetIntroSpectResponse introSpectResponse,
			AuthReq authmodel) {
		AuthRespModel respModel = new AuthRespModel();
		respModel.setAccessToken(response.getAccessToken());
		respModel.setRefreshToken(response.getRefreshToken());
		respModel.setKcRole(KcConstants.GUEST_USER);
		String hzKey = authmodel.getUserId() + "_" + authmodel.getSource();
		HazelcastConfig.getInstance().getKeycloakSession().remove(hzKey);
		HazelcastConfig.getInstance().getKeycloakSession().put(hzKey, response);
		return respModel;
	}

	/**
	 * 
	 * Method to send OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> sendOtpFor2FA(AuthReq authmodel) {

		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(authmodel.getUserId()) || StringUtil.isNullOrEmpty(authmodel.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			String hazelKey = authmodel.getUserId() + "_" + authmodel.getSource();
			if (HazelcastConfig.getInstance().getKeycloakMedianUserInfo().containsKey(hazelKey)) {
				GetIntroSpectResponse userInfo = HazelcastConfig.getInstance().getKeycloakMedianUserInfo()
						.get(hazelKey);
				if (userInfo != null && StringUtil.isNotNullOrEmpty(userInfo.getMobile())) {
					String response = sendOTP(userInfo.getMobile(), authmodel.getSource(), AppConstants.OTP_MSG,
							authmodel.getUserId());

					if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
						return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);

					} else {
						return prepareResponse.prepareFailedResponse(response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to validate OTP for 2FA
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validate2FAOTP(AuthReq authReq) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(authReq.getUserId()) || StringUtil.isNullOrEmpty(authReq.getOtp())
					|| StringUtil.isNullOrEmpty(authReq.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			String validateResp = validateOTP(authReq.getUserId(), authReq.getSource(), authReq.getOtp());

			if (validateResp.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {

				/** get user info from cache. If does not exist get it from keycloak **/
				List<GetUserInfoResp> userInfo = new ArrayList<>();
				if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(authReq.getUserId())) {
					userInfo = HazelcastConfig.getInstance().getKeycloakUserDetails().get(authReq.getUserId());
				} else {
					userInfo = kcAdminRest.getUserInfo(authReq.getUserId());
				}
				String userName = "";
				userName = StringUtil.isNotNullOrEmpty(userInfo.get(0).getFirstName()) ? userInfo.get(0).getFirstName()
						: "";
				userName = userName + " "
						+ (StringUtil.isNotNullOrEmpty(userInfo.get(0).getLastName()) ? userInfo.get(0).getLastName()
								: "");
				/** update fcmToken into device mapping **/
				updateDeviceMapping(authReq.getUserId(), authReq.getFcmToken(), authReq.getSource(),
						userName.toUpperCase().trim());

				/** update rest session **/
				updateRestSession(authReq);
				return prepareRepForActiveUser(authReq);
			} else {
				return prepareResponse.prepareFailedResponse(validateResp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to validate OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	private String validateOTP(String userId, String source, String otp) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(otp) || StringUtil.isNullOrEmpty(source))
				return AppConstants.INVALID_PARAMETER;

			String hzKey = userId + "_" + source;

			/** Check hold time to validate **/
			Log.info("contains otp - "
					+ HazelcastConfig.getInstance().getOtp().containsKey(hzKey + AppConstants.HAZEL_KEY_OTP));
			if (HazelcastConfig.getInstance().getHoldResendOtp().containsKey(hzKey + AppConstants.HAZEL_KEY_OTP_HOLD))
				return AppConstants.OTP_LIMIT_EXCEED;

			Log.info("validateOTP - " + hzKey);
			/** Check the validity **/
			if (!HazelcastConfig.getInstance().getOtp().containsKey(hzKey + AppConstants.HAZEL_KEY_OTP))
				return AppConstants.OTP_EXCEED;

			/** Check the retry count **/
			if (HazelcastConfig.getInstance().getRetryOtpCount()
					.containsKey(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT)
					&& HazelcastConfig.getInstance().getRetryOtpCount()
							.get(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT) > 3) {
				HazelcastConfig.getInstance().getHoldResendOtp().put(hzKey + AppConstants.HAZEL_KEY_OTP_HOLD, true, 300,
						TimeUnit.SECONDS);
				HazelcastConfig.getInstance().getRetryOtpCount().remove(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT);
				return AppConstants.OTP_LIMIT_EXCEED;
			}

			String cacheOtp = HazelcastConfig.getInstance().getOtp().get(hzKey + AppConstants.HAZEL_KEY_OTP);

			/** validate OTP **/
			if (otp.equals(cacheOtp)) {
				/** Removing OTP cache if OTP validated **/
				removeOTPCache(hzKey);
				return AppConstants.SUCCESS_STATUS;
			} else {
				int retryCount = 1;
				if (HazelcastConfig.getInstance().getRetryOtpCount()
						.containsKey(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT)) {
					retryCount = HazelcastConfig.getInstance().getRetryOtpCount()
							.get(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT) + 1;
				}
				HazelcastConfig.getInstance().getRetryOtpCount().put(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT,
						retryCount);
				return AppConstants.OTP_INVALID;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

	/**
	 * 
	 * Method to remove OTP cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param hzKey
	 */
	private void removeOTPCache(String hzKey) {

		HazelcastConfig.getInstance().getOtp().remove(hzKey + AppConstants.HAZEL_KEY_OTP);
		HazelcastConfig.getInstance().getHoldResendOtp().remove(hzKey + AppConstants.HAZEL_KEY_OTP_HOLD);
		HazelcastConfig.getInstance().getResendOtp().remove(hzKey + AppConstants.HAZEL_KEY_OTP_RESEND);
		HazelcastConfig.getInstance().getRetryOtpCount().remove(hzKey + AppConstants.HAZEL_KEY_OTP_RETRY_COUNT);
	}

	/**
	 * 
	 * Method to send OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	private String sendOTP(String mobileNo, String source, String message, String userId) {

		try {
			String hzKey = userId + "_" + source;
			Log.info("sendOTP - " + hzKey);
			if (HazelcastConfig.getInstance().getResendOtp().containsKey(hzKey + AppConstants.HAZEL_KEY_OTP_RESEND))
				return AppConstants.RESEND_FAILED;

			if (HazelcastConfig.getInstance().getHoldResendOtp().containsKey(hzKey + AppConstants.HAZEL_KEY_OTP_HOLD))
				return AppConstants.OTP_LIMIT_EXCEED;

//			String otp = codifiUtil.generateOTP();
//			String otp = "123789";
			String otp = "456789";
			System.out.println("OTP - " + otp);
//			long phoneNo = Long.parseLong(mobileNo);
//			boolean sendOtp = codifiUtil.sendOTPMessage(otp, mobileNo, message);
//			boolean sendOtp = smsRestService.sendOTPtoMobile(otp, mobileNo);
			boolean sendOtp = true;
			if (sendOtp) {
				HazelcastConfig.getInstance().getOtp().remove(hzKey + AppConstants.HAZEL_KEY_OTP);
				/** Set 5 mins validity for otp validation **/
				HazelcastConfig.getInstance().getOtp().put(hzKey + AppConstants.HAZEL_KEY_OTP, otp, 300,
						TimeUnit.SECONDS);
				Log.info("HZ sendOTP - "
						+ HazelcastConfig.getInstance().getOtp().get(hzKey + AppConstants.HAZEL_KEY_OTP));
				/** Set 30 sec validity for resent **/
				HazelcastConfig.getInstance().getResendOtp().put(hzKey + AppConstants.HAZEL_KEY_OTP_RESEND, otp, 30,
						TimeUnit.SECONDS);
				return AppConstants.SUCCESS_STATUS;
			} else {
				return AppConstants.CANNOT_SEND_OTP;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

	/**
	 * 
	 * Method to prepare final response for active user after 2FA validation
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	private RestResponse<GenericResponse> prepareRepForActiveUser(AuthReq authmodel) {
		UsersLoggedInRespModel loggedModel = new UsersLoggedInRespModel();
		Timestamp timestamp = new Timestamp(new Date().getTime());
		String hour = new SimpleDateFormat("HH:mm:ss").format(timestamp);
		try {
			String hazelKey = authmodel.getUserId() + "_" + authmodel.getSource();
			if (HazelcastConfig.getInstance().getKeycloakMedianSession().containsKey(hazelKey)
					&& HazelcastConfig.getInstance().getKeycloakMedianUserInfo().containsKey(hazelKey)) {

				GetTokenResponse intermediateToken = HazelcastConfig.getInstance().getKeycloakMedianSession()
						.get(hazelKey);
				GetIntroSpectResponse intermediateUserInfo = HazelcastConfig.getInstance().getKeycloakMedianUserInfo()
						.get(hazelKey);
				/** Logout old session and remove Distributed Cache **/
				if (HazelcastConfig.getInstance().getKeycloakSession().get(hazelKey) != null
						&& HazelcastConfig.getInstance().getKeycloakUserInfo().get(hazelKey) != null) {

					GetTokenResponse token = HazelcastConfig.getInstance().getKeycloakSession().get(hazelKey);
					GetIntroSpectResponse userInfo = HazelcastConfig.getInstance().getKeycloakUserInfo().get(hazelKey);
					kcTokenRest.logout(userInfo.getUserId(), token.getAccessToken(), token.getRefreshToken());
					HazelcastConfig.getInstance().getKeycloakSession().remove(hazelKey);
					HazelcastConfig.getInstance().getKeycloakUserInfo().remove(hazelKey);
				}
				/** Add new session into Distributed Cache **/
				HazelcastConfig.getInstance().getKeycloakSession().put(hazelKey, intermediateToken);
				HazelcastConfig.getInstance().getKeycloakUserInfo().put(hazelKey, intermediateUserInfo);

				AuthRespModel respModel = new AuthRespModel();
				respModel.setAccessToken(intermediateToken.getAccessToken());
				respModel.setRefreshToken(intermediateToken.getRefreshToken());
				respModel.setKcRole(KcConstants.ACTIVE_USER);

				/** Clear intermediate cache **/
				HazelcastConfig.getInstance().getKeycloakMedianSession().remove(hazelKey);
				HazelcastConfig.getInstance().getKeycloakMedianUserInfo().remove(hazelKey);
				HazelcastConfig.getInstance().getUserSessionOtp().remove(hazelKey + AppConstants.HAZEL_KEY_OTP_SESSION);

				loggedModel.setSource(authmodel.getSource());
				loggedModel.setUserId(authmodel.getUserId());
				loggedModel.setTime(hour);
				String hazelcastKey = "";
				
				String key = authmodel.getUserId() + "_"+ authmodel.getSource();
				
				String response = HazelcastConfig.getInstance().getLogResponseModel().get(key);
				if(response == null) {
					HazelcastConfig.getInstance().getLogResponseModel().put(key, key);
					if (authmodel.getSource().equalsIgnoreCase("WEB")) {
						HazelcastConfig.getInstance().getWebLoggedInUsers().put(authmodel.getUserId(), loggedModel);
						accessLogManager.insertUserLogginedInDetails(authmodel.getUserId(), authmodel.getSource(),
								hazelcastKey);
					}
					if (authmodel.getSource().equalsIgnoreCase("MOB")) {
						HazelcastConfig.getInstance().getMobLoggedInUsers().put(authmodel.getUserId(), loggedModel);
						accessLogManager.insertUserLogginedInDetails(authmodel.getUserId(), authmodel.getSource(),
								hazelcastKey);
					}
					if (authmodel.getSource().equalsIgnoreCase("API")) {
						HazelcastConfig.getInstance().getApiLoggedInUsers().put(authmodel.getUserId(), loggedModel);
						accessLogManager.insertUserLogginedInDetails(authmodel.getUserId(), authmodel.getSource(),
								hazelcastKey);
					}
				}
				
				/** For SSO Login **/
				if (StringUtil.isNotNullOrEmpty(authmodel.getVendor())) {
					/*
					 * Check the vendor is valid or not
					 */
					String vendorKey = authmodel.getVendor().toUpperCase();
					List<VendorEntity> vendorDetails = vendorRepository.findAllByApiKey(vendorKey);

					if (vendorDetails != null && vendorDetails.size() > 0) {

						/*
						 * Generate the auth code with Alpha numeric
						 */
						String authCode = CodifiUtil.randomAlphaNumeric(20).toUpperCase();
						String temAuthCode = vendorKey + "_" + authCode;
						String shaKey = codifiUtil.generateSHAKey(authmodel.getUserId(), authCode,
								vendorDetails.get(0).getApiSecret());
						HazelcastConfig.getInstance().getVendorAuthCode().put(shaKey, authmodel.getUserId());
						HazelcastConfig.getInstance().getVendorAuthCode().put(temAuthCode.toUpperCase(),
								authmodel.getUserId());

						List<VendorSubcriptionEntity> vendorSubcriptionEntities = subcriptionRepository
								.findAllByUserIdAndAppIdAndActiveStatus(authmodel.getUserId(),
										vendorDetails.get(0).getId(), 1);
						if (vendorSubcriptionEntities != null && vendorSubcriptionEntities.size() > 0) {
							VendorSubcriptionEntity tempVendorSubcriptionEntity = vendorSubcriptionEntities.get(0);
							if (tempVendorSubcriptionEntity.getAuthorizationStatus() == 1) {
								respModel.setAuthorized(true);
								/*
								 * Build the response
								 */
								respModel.setRedirectUrl(vendorDetails.get(0).getRedirectUrl() + "?authCode=" + authCode
										+ "&userId=" + authmodel.getUserId());
							} else {
								respModel.setAuthorized(false);
							}
						} else {
							respModel.setAuthorized(false);
						}
					}
				}

				return prepareResponse.prepareSuccessResponseObject(respModel);
			}
			Log.error("KeyCloak info does not exist");
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to update rest user session after OTP validation
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 */
	private void updateRestSession(AuthReq authmodel) {

		String hzUserSessionKey = authmodel.getUserId() + AppConstants.HAZEL_KEY_REST_SESSION;
//		ExecutorService pool = Executors.newSingleThreadExecutor();
//		pool.execute(new Runnable() {
//			@Override
//			public void run() {
		try {
			/** Check if rest user session is active, if not get new session **/
			if (!HazelcastConfig.getInstance().getIsRestUserSessionActive().get(hzUserSessionKey)) {

				/** Prepare request body **/
				LoginRestReq request = prepareSsoLoginRequest(authmodel);
				if (request != null) {
					LoginRestResp loginRestResp = loginRestService.ssoLogin(request);
					if (loginRestResp != null && StringUtil.isNotNullOrEmpty(loginRestResp.getStatus())) {
						if (loginRestResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_ERROR)) {
							System.out.println("Failed to get REST Session -" + loginRestResp.getErrors());
							Log.error("Failed to get REST Session -" + loginRestResp.getErrors());
						} else if (loginRestResp.getStatus().equalsIgnoreCase(AppConstants.REST_STATUS_SUCCESS)) {
							updateUserCache(loginRestResp, authmodel.getUserId());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//			}
//		});
//		pool.shutdown();
	}

	/**
	 * Method to Prepare request body for kambala login API
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	private LoginRestReq prepareSsoLoginRequest(AuthReq authmodel) {

		LoginRestReq restReq = new LoginRestReq();
		try {
			String key = authmodel.getUserId() + "_" + authmodel.getSource();
			GetIntroSpectResponse userInfo = new GetIntroSpectResponse();
			if (HazelcastConfig.getInstance().getKeycloakMedianUserInfo().containsKey(key)) {
				userInfo = HazelcastConfig.getInstance().getKeycloakMedianUserInfo().get(key);
			} else if (HazelcastConfig.getInstance().getKeycloakUserInfo().containsKey(key)) {
				userInfo = HazelcastConfig.getInstance().getKeycloakUserInfo().get(key);
			}
			if (userInfo != null) {

				String ssoToken = CodifiUtil.random256Key();
				Log.info("Tok-" + ssoToken);
				if (StringUtil.isNotNullOrEmpty(ssoToken)) {
					HazelcastConfig.getInstance().getRestUserSsoToken().put(ssoToken, authmodel.getUserId());
					restReq.setUser_id(authmodel.getUserId());
					restReq.setLogin_type("TP_TOKEN");
					restReq.setPassword(ssoToken);
					restReq.setSecond_auth("");
//					restReq.setApi_key(
//							"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9.LzXW_BBI8CJR9clDDTTXezwbr5bnAHJgpPWgI-Tg1RQ");
					restReq.setApi_key(props.getApiKey());
					restReq.setSource("MOBILEAPI");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return restReq;
	}

	/**
	 * Method update latest user session details on cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param model
	 * @param userID
	 */
	private void updateUserCache(LoginRestResp model, String userID) {

		String hzUserDetailKey = userID + AppConstants.HAZEL_KEY_USER_DETAILS;
		String hzUserSessionKey = userID + AppConstants.HAZEL_KEY_REST_SESSION;

		System.out.println("hzUserDetailKey -- " + hzUserDetailKey);
		System.out.println("hzUserSessionKey -- " + hzUserSessionKey);

		HazelcastConfig.getInstance().getUserSessionDetails().remove(hzUserDetailKey);
		HazelcastConfig.getInstance().getRestUserSession().remove(hzUserSessionKey);
		HazelcastConfig.getInstance().getUserSessionDetails().put(hzUserDetailKey, model);
		HazelcastConfig.getInstance().getRestUserSession().put(hzUserSessionKey, model.getData().getAccessToken());

		System.out.println("hzUserDetailKey getUserSessionDetails-- "
				+ HazelcastConfig.getInstance().getUserSessionDetails().get(hzUserSessionKey));
		System.out.println("hzUserDetailKey getRestUserSession-- "
				+ HazelcastConfig.getInstance().getRestUserSession().get(hzUserSessionKey));
	}

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> passwordReset(AuthReq authmodel) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(authmodel.getUserId()) || StringUtil.isNullOrEmpty(authmodel.getPassword()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			String response = kcAdminRest.resetPassword(authmodel);
			if (StringUtil.isNotNullOrEmpty(response) && response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
				return prepareResponse.prepareSuccessMessage(AppConstants.PASSWORD_CHANGED_SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to verify details to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param forgetPassReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> forgetPasword(ForgetPassReq forgetPassReq) {

		try {

			if (StringUtil.isNullOrEmpty(forgetPassReq.getUserId()) || StringUtil.isNullOrEmpty(forgetPassReq.getPan()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			List<GetUserInfoResp> userInfo = new ArrayList<>();
			/** get user info from cache. If does not exist get it from keycloak **/
//			if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(forgetPassReq.getUserId())) {
//				userInfo = HazelcastConfig.getInstance().getKeycloakUserDetails().get(forgetPassReq.getUserId());
//			} else {
//				userInfo = kcAdminRest.getUserInfo(forgetPassReq.getUserId());
//			}
			/** get user info from keycloak **/
			userInfo = kcAdminRest.getUserInfo(forgetPassReq.getUserId());

			/** Return if user does not exist **/
			if (!StringUtil.isListNotNullOrEmpty(userInfo))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_USER);

			/** Validate Pan, if success send otp **/
			if (userInfo.get(0).getAttributes() != null
					&& StringUtil.isListNotNullOrEmpty(userInfo.get(0).getAttributes().getPan())
					&& StringUtil.isListNotNullOrEmpty(userInfo.get(0).getAttributes().getMobile())) {
				String pan = userInfo.get(0).getAttributes().getPan().get(0);
				if (forgetPassReq.getPan().equalsIgnoreCase(pan)) {

					String response = sendOTP(userInfo.get(0).getAttributes().getMobile().get(0),
							forgetPassReq.getSource(), AppConstants.OTP_MSG, forgetPassReq.getUserId());
					if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
						return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);
					} else {
						return prepareResponse.prepareFailedResponse(response);
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PAN);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to resend otp for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param mobileNo
	 * @param source
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> forgetPwdResendOtp(ForgetPassReq forgetPassReq) {
//
//		try {
//			/** Validate Request **/
//			if (StringUtil.isNullOrEmpty(forgetPassReq.getUserId())
//					|| StringUtil.isNullOrEmpty(forgetPassReq.getSource()))
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//
//			String response = sendOTP(forgetPassReq.getMobileNo(), forgetPassReq.getSource(), AppConstants.OTP_MSG);
//			if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
//				return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);
//			} else {
//				return prepareResponse.prepareFailedResponse(response);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.error(e.getMessage());
//		}
//		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//	}

	/**
	 * 
	 * Method to validate otp for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param forgetPassReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateForgetPwdOTP(ForgetPassReq forgetPassReq) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(forgetPassReq.getUserId()) || StringUtil.isNullOrEmpty(forgetPassReq.getOtp())
					|| StringUtil.isNullOrEmpty(forgetPassReq.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** validate OTP **/
			String validateResp = validateOTP(forgetPassReq.getUserId(), forgetPassReq.getSource(),
					forgetPassReq.getOtp());

			if (!validateResp.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
				return prepareResponse.prepareFailedResponse(validateResp);

			/** If success send token to reset or unblock **/
			ValidatePwdOtpResp resp = new ValidatePwdOtpResp();
			String session = getSessionForReset(forgetPassReq.getUserId(), forgetPassReq.getSource());
			if (StringUtil.isNullOrEmpty(session))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			resp.setToken(session);
			return prepareResponse.prepareSuccessResponseObject(resp);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to get session for reset password and unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @param source
	 * @return
	 */
	private String getSessionForReset(String userId, String source) {
		String sessionId = "";
		try {
			sessionId = CodifiUtil.randomAlphaNumeric(256);

			if (StringUtil.isNotNullOrEmpty(sessionId)) {
				String key = userId + "_" + source + AppConstants.HAZEL_KEY_OTP_SESSION;
				HazelcastConfig.getInstance().getUserSessionOtp().put(key, sessionId);
				return sessionId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sessionId;
	}

	/**
	 * 
	 * Method to unblock account
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> unblock(UnblockReq unblockReq) {
		try {

			if (StringUtil.isNullOrEmpty(unblockReq.getUserId()) || StringUtil.isNullOrEmpty(unblockReq.getPan()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			List<GetUserInfoResp> userInfo = new ArrayList<>();
			/** get user info from cache. If does not exist get it from keycloak **/
//			if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(unblockReq.getUserId())) {
//				userInfo = HazelcastConfig.getInstance().getKeycloakUserDetails().get(unblockReq.getUserId());
//			} else {
//				userInfo = kcAdminRest.getUserInfo(unblockReq.getUserId());
//			}
			/** get user info from keycloak **/
			userInfo = kcAdminRest.getUserInfo(unblockReq.getUserId());
			/** Return if user does not exist **/
			if (!StringUtil.isListNotNullOrEmpty(userInfo))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_USER);

			/** Validate Pan, if success send otp **/
			if (userInfo.get(0).getAttributes() != null
					&& StringUtil.isListNotNullOrEmpty(userInfo.get(0).getAttributes().getPan())
					&& StringUtil.isListNotNullOrEmpty(userInfo.get(0).getAttributes().getMobile())) {

				String pan = userInfo.get(0).getAttributes().getPan().get(0);
				if (unblockReq.getPan().equalsIgnoreCase(pan)) {
					String response = sendOTP(userInfo.get(0).getAttributes().getMobile().get(0),
							unblockReq.getSource(), AppConstants.OTP_MSG, unblockReq.getUserId());
					if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
						return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);
					} else {
						return prepareResponse.prepareFailedResponse(response);
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PAN);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to resend otp for unblock
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> resendOtpToUnblock(UnblockReq unblockReq) {
//
//		try {
//			/** Validate Request **/
//			if (StringUtil.isNullOrEmpty(unblockReq.getMobileNo()) || StringUtil.isNullOrEmpty(unblockReq.getSource()))
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//
//			String response = sendOTP(unblockReq.getMobileNo(), unblockReq.getSource(), AppConstants.OTP_MSG);
//			if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
//				return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);
//			} else {
//				return prepareResponse.prepareFailedResponse(response);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.error(e.getMessage());
//		}
//		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//	}

	/**
	 * 
	 * Method to validate otp for unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateOtpToUnblock(UnblockReq unblockReq) {
		try {
			/** Validate Request **/
			if (StringUtil.isNullOrEmpty(unblockReq.getUserId()) || StringUtil.isNullOrEmpty(unblockReq.getOtp())
					|| StringUtil.isNullOrEmpty(unblockReq.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** validate OTP **/
			String validateResp = validateOTP(unblockReq.getUserId(), unblockReq.getSource(), unblockReq.getOtp());

			if (!validateResp.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
				return prepareResponse.prepareFailedResponse(validateResp);

			String unblockResp = unblockAccount(unblockReq.getUserId());

			if (unblockResp.equalsIgnoreCase(AppConstants.USER_UNBLOCK_SUCCESS)) {
				HazelcastConfig.getInstance().getPasswordRetryCount().remove(unblockReq.getUserId());
				return prepareResponse.prepareSuccessMessage(unblockResp);
			}

			return prepareResponse.prepareFailedResponse(unblockResp);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @return
	 */

	private String unblockAccount(String userId) {
		try {
			String response = kcAdminRest.unblockAccount(userId);
			if (StringUtil.isNotNullOrEmpty(response) && response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
				return AppConstants.USER_UNBLOCK_SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

	/**
	 * method to block user
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param userId
	 * @return
	 */
	private String blockAccount(String userId) {
		try {
			String response = kcAdminRest.blockAccount(userId);
			if (StringUtil.isNotNullOrEmpty(response) && response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
				return AppConstants.USER_BLOCKED;

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

	/**
	 * Method to change password
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> changePassword(AuthReq authmodel) {

		if (StringUtil.isNullOrEmpty(authmodel.getUserId()) || StringUtil.isNullOrEmpty(authmodel.getPassword())
				|| StringUtil.isNullOrEmpty(authmodel.getNewPassword()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		try {
			GetTokenResponse kcTokenResp = kcTokenRest.getToken(authmodel);
			if (kcTokenResp != null) {
				String response = kcAdminRest.changePassword(authmodel);
				if (StringUtil.isNotNullOrEmpty(response) && response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS))
					return prepareResponse.prepareSuccessMessage(AppConstants.PASSWORD_CHANGED_SUCCESS);
			}
		} catch (ClientWebApplicationException e) {
			if (e.getResponse().getStatus() == 401) {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PASSWORD);
			}
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to generate scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> generateScanner(AuthReq authmodel) {

		if (StringUtil.isNullOrEmpty(authmodel.getUserId()) || StringUtil.isNullOrEmpty(authmodel.getSource()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		try {
			List<GetUserInfoResp> userInfo = new ArrayList<>();
			/** get user info from cache. If does not exist get it from keycloak **/
			if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(authmodel.getUserId())) {
				userInfo = HazelcastConfig.getInstance().getKeycloakUserDetails().get(authmodel.getUserId());
			}

			if (userInfo.get(0).getAttributes() != null
					&& StringUtil.isListNotNullOrEmpty(userInfo.get(0).getAttributes().getMobile())) {

				String response = sendOTP(userInfo.get(0).getAttributes().getMobile().get(0), authmodel.getSource(),
						AppConstants.OTP_MSG, authmodel.getUserId());
				if (response.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
					return prepareResponse.prepareSuccessMessage(AppConstants.OTP_SENT);
				} else {
					return prepareResponse.prepareFailedResponse(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to get scanner
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScanner(AuthReq authReq) {
		/** Validate Request **/
		if (StringUtil.isNullOrEmpty(authReq.getUserId()) || StringUtil.isNullOrEmpty(authReq.getOtp())
				|| StringUtil.isNullOrEmpty(authReq.getSource()))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		try {
			/** validate OTP **/
			String validateResp = validateOTP(authReq.getUserId(), authReq.getSource(), authReq.getOtp());

			if (validateResp.equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
				TotpDetailsEntity totpDetailsEntity = totpRepository.findByUserId(authReq.getUserId());
				/** Get TOTP details for user. If exist reset otherwise create new **/
				if (totpDetailsEntity != null && StringUtil.isNotNullOrEmpty(totpDetailsEntity.getUserId())) {
					totpRepository.deleteById(totpDetailsEntity.getId());
				}
				TotpDetailsEntity totpDetailsEntityNew = appUtils.createScanner(authReq.getUserId());
				if (totpDetailsEntityNew != null) {
					totpDetailsEntityNew = totpRepository.save(totpDetailsEntityNew);
					TotpResponseModel response = new TotpResponseModel();
					response.setScanImge(totpDetailsEntityNew.getImg());
					response.setSecKey(totpDetailsEntityNew.getSecretKey());
					response.setTotpEnabled(false);
					return prepareResponse.prepareSuccessResponseObject(response);
				}
			} else {
				return prepareResponse.prepareFailedResponse(validateResp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to enable TOTP
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq,TOTP
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> enableTotp(AuthReq authReq) {
		try {
			if (StringUtil.isNullOrEmpty(authReq.getTotp()) || StringUtil.isNullOrEmpty(authReq.getUserId())
					|| StringUtil.isNullOrEmpty(authReq.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			TotpDetailsEntity totpDetailsEntity = totpRepository.findByUserId(authReq.getUserId());
			if (totpDetailsEntity != null) {
				int activeStatus = totpDetailsEntity.getActiveStatus();
				String secretKey = totpDetailsEntity.getSecretKey();
				String userName = totpDetailsEntity.getUserId();
				/** IF already enabled **/
				if (activeStatus == 1)
					return prepareResponse.prepareFailedResponse(AppConstants.TOTP_ALREADY_ENABLED);
				if (authReq.getTotp().equalsIgnoreCase(getTOTPCode(secretKey))) {
					int updateTotp = totpRepository.enableTotp(authReq.getUserId(), userName);
					if (updateTotp > 0) {
						setUserPreference(authReq.getUserId(), "TOTP");
						return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_TOPT);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private String getTOTPCode(String secretKey) {
		Base32 base32 = new Base32();
		byte[] bytes = base32.decode(secretKey);
		String hexKey = Hex.encodeHexString(bytes);
		return TOTP.getOTP(hexKey);
	}

	/**
	 * 
	 * Method to verify TOTP
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> verifyTotp(AuthReq authReq) {

		try {
			if (StringUtil.isNullOrEmpty(authReq.getTotp()) || StringUtil.isNullOrEmpty(authReq.getUserId())
					|| StringUtil.isNullOrEmpty(authReq.getSource()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			TotpDetailsEntity totpDetailsEntity = totpRepository.findByUserId(authReq.getUserId());
			if (totpDetailsEntity != null) {
				int activeStatus = totpDetailsEntity.getActiveStatus();
				String secretKey = totpDetailsEntity.getSecretKey();
				if (activeStatus == 0)
					return prepareResponse.prepareFailedResponse(AppConstants.TOTP_NOT_ENABLED);
				if (authReq.getTotp().equalsIgnoreCase(getTOTPCode(secretKey))) {

					/** get user info from cache. If does not exist get it from keycloak **/
					List<GetUserInfoResp> userInfo = new ArrayList<>();
					if (HazelcastConfig.getInstance().getKeycloakUserDetails().containsKey(authReq.getUserId())) {
						userInfo = HazelcastConfig.getInstance().getKeycloakUserDetails().get(authReq.getUserId());
					} else {
						userInfo = kcAdminRest.getUserInfo(authReq.getUserId());
					}
					String userName = "";
					userName = StringUtil.isNotNullOrEmpty(userInfo.get(0).getFirstName())
							? userInfo.get(0).getFirstName()
							: "";
					userName = userName + " "
							+ (StringUtil.isNotNullOrEmpty(userInfo.get(0).getLastName())
									? userInfo.get(0).getLastName()
									: "");
					/** update fcmToken into device mapping **/
					updateDeviceMapping(authReq.getUserId(), authReq.getFcmToken(), authReq.getSource(),
							userName.toUpperCase().trim());

					/** update rest session **/
					updateRestSession(authReq);
					return prepareRepForActiveUser(authReq);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_TOPT);
				}

			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.TOTP_NOT_ENABLED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to set 2FA user preference
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq,TOTP
	 * @return
	 */
	private TwoFAPreferenceEntity setUserPreference(String userId, String TOTP) {
		TwoFAPreferenceEntity twoFAPreferenceEntity = new TwoFAPreferenceEntity();
		try {
			twoFAPreferenceEntity.setUserId(userId);
			twoFAPreferenceEntity.setType(TOTP);
			TwoFAPreferenceEntity entity = twoFARepo.findByUserId(userId);
			if (entity != null && StringUtil.isNotNullOrEmpty(entity.getType())) {
				entity.setType(TOTP);
				entity.setUpdatedBy(userId);
				entity.setActiveStatus(1);
				twoFARepo.save(entity);
				HazelcastConfig.getInstance().getTwoFAUserPreference().put(userId, TOTP);
			} else {
				twoFAPreferenceEntity = twoFARepo.save(twoFAPreferenceEntity);
				HazelcastConfig.getInstance().getTwoFAUserPreference().put(userId, TOTP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return twoFAPreferenceEntity;

	}

	/**
	 * 
	 * Method to load 2FA preference into cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public RestResponse<GenericResponse> loadTwoFAUserPreference() {
		try {
			List<String> userIds = twoFARepo.getUserIds();
			if (StringUtil.isListNullOrEmpty(userIds))
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			HazelcastConfig.getInstance().getTwoFAUserPreference().clear();
			for (String userId : userIds) {
				TwoFAPreferenceEntity entity = twoFARepo.findByUserIdAndActiveStatus(userId, 1);
				if (StringUtil.isNotNullOrEmpty(entity.getType())) {
					HazelcastConfig.getInstance().getTwoFAUserPreference().put(userId, entity.getType());
				}
			}
			return prepareResponse.prepareFailedResponse(AppConstants.SUCCESS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to update device id
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @param deviceId
	 * @param deviceType
	 * @param userName
	 */
	private void updateDeviceMapping(String userId, String deviceId, String deviceType, String userName) {
		DeviceMappingEntity mappingEntity = new DeviceMappingEntity();
		try {
			if (StringUtil.isNotNullOrEmpty(userId) && StringUtil.isNotNullOrEmpty(deviceId)
					&& StringUtil.isNotNullOrEmpty(deviceType) && StringUtil.isNotNullOrEmpty(userName)) {
				DeviceMappingEntity dbMappingEntity = deviceMappingRepository
						.findAllByDeviceIdAndDeviceTypeAndActiveStatus(deviceId, deviceType, 1);
				ExecutorService pool = Executors.newSingleThreadExecutor();
				pool.execute(new Runnable() {
					@Override
					public void run() {
						if (dbMappingEntity != null && StringUtil.isNotNullOrEmpty(dbMappingEntity.getUserId())) {
							if (!dbMappingEntity.getUserId().equalsIgnoreCase(userId)) {
								dbMappingEntity.setUserId(userId);
								dbMappingEntity.setUpdatedBy(userId);
								deviceMappingRepository.saveAndFlush(dbMappingEntity);
							}
						} else {
							mappingEntity.setUserName(userName);
							mappingEntity.setUserId(userId);
							mappingEntity.setDeviceId(deviceId);
							mappingEntity.setDeviceType(deviceType);
							mappingEntity.setCreatedBy(userId);
							deviceMappingRepository.saveAndFlush(mappingEntity);
						}
					}
				});
				pool.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

	}
}

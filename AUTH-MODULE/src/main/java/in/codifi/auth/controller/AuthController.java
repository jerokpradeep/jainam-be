package in.codifi.auth.controller;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.controller.spec.AuthControllerSpec;
import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.model.request.BioMetricReqModel;
import in.codifi.auth.model.request.ForgetPassReq;
import in.codifi.auth.model.request.UnblockReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.service.spec.AuthServiceSpec;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.AppUtils;
import in.codifi.auth.utility.PrepareResponse;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.ws.model.OdinSsoModel;

/**
 * Class for Authentication
 * 
 * @author Dinesh
 *
 */
@Path("/auth")
public class AuthController implements AuthControllerSpec {

	@Inject
	AuthServiceSpec authServiceSpec;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	AppUtils appUtils;

	@Context
	ContainerRequestContext request;

	/**
	 * Service to verify our token from ODIN
	 * 
	 * @author Nesan
	 * @return
	 */
	@Override
	public RestResponse<OdinSsoModel> verifyToken(String token) {

		return authServiceSpec.verifyToken(token);
	}

	/**
	 * 
	 * Method to check user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> verifyClient(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.verifyClient(authReq);
	}

	/**
	 * 
	 * Method to validate password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validatePassword(AuthReq authReq) {

		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.validatePassword(authReq);
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
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.validate2FAOTP(authReq);
	}

	/**
	 * 
	 * Method to send OTP for 2FA verification
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> sendOtpFor2FA(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.sendOtpFor2FA(authReq);
	}

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> passwordReset(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.passwordReset(authReq);
	}

	/**
	 * 
	 * Method to unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> unblock(UnblockReq unblockReq) {
		if (unblockReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.unblock(unblockReq);
	}

	/**
	 * 
	 * Method to resend otp to unblock account
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> resendOtpToUnblock(UnblockReq unblockReq) {
//		if (unblockReq == null)
//			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		return authServiceSpec.resendOtpToUnblock(unblockReq);
//	}

	/**
	 * 
	 * Method to validate OTP to unblock
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateOtpToUnblock(UnblockReq unblockReq) {
		if (unblockReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.validateOtpToUnblock(unblockReq);
	}

	/**
	 * 
	 * Method to verify details to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> forgetPasword(ForgetPassReq forgetPassReq) {
		if (forgetPassReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.forgetPasword(forgetPassReq);
	}

	/**
	 * 
	 * Method to Resend OTP for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param forgetPassReq
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> forgetPwdResendOtp(ForgetPassReq forgetPassReq) {
//		if (forgetPassReq == null)
//			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//		return authServiceSpec.forgetPwdResendOtp(forgetPassReq);
//	}

	/**
	 * 
	 * Method to validate OTP for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateForgetPwdOTP(ForgetPassReq forgetPassReq) {
		if (forgetPassReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.validateForgetPwdOTP(forgetPassReq);
	}

	@Override
	public String test() {
		System.out.println("Reached");
		return "Reached";
	}

	/**
	 * 
	 * Method to change password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> changePassword(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.changePassword(authReq);
	}

	/**
	 * Method to generate scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> generateScanner(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.generateScanner(authReq);
	}

	/**
	 * 
	 * Method to get scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScanner(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.getScanner(authReq);
	}

	/**
	 * 
	 * Method to enable TOTP
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> enableTotp(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.enableTotp(authReq);
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
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.verifyTotp(authReq);
	}

	@Override
	public RestResponse<GenericResponse> email(AuthReq authReq) {
		System.out.println("Method invoked ::: " + new Date());
		return null;
	}

	/**
	 * Method to Log Out From Odin
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> restLogOut() {

//		ClinetInfoModel info = appUtils.getClientInfo();
//		System.out.println(info);
//		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
//			Log.error(AppConstants.CLIENT_INFO_IS_NULL);
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		} else if (StringUtil.isNullOrEmpty(info.getUcc())) {
//			return prepareResponse.prepareFailedResponse(AppConstants.GUEST_USER_ERROR);
//		}
		ClinetInfoModel info = new ClinetInfoModel();
		info.setUserId("WCM549");
		return authServiceSpec.restLogOut(info);
	}

	/**
	 * Method to validate session for bio metric login
	 * 
	 * @author Dinesh Kumar
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateSessionForBioLogin(AuthReq authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		MultivaluedMap<String, String> headers = request.getHeaders();
		String deviceIp = headers.getFirst(AppConstants.X_FORWARDED_FOR);
		return authServiceSpec.validateSessionForBioLogin(authReq, deviceIp);
	}

	/**
	 * Method to enable bio metric login
	 * 
	 * @author Dinesh Kumar
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> enableBioMetric(BioMetricReqModel authReq) {
		if (authReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return authServiceSpec.enableBioMetric(authReq);
	}

	/**
	 * Method to generate QR code
	 * 
	 * @author LOKESH
	 * @return
	 */

	@Override
	public RestResponse<GenericResponse> generateQrcode() {
		return authServiceSpec.generateQrcode();
	}

	/**
	 * Method to validate session for qr login
	 * 
	 * @author Lokesh
	 * @param authReq
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> validateSessionForQrLogin(AuthReq authReq) {
		return authServiceSpec.validateSessionForQrLogin(authReq);
	}

	/**
	 * Method to get token
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getToken() {
		return authServiceSpec.getToken();
	}
}

package in.codifi.auth.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.model.request.BioMetricReqModel;
import in.codifi.auth.model.request.ForgetPassReq;
import in.codifi.auth.model.request.UnblockReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.ws.model.OdinSsoModel;

public interface AuthServiceSpec {

	/**
	 * 
	 * Method to check user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> verifyClient(AuthReq authmodel);

	/**
	 * 
	 * Method to validate password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> validatePassword(AuthReq authmodel);

	/**
	 * 
	 * Method to validate OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> validate2FAOTP(AuthReq authmodel);

	/**
	 * 
	 * Method to send OTP
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> sendOtpFor2FA(AuthReq authmodel);

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> passwordReset(AuthReq authmodel);

	/**
	 * 
	 * Method to verify details to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param forgetPassReq
	 * @return
	 */
	RestResponse<GenericResponse> forgetPasword(ForgetPassReq forgetPassReq);

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
//	RestResponse<GenericResponse> forgetPwdResendOtp(ForgetPassReq forgetPassReq);

	/**
	 * 
	 * Method to validate otp for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> validateForgetPwdOTP(ForgetPassReq forgetPassReq);

	/**
	 * 
	 * Method to unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	RestResponse<GenericResponse> unblock(UnblockReq unblockReq);

	/**
	 * 
	 * Method to resend otp for unblock
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
//	RestResponse<GenericResponse> resendOtpToUnblock(UnblockReq unblockReq);

	/**
	 * 
	 * Method to validate otp for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	RestResponse<GenericResponse> validateOtpToUnblock(UnblockReq unblockReq);

	/**
	 * Method to change password
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> changePassword(AuthReq authmodel);

	/**
	 * Method to generate scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> generateScanner(AuthReq authmodel);

	/**
	 * Method to get scanner
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authReq
	 * @return
	 */
	RestResponse<GenericResponse> getScanner(AuthReq authReq);

	/**
	 * Method to enable totp
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq , totp
	 * @return
	 */
	RestResponse<GenericResponse> enableTotp(AuthReq authReq);

	/**
	 * 
	 * Method to verify TOTP
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq
	 * @return
	 */
	RestResponse<GenericResponse> verifyTotp(AuthReq authReq);

	/**
	 * Method to verfify our token from ODIN req
	 * 
	 * @author Nesan
	 * @return
	 */
	RestResponse<OdinSsoModel> verifyToken(String token);

	/**
	 * Method to Log Out From Odin
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> restLogOut(ClinetInfoModel info);

	/**
	 * 
	 * Method to validate password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	RestResponse<GenericResponse> validateSessionForBioLogin(AuthReq authmodel, String deviceIp);

	/**
	 * Method to enable bio metric for login
	 * 
	 * @author Dinesh Kumar
	 * @param req
	 * @return
	 */
	RestResponse<GenericResponse> enableBioMetric(BioMetricReqModel req);

	/**
	 * Method to generate QR code
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> generateQrcode();

	/**
	 * Method to validate session for qr login
	 * 
	 * @author Lokesh
	 * @param authReq
	 * @return
	 */
	RestResponse<GenericResponse> validateSessionForQrLogin(AuthReq authReq);

	/**
	 * Method to get token
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> getToken();
}

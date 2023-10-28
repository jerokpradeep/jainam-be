package in.codifi.auth.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.request.AuthReq;
import in.codifi.auth.model.request.ForgetPassReq;
import in.codifi.auth.model.request.UnblockReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.orders.ws.model.OdinSsoModel;

public interface AuthControllerSpec {
	
	/**
	 * Service to verfify our token from ODIN
	 * 
	 * @author Nesan
	 * @return
	 */
	@Path("/verifytoken")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<OdinSsoModel> verifyToken(@QueryParam("token") String token);

	/**
	 * 
	 * Method to check user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/client/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> verifyClient(AuthReq authmodel);

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/pwd/validate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> validatePassword(AuthReq authmodel);

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/pwd/reset")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> passwordReset(AuthReq authmodel);

	/**
	 * 
	 * Method to send OTP for 2FA verification
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/otp/send")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> sendOtpFor2FA(AuthReq authmodel);

	/**
	 * 
	 * Method to validate OTP for 2FA
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/otp/validate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> validate2FAOTP(AuthReq authmodel);

	/**
	 * 
	 * Method to verify details to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/pwd/forget")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> forgetPasword(ForgetPassReq forgetPassReq);

	/**
	 * 
	 * Method to Resend OTP for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param forgetPassReq
	 * @return
	 */
//	@Path("/pwd/forget/resend")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	RestResponse<GenericResponse> forgetPwdResendOtp(ForgetPassReq forgetPassReq);

	/**
	 * 
	 * Method to validate OTP for forget password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/pwd/forget/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> validateForgetPwdOTP(ForgetPassReq forgetPassReq);

	/**
	 * 
	 * Method to unblock user
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/client/unblock")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> unblock(UnblockReq unblockReq);

	/**
	 * 
	 * Method to resend otp to unblock account
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
//	@Path("/client/unblock/resend")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	RestResponse<GenericResponse> resendOtpToUnblock(UnblockReq unblockReq);

	/**
	 * 
	 * Method to validate OTP to unblock
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param unblockReq
	 * @return
	 */
	@Path("/client/unblock/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> validateOtpToUnblock(UnblockReq unblockReq);

	@Path("/test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	String test();

	/**
	 * 
	 * Method to reset password
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/pwd/change")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> changePassword(AuthReq authmodel);

	/**
	 * Method to generate scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authmodel
	 * @return
	 */
	@Path("/scanner/generate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> generateScanner(AuthReq authmodel);

	/**
	 * 
	 * Method to get scanner for TOTP
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param authReq
	 * @return
	 */
	@Path("/scanner/get")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getScanner(AuthReq authReq);

	/**
	 * 
	 * Method to enable TOTP
	 * 
	 * @author SOWMIYA
	 *
	 * @param authReq,TOTP
	 * @return
	 */
	@Path("/topt/enable")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
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
	@Path("/topt/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> verifyTotp(AuthReq authReq);
	
	
	@Path("/email")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> email(AuthReq authReq);
}

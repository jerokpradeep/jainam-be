package in.codifi.auth.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.model.request.LoginOTPReq;
import in.codifi.auth.model.request.LoginReq;
import in.codifi.auth.model.request.LoginRestReq;
import in.codifi.auth.model.response.GenericResponse;
import in.codifi.auth.service.spec.ILoginService;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.CodifiUtil;
import in.codifi.auth.utility.PrepareResponse;
import in.codifi.auth.utility.StringUtil;
import in.codifi.auth.ws.service.LoginRestService;
import in.codifi.orders.ws.model.OdinSsoModel;
import in.codifi.orders.ws.model.PlainMessage;
import io.quarkus.logging.Log;

@ApplicationScoped
public class LoginService implements ILoginService {

	@Inject
	LoginRestService loginRestService;

	@Inject
	PrepareResponse prepareResponse;

	DateFormat formatter = new SimpleDateFormat("YYY-MM-DD HH:mm:ss");

	/**
	 * method to login
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> login(LoginReq req) {
		LoginRestReq restReq = new LoginRestReq();
		try {
			restReq.setUser_id(req.getUserId());
			restReq.setLogin_type("PASSWORD");
			restReq.setPassword(req.getPassword());
			restReq.setSecond_auth_type("OTP");
			restReq.setSecond_auth("123456");
			restReq.setApi_key(
					"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9.LzXW_BBI8CJR9clDDTTXezwbr5bnAHJgpPWgI-Tg1RQ");
			restReq.setSource("MOBILEAPI");
			restReq.setUDID("a1b23cd4e5f6g78h");
			restReq.setVersion("2.0.0");
			restReq.setBuild_version("22.11.01");
			restReq.setIosversion("");
			return loginRestService.login(restReq);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to sent otp
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loginOTP(LoginReq req) {
		LoginOTPReq loginOTP = new LoginOTPReq();
		loginOTP.setApi_key(
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDdXN0b21lcklkIjoiMTQwNCIsImV4cCI6MTY4OTA0NDg4MCwiaWF0IjoxNjU3NTA4OTIxfQ.46QVfspRCeq9cz-lLai6panvyD8vOr6dxXCDCril7MM");
		loginOTP.setSource("MOBILEAPI");
		loginOTP.setUser_id(req.getUserId());

		return loginRestService.loginOTP(loginOTP);
	}

	/**
	 * Method to verfify our token from ODIN req
	 * 
	 * @author Nesan
	 * @return
	 */
	@Override
	public RestResponse<OdinSsoModel> verifyToken(String token) {
		PlainMessage plainMessage = new PlainMessage();
		try {
			Date currentDate = new Date();
			if (StringUtil.isNullOrEmpty(token))
				return prepareResponse.prepareOdinResponse(AppConstants.CODE_200, AppConstants.INVALID_PARAMETER,
						plainMessage);

			String ucc = HazelcastConfig.getInstance().getRestUserSession().get(token);
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
	 * method to login with sso
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> ssoLogin(LoginReq req) {
		LoginRestReq restReq = new LoginRestReq();
		try {

			String ssoToken = CodifiUtil.random256Key();
			Log.info("Tok-" + ssoToken);
			if (StringUtil.isNotNullOrEmpty(ssoToken)) {
				HazelcastConfig.getInstance().getRestUserSsoToken().put(ssoToken, req.getUserId());
				restReq.setUser_id(req.getUserId());
				restReq.setLogin_type("TP_TOKEN");
				restReq.setPassword(ssoToken);
				restReq.setSecond_auth("");
				restReq.setApi_key(
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNjkxNzYxMTQwLCJpYXQiOjE2NjAyMjUxOTd9.LzXW_BBI8CJR9clDDTTXezwbr5bnAHJgpPWgI-Tg1RQ");
				restReq.setSource("MOBILEAPI");

				return loginRestService.login(restReq);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

package in.codifi.client.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.client.model.request.TicketRiseReq;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.model.response.PreferredResp;
import in.codifi.client.service.spec.IErpTicketService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ErpTicketService implements IErpTicketService {
	@Inject
	PrepareResponse prepareResponse;

	@Override
	public RestResponse<GenericResponse> raiseTicket(TicketRiseReq req, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(req.getUserId()) && StringUtil.isNotNullOrEmpty(req.getSubject())
					&& StringUtil.isNotNullOrEmpty(req.getDescription())) {

				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("raise Ticket" + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method for preferred
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> preferred(String mobileNo, ClinetInfoModel info) {
		try {
			if (StringUtil.isNotNullOrEmpty(mobileNo)) {
				if (isMobileNumber(mobileNo)) {
					PreferredResp resp = new PreferredResp();
					resp.setPreferredAgent("ganeshprakashs@chola.murugappa.com");
					resp.setPreferredBranch("TNR1");
					return prepareResponse.prepareSuccessResponseObject(resp);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_MOBILE);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("raise Ticket" + e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	public boolean isMobileNumber(String input) {
		Pattern pattern = Pattern.compile("^\\d{10}$"); // Regular expression for a 10-digit mobile number
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

}

package in.codifi.admin.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.controller.spec.LoanControllerSpec;
import in.codifi.admin.model.request.LoanRequestModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.service.spec.LoanServiceSpec;
import in.codifi.admin.utility.PrepareResponse;

@Path("/loan")
public class LoanController implements LoanControllerSpec {

	@Inject
	LoanServiceSpec service;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> getLoanDetailsForHousing(LoanRequestModel model) {
		return service.getLoanDetailsForHousing(model);
	}
//		try {
//			String dateTimeStr = model.getFromDate();
//			String dateTimeStr1 = model.getToDate();
//			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			Date date = dateTimeFormat.parse(dateTimeStr);
//			Date date1 = dateTimeFormat.parse(dateTimeStr1);
//			SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
//			String dayStr = dayFormat.format(date);
//			String dayStr1 = dayFormat.format(date1);
//			Integer number = Integer.valueOf(dayStr);
//			Integer number1 = Integer.valueOf(dayStr1);
//			int value = number - number1;
//			System.out.println(value);
//			if (value <= -7) {
//				return service.getLoanDetailsForHousing(model);
//			} else if (value >= 1 && value <= 7) {
//				return service.getLoanDetailsForHousing(model);
//			} else {
//				return Response.status(Response.Status.OK).entity("Please select seven days only").build();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.error(e.getMessage());
//		}
//
//		return Response.status(Response.Status.BAD_REQUEST).entity("Failed").build();
//	}

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> getLoanDetailsForProperty(LoanRequestModel model) {
		return service.getLoanDetailsForProperty(model);
	}

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> getLoanDetailsForSecurities(LoanRequestModel model) {
		return service.getLoanDetailsForSecurities(model);
	}

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		return service.getLoanDetailsForSmallMediumEnterprises(model);
	}
}

package in.codifi.admin.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.entity.HousingLoanEntity;
import in.codifi.admin.entity.LoanAgainstPropertyEntity;
import in.codifi.admin.entity.LoanAgainstSecuritiesEntity;
import in.codifi.admin.entity.LoanSmallMediumEnterprisesEntity;
import in.codifi.admin.model.request.LoanRequestModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.repository.HousingLoanRepository;
import in.codifi.admin.repository.LoanAgainstPropertyRepository;
import in.codifi.admin.repository.LoanAgainstSecuritiesRepository;
import in.codifi.admin.repository.LoanSmallMediumEnterprisesRepository;
import in.codifi.admin.service.spec.LoanServiceSpec;
import in.codifi.admin.utility.AppConstants;
import in.codifi.admin.utility.PrepareResponse;
import in.codifi.admin.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class LoanService implements LoanServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	HousingLoanRepository housingLoanRepository;
	@Inject
	LoanAgainstPropertyRepository loanAgainstPropertyRepository;
	@Inject
	LoanAgainstSecuritiesRepository loanAgainstSecuritiesRepository;
	@Inject
	LoanSmallMediumEnterprisesRepository loanSmallMediumEnterprisesRepository;

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getLoanDetailsForHousing(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<HousingLoanEntity> response = housingLoanRepository.getReturns(fromDate1, toDate1);

			if (StringUtil.isListNotNullOrEmpty(response)) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getLoanDetailsForProperty(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanAgainstPropertyEntity> response = loanAgainstPropertyRepository.getReturns(fromDate1, toDate1);
			if (StringUtil.isListNotNullOrEmpty(response)) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getLoanDetailsForSecurities(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanAgainstSecuritiesEntity> response = loanAgainstSecuritiesRepository.getReturns(fromDate1, toDate1);
			if (StringUtil.isListNotNullOrEmpty(response)) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanSmallMediumEnterprisesEntity> response = loanSmallMediumEnterprisesRepository.getReturns(fromDate1,
					toDate1);
			if (StringUtil.isListNotNullOrEmpty(response)) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareSuccessResponseObject(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

}

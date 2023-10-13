package in.codifi.loan.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.loan.entity.HousingLoanEntity;
import in.codifi.loan.entity.LoanAgainstPropertyEntity;
import in.codifi.loan.entity.LoanAgainstSecuritiesEntity;
import in.codifi.loan.entity.LoanSmallMediumEnterprisesEntity;
import in.codifi.loan.model.request.LoanRequestModel;
import in.codifi.loan.model.response.GenericResponse;
import in.codifi.loan.model.response.LoanResponseModel;
import in.codifi.loan.repository.HousingLoanRepository;
import in.codifi.loan.repository.LoanAgainstPropertyRepository;
import in.codifi.loan.repository.LoanAgainstSecuritiesRepository;
import in.codifi.loan.repository.LoanSmallMediumEnterprisesRepository;
import in.codifi.loan.service.spec.LoanServiceSpec;
import in.codifi.loan.utility.AppConstants;
import in.codifi.loan.utility.PrepareResponse;
import in.codifi.loan.utility.StringUtil;
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
	 * method to add loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> addLoanDetailsForHousing(LoanRequestModel model) {
		try {
			if (!validateCartParam(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			HousingLoanEntity Entity = new HousingLoanEntity();
			Entity.setName(model.getName());
			Entity.setClientId(model.getClientId());
			Entity.setMobileNo(model.getMobileNo());
			Entity.setEmailId(model.getEmailId());
			Entity.setLoanAmount(model.getLoanAmount());
			Entity.setIncomeRange(model.getIncomeRange());
			Entity.setLoanRequiredFor(model.getLoanRequiredFor());

			HousingLoanEntity entity = housingLoanRepository.save(Entity);
			if (entity != null) {
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to add loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> addLoanDetailsForProperty(LoanRequestModel model) {
		try {
			if (!validateCartParam(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			LoanAgainstPropertyEntity Entity = new LoanAgainstPropertyEntity();
			Entity.setName(model.getName());
			Entity.setClientId(model.getClientId());
			Entity.setMobileNo(model.getMobileNo());
			Entity.setEmailId(model.getEmailId());
			Entity.setLoanAmount(model.getLoanAmount());
			Entity.setIncomeRange(model.getIncomeRange());
			Entity.setLoanRequiredFor(model.getLoanRequiredFor());

			LoanAgainstPropertyEntity entity = loanAgainstPropertyRepository.save(Entity);
			if (entity != null) {
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to add loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> addLoanDetailsForSecurities(LoanRequestModel model) {
		try {
			if (!validateCartParam(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			LoanAgainstSecuritiesEntity Entity = new LoanAgainstSecuritiesEntity();
			Entity.setName(model.getName());
			Entity.setClientId(model.getClientId());
			Entity.setMobileNo(model.getMobileNo());
			Entity.setEmailId(model.getEmailId());
			Entity.setLoanAmount(model.getLoanAmount());
			Entity.setIncomeRange(model.getIncomeRange());
			Entity.setLoanRequiredFor(model.getLoanRequiredFor());

			LoanAgainstSecuritiesEntity entity = loanAgainstSecuritiesRepository.save(Entity);
			if (entity != null) {
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to add loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> addLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		try {
			if (!validateCartParam(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			LoanSmallMediumEnterprisesEntity Entity = new LoanSmallMediumEnterprisesEntity();
			Entity.setName(model.getName());
			Entity.setClientId(model.getClientId());
			Entity.setMobileNo(model.getMobileNo());
			Entity.setEmailId(model.getEmailId());
			Entity.setLoanAmount(model.getLoanAmount());
			Entity.setIncomeRange(model.getIncomeRange());
			Entity.setLoanRequiredFor(model.getLoanRequiredFor());

			LoanSmallMediumEnterprisesEntity entity = loanSmallMediumEnterprisesRepository.save(Entity);
			if (entity != null) {
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate loan details
	 * 
	 * @author LOKESH
	 * @return
	 */
	private boolean validateCartParam(LoanRequestModel model) {
		if (StringUtil.isNotNullOrEmpty(model.getClientId())) {
			return true;
		}
		return false;
	}

	/**
	 * method to get loan details for housing
	 * 
	 * @author LOKESH
	 */
	@Override
	public Response getLoanDetailsForHousing(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<HousingLoanEntity> response = housingLoanRepository.getReturns(fromDate1, toDate1);
			List<LoanResponseModel> loanResponseModel = PrepareLoanDetailsResponseForHousing(response);

			StringBuilder fileContent = new StringBuilder();
			fileContent.append("ID|NAME|CLIENTID|MOBILENO|EMAILID|LOANAMOUNT|INCOMERANGE|LOANREQUIREDFOR|\n");

			for (LoanResponseModel rSet : loanResponseModel) {
				String data = String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", 
						rSet.getId(),
						rSet.getName(),
						rSet.getClientId(),
						rSet.getMobileNo(), 
						rSet.getEmailId(), 
						rSet.getLoanAmount(),
						rSet.getIncomeRange(), 
						rSet.getLoanRequiredFor());
						fileContent.append(data);
			}

			String fileName = "HouseLoanDetails";

			try {
				File outputFile = File.createTempFile(fileName, ".txt");
				try (OutputStream outputStream = new FileOutputStream(outputFile)) {
					outputStream.write(fileContent.toString().getBytes(StandardCharsets.UTF_8));
				}
				return Response.ok(outputFile)
						.header("Content-Disposition", "attachment; filename=" + fileName + ".txt").build();
			} catch (IOException e) {
				// Handle the exception
				return Response.serverError().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return Response.serverError().build();

	}

	private List<LoanResponseModel> PrepareLoanDetailsResponseForHousing(List<HousingLoanEntity> response) {
		List<LoanResponseModel> responses = new ArrayList<>();
		for(HousingLoanEntity rSet : response) {
			LoanResponseModel result = new LoanResponseModel();
			result.setId(rSet.getId());
			result.setName(rSet.getName());
			result.setClientId(rSet.getClientId());
			result.setMobileNo(rSet.getMobileNo());
			result.setEmailId(rSet.getEmailId());
			result.setLoanAmount(rSet.getLoanAmount());
			result.setIncomeRange(rSet.getIncomeRange());
			result.setLoanRequiredFor(rSet.getLoanRequiredFor());
			
			responses.add(result);
		}
		return responses;
	}
	
	
	
	/**
	 * method to get loan details for Property
	 * 
	 * @author LOKESH
	 */
	@Override
	public Response getLoanDetailsForProperty(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanAgainstPropertyEntity> response = loanAgainstPropertyRepository.getReturns(fromDate1, toDate1);
			List<LoanResponseModel> loanResponseModel = PrepareLoanDetailsResponseForProperty(response);

			StringBuilder fileContent = new StringBuilder();
			fileContent.append("ID|NAME|CLIENTID|MOBILENO|EMAILID|LOANAMOUNT|INCOMERANGE|LOANREQUIREDFOR|\n");

			for (LoanResponseModel rSet : loanResponseModel) {
				String data = String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", 
						rSet.getId(),
						rSet.getName(),
						rSet.getClientId(),
						rSet.getMobileNo(), 
						rSet.getEmailId(), 
						rSet.getLoanAmount(),
						rSet.getIncomeRange(), 
						rSet.getLoanRequiredFor());
						fileContent.append(data);
			}

			String fileName = "HouseLoanDetails";

			try {
				File outputFile = File.createTempFile(fileName, ".txt");
				try (OutputStream outputStream = new FileOutputStream(outputFile)) {
					outputStream.write(fileContent.toString().getBytes(StandardCharsets.UTF_8));
				}
				return Response.ok(outputFile)
						.header("Content-Disposition", "attachment; filename=" + fileName + ".txt").build();
			} catch (IOException e) {
				// Handle the exception
				return Response.serverError().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return Response.serverError().build();

	}

	private List<LoanResponseModel> PrepareLoanDetailsResponseForProperty(List<LoanAgainstPropertyEntity> response) {
		List<LoanResponseModel> responses = new ArrayList<>();
		for(LoanAgainstPropertyEntity rSet : response) {
			LoanResponseModel result = new LoanResponseModel();
			result.setId(rSet.getId());
			result.setName(rSet.getName());
			result.setClientId(rSet.getClientId());
			result.setMobileNo(rSet.getMobileNo());
			result.setEmailId(rSet.getEmailId());
			result.setLoanAmount(rSet.getLoanAmount());
			result.setIncomeRange(rSet.getIncomeRange());
			result.setLoanRequiredFor(rSet.getLoanRequiredFor());
			
			responses.add(result);
		}
		return responses;
	}
	
	
	
	
	/**
	 * method to get loan details for Securities
	 * 
	 * @author LOKESH
	 */
	@Override
	public Response getLoanDetailsForSecurities(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanAgainstSecuritiesEntity> response = loanAgainstSecuritiesRepository.getReturns(fromDate1, toDate1);
			List<LoanResponseModel> loanResponseModel = PrepareLoanDetailsResponseForSecurities(response);

			StringBuilder fileContent = new StringBuilder();
			fileContent.append("ID|NAME|CLIENTID|MOBILENO|EMAILID|LOANAMOUNT|INCOMERANGE|LOANREQUIREDFOR|\n");

			for (LoanResponseModel rSet : loanResponseModel) {
				String data = String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", 
						rSet.getId(),
						rSet.getName(),
						rSet.getClientId(),
						rSet.getMobileNo(), 
						rSet.getEmailId(), 
						rSet.getLoanAmount(),
						rSet.getIncomeRange(), 
						rSet.getLoanRequiredFor());
						fileContent.append(data);
			}

			String fileName = "HouseLoanDetails";

			try {
				File outputFile = File.createTempFile(fileName, ".txt");
				try (OutputStream outputStream = new FileOutputStream(outputFile)) {
					outputStream.write(fileContent.toString().getBytes(StandardCharsets.UTF_8));
				}
				return Response.ok(outputFile)
						.header("Content-Disposition", "attachment; filename=" + fileName + ".txt").build();
			} catch (IOException e) {
				// Handle the exception
				return Response.serverError().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return Response.serverError().build();

	}

	private List<LoanResponseModel> PrepareLoanDetailsResponseForSecurities(List<LoanAgainstSecuritiesEntity> response) {
		List<LoanResponseModel> responses = new ArrayList<>();
		for(LoanAgainstSecuritiesEntity rSet : response) {
			LoanResponseModel result = new LoanResponseModel();
			result.setId(rSet.getId());
			result.setName(rSet.getName());
			result.setClientId(rSet.getClientId());
			result.setMobileNo(rSet.getMobileNo());
			result.setEmailId(rSet.getEmailId());
			result.setLoanAmount(rSet.getLoanAmount());
			result.setIncomeRange(rSet.getIncomeRange());
			result.setLoanRequiredFor(rSet.getLoanRequiredFor());
			
			responses.add(result);
		}
		return responses;
	}
	
	
	
	/**
	 * method to get loan details for SmallMediumEnterprises
	 * 
	 * @author LOKESH
	 */
	@Override
	public Response getLoanDetailsForSmallMediumEnterprises(LoanRequestModel model) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fromDate1 = inputFormat.parse(model.getFromDate());
			Date toDate1 = inputFormat.parse(model.getToDate());
			List<LoanSmallMediumEnterprisesEntity> response = loanSmallMediumEnterprisesRepository.getReturns(fromDate1, toDate1);
			List<LoanResponseModel> loanResponseModel = PrepareLoanDetailsResponseForSmallMediumEnterprises(response);

			StringBuilder fileContent = new StringBuilder();
			fileContent.append("ID|NAME|CLIENTID|MOBILENO|EMAILID|LOANAMOUNT|INCOMERANGE|LOANREQUIREDFOR|\n");

			for (LoanResponseModel rSet : loanResponseModel) {
				String data = String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", 
						rSet.getId(),
						rSet.getName(),
						rSet.getClientId(),
						rSet.getMobileNo(), 
						rSet.getEmailId(), 
						rSet.getLoanAmount(),
						rSet.getIncomeRange(), 
						rSet.getLoanRequiredFor());
						fileContent.append(data);
			}

			String fileName = "HouseLoanDetails";

			try {
				File outputFile = File.createTempFile(fileName, ".txt");
				try (OutputStream outputStream = new FileOutputStream(outputFile)) {
					outputStream.write(fileContent.toString().getBytes(StandardCharsets.UTF_8));
				}
				return Response.ok(outputFile)
						.header("Content-Disposition", "attachment; filename=" + fileName + ".txt").build();
			} catch (IOException e) {
				// Handle the exception
				return Response.serverError().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return Response.serverError().build();

	}

	private List<LoanResponseModel> PrepareLoanDetailsResponseForSmallMediumEnterprises(List<LoanSmallMediumEnterprisesEntity> response) {
		List<LoanResponseModel> responses = new ArrayList<>();
		for(LoanSmallMediumEnterprisesEntity rSet : response) {
			LoanResponseModel result = new LoanResponseModel();
			result.setId(rSet.getId());
			result.setName(rSet.getName());
			result.setClientId(rSet.getClientId());
			result.setMobileNo(rSet.getMobileNo());
			result.setEmailId(rSet.getEmailId());
			result.setLoanAmount(rSet.getLoanAmount());
			result.setIncomeRange(rSet.getIncomeRange());
			result.setLoanRequiredFor(rSet.getLoanRequiredFor());
			
			responses.add(result);
		}
		return responses;
	}

}
	

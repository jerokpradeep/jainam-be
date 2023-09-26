package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.api.controller.spec.IMarketWatchController;
import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.ValidateRequestService;
import in.codifi.api.service.spec.IMarketWatchService;
import in.codifi.api.util.AppConstants;
import in.codifi.api.util.PrepareResponse;

/**
 * Class for Market Watch Controller
 * 
 * @author Gowrisankar
 *
 */
@Path("/marketWatch")
public class MarketWatchController implements IMarketWatchController {

	@Inject
	IMarketWatchService codifiMwService;
	@Inject
	ValidateRequestService validateRequestService;
	@Inject
	PrepareResponse prepareResponse;

	@Override
	public RestResponse<ResponseModel> getAllMwScripsMob(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.getAllMwScripsMob(pDto.getUserId(), pDto.isPredefined());
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to provide the user scrips details from cache or data base
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> getAllMwScrips(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.getAllMwScrips(pDto.getUserId());
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> getMWScrips(MwRequestModel pDto) {
//		boolean isValid = validateRequestService.isValidUser(pDto);
		boolean isValid = true;
		if (isValid) {
			return codifiMwService.getMWScrips(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * 
	 * Method to get scrips details for mobile user based on predefine MW
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> getMWScripsForMob(MwRequestModel pDto) {
		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.getMWScripsForMob(pDto, pDto.isPredefined());
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> deletescrip(MwRequestModel pDto) {
		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.deletescrip(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to add the scrip into cache and data base
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> addscrip(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.addscrip(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to sort the scrip into cache and data base
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> sortMwScrips(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.sortMwScrips(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to create the new marketWatch
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public RestResponse<ResponseModel> createMW(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
		if (isValid) {
			return codifiMwService.createMW(pDto.getUserId());
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * Method to rename market watch
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public RestResponse<ResponseModel> renameMarketWatch(MwRequestModel pDto) {

		boolean isValid = validateRequestService.isValidUser(pDto);
//		boolean isValid = true;
		if (isValid) {
			return codifiMwService.renameMarketWatch(pDto);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		}
	}

	/**
	 * 
	 * Method to Delete expired scrips in MW List
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> deleteExpiredContract() {
		return codifiMwService.deleteExpiredContract();
	}

//	/**
//	 * method get all market watch scrips
//	 * 
//	 * @author sowmiya
//	 * @return
//	 */
//	public RestResponse<ResponseModel> getAllMwScripsAdvanced(MWReqModel reqModel) {
////		boolean isValid = validateRequestService.isValidUser(pDto);
////		if (isValid) {
//			return codifiMwService.getAllMwScripsAdvanced(reqModel);
////		} else {
////			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
////		}
//
//	}

}

package in.codifi.api.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.ResponseModel;

public interface IMarketWatchService {

	/**
	 * To get scrip details for given user id
	 * 
	 * @author Gowrisankar
	 * @param predefined
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> getAllMwScripsMob(String pUserId, boolean predefined);

	/**
	 * Load the contract master from data base from Cache
	 * 
	 * @author Gowrisankar
	 * @return
	 */
//	public RestResponse<ResponseModel> loadContractMaster();

	/**
	 * To get scrip details for given user id
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> getAllMwScrips(String pUserId);

	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> getMWScrips(MwRequestModel pDto);

	/**
	 * 
	 * Method to get scrips details for mobile user based on predefine MW
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @param predefined
	 * @return
	 */
	public RestResponse<ResponseModel> getMWScripsForMob(MwRequestModel pDto, boolean predefined);

	/**
	 * 
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> deletescrip(MwRequestModel pDto);

	/**
	 * Method to add the scrip into cache and data base
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> addscrip(MwRequestModel pDto);

	/**
	 * 
	 * Method to Sort MW scrips
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> sortMwScrips(MwRequestModel pDto);

	/**
	 * Method to create the new marketWatch
	 * 
	 * @author dinesh Kumar
	 */
	public RestResponse<ResponseModel> createMW(String pUserId);

	/**
	 * Method to change market watch name
	 * 
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> renameMarketWatch(MwRequestModel pDto);

	/**
	 * 
	 * Method to Delete expired contract in MW
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	RestResponse<ResponseModel> deleteExpiredContract();

//	/**
//	 * method to get all market watch scrips for advanced logic
//	 * 
//	 * @author sowmiya
//	 * 
//	 * @param reqModel
//	 * @return
//	 */
//	public RestResponse<ResponseModel> getAllMwScripsAdvanced(MWReqModel reqModel);

}

package in.codifi.mw.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.model.request.MwRequestModel;
import in.codifi.mw.model.response.GenericResponse;

public interface IMarketWatchService {

	/**
	 * To get scrip details for given user id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getAllMwScrips(String pUserId);
	
	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> getMWScrips(MwRequestModel reqModel);
	
	/**
	 * 
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> deletescrip(MwRequestModel reqModel);
	
	/**
	 * Method to add the scrip into cache and data base
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> addscrip(MwRequestModel reqModel);
	
	/**
	 * Method to Sort MW scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> sortMwScrips(MwRequestModel reqModel);
	
	/**
	 * Method to create the new marketWatch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> createMW(String pUserId);
	
	/**
	 * Method to change market watch name
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> renameMarketWatch(MwRequestModel reqModel);
	
	/**
	 * Method to Delete expired contract in MW
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> deleteExpiredContract();

}

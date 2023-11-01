package in.codifi.scrips.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.scrips.model.response.GenericResponse;

public interface ContractServiceSpecs {

	/**
	 * Method to load contract master
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	RestResponse<GenericResponse> loadContractMaster();

	/**
	 * Delete Expired contract manually
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	RestResponse<GenericResponse> deleteExpiredContract();

	/**
	 * 
	 * Method to Delete BSE Contract
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	RestResponse<GenericResponse> deleteBSEContract();

	/**
	 * Method to get reload contract master file from server
	 * 
	 * @author Nesan
	 *
	 * @return
	 */
	RestResponse<GenericResponse> reloadContractMasterFile();

	/**
	 * Method to Load fiftytwoWeekData
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> loadFiftytwoWeekData();

	/**
	 * method to load MTF data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	RestResponse<GenericResponse> loadMTFData();

	/**
	 * method to add index value
	 * 
	 * @author LOKESH
	 */
	RestResponse<GenericResponse> addIndexValue();

	/**
	 * Method to create Archive Table For Contract Master
	 * 
	 * @author LOKESH
	 * @return
	 */
	RestResponse<GenericResponse> createArchiveTableForContractMaster();

}

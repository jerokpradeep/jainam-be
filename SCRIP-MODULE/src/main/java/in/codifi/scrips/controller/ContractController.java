package in.codifi.scrips.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.scrips.controller.spec.ContractControllerSpec;
import in.codifi.scrips.model.response.GenericResponse;
import in.codifi.scrips.service.spec.ContractServiceSpecs;

@Path("/contract")
public class ContractController implements ContractControllerSpec {

	@Inject
	ContractServiceSpecs service;

	/**
	 * Method to load contract master into cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loadContractMaster() {
		return service.loadContractMaster();
	}

	/**
	 * method to load MTF data
	 * 
	 * @author SOWMIYA
	 * @return
	 */
	public RestResponse<GenericResponse> loadMTFData() {
		return service.loadMTFData();
	}

	/**
	 * Delete Expired contract manually
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteExpiredContract() {
		return service.deleteExpiredContract();
	}

	/**
	 * Delete Delete BSE contract
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteBSEContract() {
		return service.deleteBSEContract();
	}

	/**
	 * Method to reload contract master file from server
	 * 
	 * @author Nesan
	 * 
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> reloadContractMasterFile() {
		return service.reloadContractMasterFile();
	}

	/**
	 * Method to Load fiftytwoWeekData
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loadFiftytwoWeekData() {
		return service.loadFiftytwoWeekData();
	}

	/**
	 * method to add index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> addIndexValue() {
		return service.addIndexValue();
	}

	/**
	 * Method to create Archive Table For Contract Master
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createArchiveTableForContractMaster() {
		return service.createArchiveTableForContractMaster();
	}
}

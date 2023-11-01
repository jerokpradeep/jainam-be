package in.codifi.scrips.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.scrips.model.response.GenericResponse;

public interface ContractControllerSpec {

	@Path("/reloadcache")
	@POST
	RestResponse<GenericResponse> loadContractMaster();

	/**
	 * method to load MTF data
	 * 
	 * @author SOWMIYA
	 */
	@Path("/mtf/reloadcache")
	@GET
	RestResponse<GenericResponse> loadMTFData();

	/**
	 * 
	 * Method to Delete expired contract
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Path("/delete/expired")
	@POST
	RestResponse<GenericResponse> deleteExpiredContract();

	/**
	 * Delete Delete BSE contract
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Path("/delete/bse")
	@GET
	RestResponse<GenericResponse> deleteBSEContract();

	/**
	 * Method to get reload - contract master file from server
	 * 
	 * @author Nesan
	 *
	 * @return
	 */
	@Path("/reload/contractmaster")
	@GET
	RestResponse<GenericResponse> reloadContractMasterFile();

	/**
	 * Method to Load fiftytwoWeekData
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Path("/load/fiftytwoWeekData")
	@GET
	RestResponse<GenericResponse> loadFiftytwoWeekData();

	/**
	 * method to add index value
	 * 
	 * @author LOKESH
	 */
	@Path("/add/indexvalue")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> addIndexValue();

	/**
	 * Method to create Archive Table For Contract Master
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/createArchiveTableforContractmaster")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> createArchiveTableForContractMaster();

}

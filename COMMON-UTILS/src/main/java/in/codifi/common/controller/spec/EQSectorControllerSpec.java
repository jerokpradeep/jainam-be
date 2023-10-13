package in.codifi.common.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.entity.primary.EQSectorMappingEntity;
import in.codifi.common.model.request.MapReqModel;
import in.codifi.common.model.request.SectorDetailsReq;
import in.codifi.common.model.response.GenericResponse;

public interface EQSectorControllerSpec {
	
	/**
	 * Method to get sectors
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getSector")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSector();
	
	/**
	 * Method to get Sector Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/getSectorDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getSectorDetails(SectorDetailsReq req);

	/**
	 * Method to get EQSector data
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getEQSector();

	/**
	 * Method to load EQSector data
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Path("/load")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> insertEQSectorData();

	/**
	 * Method to add mapping scrips
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Path("/map/add")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> addMappingScrips(List<EQSectorMappingEntity> entities);

	/**
	 * Method to delete mapping scrips
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Path("/map/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteMappingScrips(MapReqModel request);

	/**
	 * Method to delete mapping scrips
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Path("/map/scrips")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getMappingScrips();

}

package in.codifi.admin.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.UrlRequestModel;
import in.codifi.admin.model.response.AccesslogResponseModel;
import in.codifi.admin.model.response.GenericResponse;

public interface AdminLogsControllerSpec {

	/**
	 * Method to get the total logged in details for past days
	 * 
	 * @author LOKESH
	 * @param requestContext
	 * @return
	 */
	@Path("/userLogDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> userLogDetails();

	/**
	 * method to get the user based records from the data base (TOP 10 USER)
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/getUserbasedRecords")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUserbasedRecords(AccesslogResponseModel accessModel);

	/**
	 * method to get the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/getUrlBasedRecords")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUrlBasedRecords();

//	/**
//	 * Method to get last 12 hour login count
//	 * 
//	 * @author LOKESH
//	 * @return
//	 */
//	@Path("/getLast12hourLoginCount")
//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public RestResponse<GenericResponse> getLast12hourLoginCount();

	/**
	 * Method to get distinct url for drop down
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/getDistinctUrl")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getDistinctUrl();

	/**
	 * method to get the url based records 1
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/getUrlBasedRecords1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUrlBasedRecords1();

	/**
	 * method to get the url record
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/getUrlRecord")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUrlRecord(UrlRequestModel model);

	/**
	 * method to Insert the Login record per day
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/insert/loginRecord")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getLoginRecord();

	/**
	 * method to get user record mob
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/userecord/mob")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUserRecordMOb();

	/**
	 * method to get user record Web
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/userecord/web")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUserRecordWeb();

	/**
	 * method to get user record API
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/userecord/api")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUserRecordApi();

	/**
	 * method to get Unique UserId
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Path("/userecord/UniqueUser")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getUniqueUserId();

}

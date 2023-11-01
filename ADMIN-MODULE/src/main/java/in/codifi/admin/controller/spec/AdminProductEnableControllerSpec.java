package in.codifi.admin.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.request.IndexRequestModel;
import in.codifi.admin.model.request.PreferenceRequestModel;
import in.codifi.admin.model.response.GenericResponse;

public interface AdminProductEnableControllerSpec {

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	@Path("/update/mtfpreference")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> updatePreference(PreferenceRequestModel model);

	/**
	 * method to get Preference value
	 * 
	 * @author LOKESH
	 */
	@Path("/get/mtfpreference")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getPreference();
	
	/**
	 * Method to load Admin Preference
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Path("/load/adminPreference")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> loadAdminPreference();

	/**
	 * method to update index value
	 * 
	 * @author LOKESH
	 */
	@Path("/update/indexvalue")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> updateIndexValue(IndexRequestModel model);

	/**
	 * method to get index value
	 * 
	 * @author LOKESH
	 */
	@Path("/get/indexvalue")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getIndexValue();
	
	/**
	 * method to truncate index value
	 * 
	 * @author LOKESH
	 */
	@Path("/truncate/indexvalue")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> truncateIndexValue();
}

package in.codifi.common.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.model.request.NFOFutureReqModel;
import in.codifi.common.model.response.GenericResponse;

public interface NFOFutureControllerSpec {
	/**
	 * method to get nfo future details
	 * 
	 * @author sowmiya
	 * @param reqModel
	 * @return
	 */
	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<GenericResponse> getNFOFutureDetails(NFOFutureReqModel reqModel);

	/**
	 * method load nfo future
	 * 
	 * @author sowmiya
	 * @return
	 */
	@Path("/load")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> loadNFOFuture();

}

package in.codifi.common.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.controller.spec.VersionControllerSpec;
import in.codifi.common.entity.primary.VersionEntity;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.service.spec.VersionServiceSpec;

@Path("/version")
public class VersionController implements VersionControllerSpec {
	
	@Inject
	VersionServiceSpec analysisServiceSpec;
	
	/**
	 * method to verify version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> findVersion(VersionEntity versionEntity) {
		return analysisServiceSpec.findVersion(versionEntity);
	}
	
	/**
	 * Method to update version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateVersion(VersionEntity versionEntity) {
		return analysisServiceSpec.updateVersion(versionEntity);
	}

}

package in.codifi.common.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.entity.primary.VersionEntity;
import in.codifi.common.model.response.GenericResponse;

public interface VersionServiceSpec {
	
	/**
	 * method to get verify version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> findVersion(VersionEntity versionEntity);
	
	/**
	 * Method to update version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> updateVersion(VersionEntity versionEntity);

}

package in.codifi.common.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.model.response.GenericResponse;

public interface IndicesServiceSpec {

	/**
	 * Method to get indices details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getIndices();

	/**
	 * Method to Add indices data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> insertIndicesData();

}

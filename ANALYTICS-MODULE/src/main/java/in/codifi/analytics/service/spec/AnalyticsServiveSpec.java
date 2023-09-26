package in.codifi.analytics.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.analytics.model.request.AnalyticsRequest;
import in.codifi.analytics.model.response.GenericResponse;

public interface AnalyticsServiveSpec {

	/**
	 * Method to add Research Call
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> addResearchCall(AnalyticsRequest req);

}

package in.codifi.analytics.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.analytics.controller.spec.AnalyticsControllerSpec;
import in.codifi.analytics.model.request.AnalyticsRequest;
import in.codifi.analytics.model.response.GenericResponse;
import in.codifi.analytics.service.spec.AnalyticsServiveSpec;

@Path("/analytics")
public class AnalyticsController implements AnalyticsControllerSpec {
	
	@Inject
	AnalyticsServiveSpec analyticsServiveSpec;
	
	/**
	 * Method to add Research Call
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addResearchCall(AnalyticsRequest req) {
		return analyticsServiveSpec.addResearchCall(req);
	}

}

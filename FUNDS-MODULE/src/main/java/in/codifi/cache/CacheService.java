package in.codifi.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.funds.config.HazelcastConfig;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.PrepareResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class CacheService {

	@Inject
	PrepareResponse prepareResponse;

	public RestResponse<GenericResponse> clearAllCache() {
		try {
			HazelcastConfig.getInstance().getPaymentDetails().clear();
			HazelcastConfig.getInstance().getIfscCodeMapping().clear();
			return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

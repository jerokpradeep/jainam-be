package in.codifi.mw.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.config.HazelCacheController;
import in.codifi.mw.model.response.CacheMwDetailsModel;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.repository.MarketWatchEntityManager;
import in.codifi.mw.service.spec.ICacheService;
import in.codifi.mw.utility.AppConstants;
import in.codifi.mw.utility.PrepareResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class Cacheservice implements ICacheService {

	@Inject
	MarketWatchEntityManager entityManager;
	@Inject
	MarketWatchService marketWatchService;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * Method to load user mw data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> loadUserMWData() {
		try {
			/** take the scrip details from the Data base for the All user */
			List<CacheMwDetailsModel> scripDetails = entityManager.getAllMarketWatch();
			if (scripDetails != null && scripDetails.size() > 0) {
				HazelCacheController.getInstance().getMwListByUserId().clear();
				marketWatchService.populateFieldsMWForAll(scripDetails);
				prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

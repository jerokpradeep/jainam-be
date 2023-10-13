package in.codifi.mw.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.config.HazelCacheController;
import in.codifi.mw.entity.primary.TickerTapeEntity;
import in.codifi.mw.model.request.ReqModel;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.repository.TickerTapeRepository;
import in.codifi.mw.service.spec.ITickerTapeService;
import in.codifi.mw.utility.AppConstants;
import in.codifi.mw.utility.PrepareResponse;
import io.quarkus.logging.Log;

public class TickerTapeService implements ITickerTapeService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	TickerTapeRepository tickerTapeRepository;

	/**
	 * Method to get ticker tape scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getTicketTapeScrips(ReqModel model) {
		List<TickerTapeEntity> entity = new ArrayList<>();
		try {

			// TODO need to change to actual user
			String userId = AppConstants.TEMP_USER_ID;

			/** Check if data exist in cache or not **/
			entity = HazelCacheController.getInstance().getTickerTapeList().get(userId);

			/** if data exist in cache then return **/
			if (entity != null && entity.size() > 0)
				return prepareResponse.prepareSuccessResponseObject(entity);

			/** If data does not exist in cache load it and return **/
			entity = loadTickerTapeScripsByUserId(userId);
			if (entity != null && entity.size() > 0)
				return prepareResponse.prepareSuccessResponseObject(entity);

			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to load ticker tape scrips for user into cache by user id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<TickerTapeEntity> loadTickerTapeScripsByUserId(String userId) {
		List<TickerTapeEntity> entities = new ArrayList<>();
		try {
			entities = tickerTapeRepository.findByUserId(userId);
			if (entities != null && entities.size() > 0) {
				HazelCacheController.getInstance().getTickerTapeList().remove(userId);
				HazelCacheController.getInstance().getTickerTapeList().put(userId, entities);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return entities;
	}

}

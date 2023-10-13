package in.codifi.common.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.NFOFutureEntity;
import in.codifi.common.model.request.NFOFutureReqModel;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.NfoFutureResponseModel;
import in.codifi.common.model.response.NfoFutureScripsResponseModel;
import in.codifi.common.repository.NFOFutureRepository;
import in.codifi.common.service.spec.NFOFutureServiceSpec;
import in.codifi.common.utility.AppConstants;
import io.quarkus.logging.Log;

@ApplicationScoped
public class NFOFutureService implements NFOFutureServiceSpec {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	NFOFutureRepository nFOFutureRepo;

	@Override
	public RestResponse<GenericResponse> getNFOFutureDetails(NFOFutureReqModel reqModel) {
		NfoFutureResponseModel futureDetails = new NfoFutureResponseModel();
		try {
			if (HazelcastConfig.getInstance().getNfoFutureDetails().get(reqModel.getSymbol()) != null) {
				futureDetails = HazelcastConfig.getInstance().getNfoFutureDetails().get(reqModel.getSymbol());
				return prepareResponse.prepareSuccessResponseObject(futureDetails);
			} else {
				List<NFOFutureEntity> futureEntity = nFOFutureRepo.findByUnderlying(reqModel.getSymbol());
				if (futureEntity != null && futureEntity.size() > 0) {
					futureDetails = prepareNFOFutureDetails(futureEntity);
					HazelcastConfig.getInstance().getNfoFutureDetails().put(reqModel.getSymbol(), futureDetails);
					return prepareResponse.prepareSuccessResponseObject(futureDetails);

				} else {
					prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
				}
			}

		} catch (Exception e) {
			Log.error("getNFOFutureDetails", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to prepare NFO future details
	 * 
	 * @author sowmiya
	 * @param futureEntity
	 * @return
	 */
	private NfoFutureResponseModel prepareNFOFutureDetails(List<NFOFutureEntity> futureEntity) {
		NfoFutureResponseModel responseModel = new NfoFutureResponseModel();
		List<NfoFutureScripsResponseModel> scripModel = new ArrayList<>();
		int spotToken = 0;
		String symbol = "";
		try {
			for (NFOFutureEntity entity : futureEntity) {
				NfoFutureScripsResponseModel model = new NfoFutureScripsResponseModel();
				spotToken = entity.getSpotToken();
				symbol = entity.getUnderlying();
				model.setFormattedInsName(entity.getFormattedInsName());
				model.setExpiry(entity.getExpiryDate());
				DecimalFormat df = new DecimalFormat("#");
				df.setMaximumFractionDigits(2);
				String oi = df.format(entity.getOi());
				model.setOi(oi);
				model.setToken(entity.getToken());
				model.setPdc(entity.getPdc());
				model.setVolume(entity.getVolume());
				scripModel.add(model);
			}
			responseModel.setSpotToken(spotToken);
			responseModel.setSymbol(symbol);
			responseModel.setScrips(scripModel);

		} catch (Exception e) {
			Log.error("prepareNFOFutureDetails", e);
		}
		return responseModel;
	}

	/**
	 * method to load nfo future
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<GenericResponse> loadNFOFuture() {
		NfoFutureResponseModel futureDetails = new NfoFutureResponseModel();
		try {
			List<String> symbols = nFOFutureRepo.getSymbols();
			if (symbols == null)
				return prepareResponse.prepareSuccessMessage(AppConstants.NO_RECORD_FOUND);
			HazelcastConfig.getInstance().getNfoFutureDetails().clear();
			for (String symbol : symbols) {
				List<NFOFutureEntity> futureEntity = nFOFutureRepo.findByUnderlying(symbol);
				if (futureEntity != null && futureEntity.size() > 0) {
					futureDetails = prepareNFOFutureDetails(futureEntity);
					HazelcastConfig.getInstance().getNfoFutureDetails().put(symbol, futureDetails);
				}
			}
			return prepareResponse.prepareSuccessMessage(AppConstants.LOADED);
		} catch (Exception e) {
			Log.error("loadNFOFuture", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

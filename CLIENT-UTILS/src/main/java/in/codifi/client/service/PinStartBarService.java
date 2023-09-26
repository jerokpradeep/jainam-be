package in.codifi.client.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.client.config.HazelcastConfig;
import in.codifi.client.entity.primary.PinStartBarEntity;
import in.codifi.client.entity.primary.PinStartBarMappingEntity;
import in.codifi.client.model.request.PinToStartbarModel;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.repository.PinStartBarMappingRepository;
import in.codifi.client.repository.PinStartBarRepository;
import in.codifi.client.service.spec.IPinStartBarService;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;

@ApplicationScoped
public class PinStartBarService implements IPinStartBarService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	PinStartBarRepository pinStartBarRepository;
	@Inject
	PinStartBarMappingRepository mappingRepository;

	/**
	 * Method to get pin to start bar details
	 *
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPinToStartBar(ClinetInfoModel info) {
		try {
			List<PinStartBarEntity> pintostartbar = HazelcastConfig.getInstance().getPinTostartbar()
					.get(info.getUserId());

			if (StringUtil.isListNotNullOrEmpty(pintostartbar))
				return prepareResponse.prepareSuccessResponseObject(pintostartbar);

			pintostartbar = pinStartBarRepository.findByUserId(info.getUserId());
			if (StringUtil.isListNotNullOrEmpty(pintostartbar)) {
				HazelcastConfig.getInstance().getPinTostartbar().put(info.getUserId(), pintostartbar);
			} else {
				/** to load default value **/
				pintostartbar = loadDeafultStartBar(info.getUserId());
			}
			return prepareResponse.prepareSuccessResponseObject(pintostartbar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to load pin start bar data into cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public RestResponse<GenericResponse> loadPinToStartBarIntoCache() {
		try {
			List<String> userIdList = pinStartBarRepository.getDistinctUserId();
			if (StringUtil.isListNotNullOrEmpty(userIdList))
				HazelcastConfig.getInstance().getPinTostartbar().clear();
			for (String userId : userIdList) {
				List<PinStartBarEntity> pintostartbarEntity = pinStartBarRepository.findByUserId(userId);
				HazelcastConfig.getInstance().getPinTostartbar().put(userId, pintostartbarEntity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to load for user
	 * 
	 * @author Dinesh Kumar
	 * @param userId
	 * @return
	 */
	public RestResponse<GenericResponse> loadPinToStartBarIntoCacheForUser(String userId) {
		try {
			HazelcastConfig.getInstance().getPinTostartbar().remove(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to load default value
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @return
	 */
	public List<PinStartBarEntity> loadDeafultStartBar(String userId) {

		List<PinStartBarMappingEntity> mappingEntities = mappingRepository.findAll();
		List<PinStartBarEntity> pinStartBarEntities = new ArrayList<>();
		for (PinStartBarMappingEntity masterData : mappingEntities) {
			PinStartBarEntity pinStartBarEntity = new PinStartBarEntity();
			pinStartBarEntity.setToken(masterData.getToken());
			pinStartBarEntity.setExchange(masterData.getExchange());
			pinStartBarEntity.setSymbol(masterData.getSymbol());
			pinStartBarEntity.setFormattedInsName(masterData.getSymbol());
			pinStartBarEntity.setSortOrder(masterData.getSortOrder());
			pinStartBarEntity.setUserId(userId);
			pinStartBarEntities.add(pinStartBarEntity);
		}
		if (StringUtil.isListNotNullOrEmpty(pinStartBarEntities)) {
			HazelcastConfig.getInstance().getPinTostartbar().put(userId, pinStartBarEntities);
			pinStartBarRepository.saveAll(pinStartBarEntities);
		}

		return pinStartBarEntities;
	}

	/**
	 * Method to add pin bar to start bar details
	 *
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addPinToStartBar(PinToStartbarModel model, ClinetInfoModel info) {
		try {
			PinStartBarEntity pinStartBarEntity = new PinStartBarEntity();

			if (!validateReq(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			List<PinStartBarEntity> dbData = pinStartBarRepository.findByUserIdAndSortOrder(info.getUserId(),
					model.getSortOrder());
			if (StringUtil.isListNullOrEmpty(dbData))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_DATA);

			ContractMasterModel masterData = HazelcastConfig.getInstance().getContractMaster()
					.get(model.getExchange() + "_" + model.getToken());
			if (masterData == null)
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			pinStartBarEntity.setId(dbData.get(0).getId());
			pinStartBarEntity.setToken(masterData.getToken());
			pinStartBarEntity.setExchange(masterData.getExch());
			pinStartBarEntity.setSymbol(masterData.getSymbol());
			pinStartBarEntity.setSegment(masterData.getSegment());
			pinStartBarEntity.setPdc(masterData.getPdc());
			pinStartBarEntity.setFormattedInsName(masterData.getFormattedInsName());
			pinStartBarEntity.setExpiry(masterData.getExpiry());
			pinStartBarEntity.setSortOrder(model.getSortOrder());
			pinStartBarEntity.setUserId(info.getUserId());
			PinStartBarEntity pinStartBarEntityNew = pinStartBarRepository.saveAndFlush(pinStartBarEntity);
			if (pinStartBarEntityNew != null) {
				loadPinToStartBarIntoCacheForUser(info.getUserId());
				return prepareResponse.prepareSuccessResponseObject(AppConstants.ADDED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to validate addPinToStartBar request
	 * 
	 * @author Dinesh Kumar
	 * @param model
	 * @return
	 */
	private boolean validateReq(PinToStartbarModel model) {
		if (StringUtil.isNotNullOrEmpty(model.getExchange()) && StringUtil.isNotNullOrEmpty(model.getToken())
				&& model.getSortOrder() > 0) {
			return true;
		}
		return false;
	}

}

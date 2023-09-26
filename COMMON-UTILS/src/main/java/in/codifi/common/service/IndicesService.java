package in.codifi.common.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.IndicesEntity;
import in.codifi.common.entity.primary.IndicesMappingEntity;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.repository.ContractEntityManger;
import in.codifi.common.repository.IndicesMappingRepository;
import in.codifi.common.repository.IndicesRepository;
import in.codifi.common.service.spec.IndicesServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class IndicesService implements IndicesServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IndicesRepository indicesRepository;
	@Inject
	IndicesMappingRepository indicesMappingRepository;
	@Inject
	ContractEntityManger entityManger;

	/**
	 * Method to get indices details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getIndices() {

		try {
			List<IndicesEntity> indicesDetailsEntity = new ArrayList<IndicesEntity>();
			if (StringUtil.isListNotNullOrEmpty(
					HazelcastConfig.getInstance().getIndicesDetails().get(AppConstants.HAZEL_KEY_INDICES))) {
				indicesDetailsEntity = HazelcastConfig.getInstance().getIndicesDetails()
						.get(AppConstants.HAZEL_KEY_INDICES);
			} else {
				/** Method to get indices details and put in cache */
				indicesDetailsEntity = loadIndicesDetailsData();
			}
			if (StringUtil.isListNullOrEmpty(indicesDetailsEntity))
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			return prepareResponse.prepareSuccessResponseObject(indicesDetailsEntity);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get indices details and put in cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<IndicesEntity> loadIndicesDetailsData() {
		List<IndicesEntity> indicesDetailsEntity = indicesRepository.findAll();
		if (StringUtil.isListNotNullOrEmpty(indicesDetailsEntity)) {
			HazelcastConfig.getInstance().getIndicesDetails().clear();
			HazelcastConfig.getInstance().getIndicesDetails().put(AppConstants.HAZEL_KEY_INDICES, indicesDetailsEntity);
		}
		return indicesDetailsEntity;
	}

	/**
	 * Method to Add indices data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> insertIndicesData() {
		try {

			List<String> exchangeList = indicesMappingRepository.findDistinctByExchange();
			List<IndicesEntity> dataToInsert = new ArrayList<>();

			if (StringUtil.isListNullOrEmpty(exchangeList))
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			for (String exchange : exchangeList) {
				List<IndicesMappingEntity> mappingEntities = indicesMappingRepository.findAllByExchange(exchange);
				List<String> scrips = new ArrayList<>();
				for (IndicesMappingEntity entity : mappingEntities) {
					scrips.add(entity.getScrips());
				}
				List<IndicesEntity> indicesEntities = entityManger.getIndicesDetails(scrips, exchange);
				dataToInsert.addAll(indicesEntities);
			}

			if (StringUtil.isListNotNullOrEmpty(dataToInsert)) {
				indicesRepository.deleteAll();
				indicesRepository.saveAll(dataToInsert);
				loadIndicesDetailsData();
				return prepareResponse.prepareSuccessResponseObject(AppConstants.INSERTED);
			}
		} catch (Exception e) {
			Log.error(e);

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

package in.codifi.common.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.EQSectorDetailsEntity;
import in.codifi.common.entity.primary.EQSectorEntity;
import in.codifi.common.entity.primary.EQSectorMappingEntity;
import in.codifi.common.model.request.MapReqModel;
import in.codifi.common.model.request.SectorDetailsReq;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.SectorDetailsResp;
import in.codifi.common.model.response.SectorDetailsRespCN;
import in.codifi.common.model.response.SectorScripDetailsResp;
import in.codifi.common.model.response.SectorsModel;
import in.codifi.common.repository.ContractEntityManger;
import in.codifi.common.repository.EQMappingRepository;
import in.codifi.common.repository.EQSectorRepository;
import in.codifi.common.service.spec.EQSectorServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class EQSectorService implements EQSectorServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	EQSectorRepository eqSectorRepo;
	@Inject
	EQMappingRepository eqMappingRepo;
	@Inject
	ContractEntityManger entityManger;

	/**
	 * Method to get sectors
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSector() {
		List<SectorsModel> sectorsModel = new ArrayList<>();
		try {
			sectorsModel = HazelcastConfig.getInstance().getSectors().get(AppConstants.HAZEL_KEY_SECTORS);
			if (StringUtil.isListNotNullOrEmpty(sectorsModel)) {
				return prepareResponse.prepareSuccessResponseObject(sectorsModel);
			} else {
				sectorsModel = loadSectors();
			}
			if (StringUtil.isListNotNullOrEmpty(sectorsModel)) {
				return prepareResponse.prepareSuccessResponseObject(sectorsModel);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			Log.error(e);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
	}

	/**
	 * method to load sectors
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<SectorsModel> loadSectors() {
		List<EQSectorEntity> eqSectorMasterEntities = eqSectorRepo.findAll();
		List<SectorsModel> response = new ArrayList<>();
		try {
			if (StringUtil.isListNotNullOrEmpty(eqSectorMasterEntities)) {
				for (EQSectorEntity rSet : eqSectorMasterEntities) {
					SectorsModel result = new SectorsModel();
					result.setSectorList(rSet.getSectorList());
					result.setSectorName(rSet.getSectorName());
					result.setImageUrl(rSet.getImageUrl());
					result.setThreeMonths(rSet.getThreeMonths());
					result.setSixMonths(rSet.getSixMonths());
					result.setOneYear(rSet.getOneYear());
					response.add(result);
				}
				HazelcastConfig.getInstance().getSectors().clear();
				HazelcastConfig.getInstance().getSectors().put(AppConstants.HAZEL_KEY_SECTORS, response);

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to get Sector Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> getSectorDetails(SectorDetailsReq req) {
		try {
			EQSectorEntity sectorDetails = eqSectorRepo.findAllBySectorList(req.getSectorList());
			SectorDetailsResp response = new SectorDetailsResp();
			List<SectorScripDetailsResp> scripList = new ArrayList<>();
			if (sectorDetails != null) {
				response.setSectorList(sectorDetails.getSectorList());
				response.setSectorName(sectorDetails.getSectorName());
				for (EQSectorDetailsEntity scrips : sectorDetails.getScrips()) {
					SectorScripDetailsResp result = new SectorScripDetailsResp();
					result.setScripName(scrips.getScripName());
					result.setSortOrder(scrips.getSectorId());
					result.setExchange(scrips.getExchange());
					result.setExpiry(scrips.getExpiry());
					result.setToken(scrips.getToken());
					result.setPdc(scrips.getPdc());
					result.setSymbol(scrips.getSymbol());

					if (StringUtil.isNotNullOrEmpty(scrips.getToken())
							&& StringUtil.isNotNullOrEmpty(scrips.getExchange())) {
						String key = scrips.getExchange() + "_" + scrips.getToken();
						if (HazelcastConfig.getInstance().getContractMaster().get(key) != null) {
							ContractMasterModel contractModel = HazelcastConfig.getInstance().getContractMaster()
									.get(key);
							if (contractModel != null) {
								result.setCompanyName(contractModel.getCompanyName());
							}
						}
					}

					scripList.add(result);
				}
				response.setScrips(scripList);
			}
			if (response != null) {
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Get EQ sector details
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> getEQSector() {
//		List<EQSectorEntity> EQSectorMasterEntity = new ArrayList<>();
//		try {
//			if (HazelcastConfig.getInstance().getEqSectorDetails().containsKey(AppConstants.HAZEL_KEY_EQSECTOR)) {
//				EQSectorMasterEntity = HazelcastConfig.getInstance().getEqSectorDetails()
//						.get(AppConstants.HAZEL_KEY_EQSECTOR);
//			} else {
//				EQSectorMasterEntity = loadEQSectorData();
//			}
//			if (StringUtil.isListNotNullOrEmpty(EQSectorMasterEntity)) {
//				return prepareResponse.prepareSuccessResponseObject(EQSectorMasterEntity);
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
//			}
//		} catch (Exception e) {
//			Log.error(e);
//			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//		}
//	}

	@Override
	public RestResponse<GenericResponse> getEQSector() {
		List<EQSectorEntity> EQSectorMasterEntity = new ArrayList<>();
		List<SectorDetailsRespCN> sectorDetailsResp = new ArrayList<>();

		try {
			if (HazelcastConfig.getInstance().getEqSectorDetailsCn().containsKey(AppConstants.HAZEL_KEY_EQSECTOR)) {
				sectorDetailsResp = HazelcastConfig.getInstance().getEqSectorDetailsCn()
						.get(AppConstants.HAZEL_KEY_EQSECTOR);
			} else {
				EQSectorMasterEntity = loadEQSectorData();

//				for (int i = 0; i <= EQSectorMasterEntity.size(); i++) {
				for (EQSectorEntity eqSectorEntity : EQSectorMasterEntity) {
					SectorDetailsRespCN detailsResp = new SectorDetailsRespCN();
					detailsResp.setSectorList(eqSectorEntity.getSectorList());
					detailsResp.setSectorName(eqSectorEntity.getSectorName());

					List<SectorScripDetailsResp> scrips = new ArrayList<>();
//					for (int j = 0; j <= EQSectorMasterEntity.get(i).getScrips().size(); j++) {

					for (EQSectorDetailsEntity detailsEntity : eqSectorEntity.getScrips()) {
						SectorScripDetailsResp scripDetailsResp = new SectorScripDetailsResp();
						scripDetailsResp.setExchange(detailsEntity.getExchange());
						scripDetailsResp.setExpiry(detailsEntity.getExpiry());
						scripDetailsResp.setPdc(detailsEntity.getPdc());
						scripDetailsResp.setScripName(detailsEntity.getScripName());
						scripDetailsResp.setSortOrder(detailsEntity.getSortOrder());
						scripDetailsResp.setSymbol(detailsEntity.getSymbol());
						scripDetailsResp.setToken(detailsEntity.getToken());

						if (StringUtil.isNotNullOrEmpty(detailsEntity.getToken())
								&& StringUtil.isNotNullOrEmpty(detailsEntity.getExchange())) {
							String key = detailsEntity.getExchange() + "_" + detailsEntity.getToken();
							if (HazelcastConfig.getInstance().getContractMaster().get(key) != null) {
								ContractMasterModel contractModel = HazelcastConfig.getInstance().getContractMaster()
										.get(key);
								if (contractModel != null) {
									scripDetailsResp.setCompanyName(contractModel.getCompanyName());
								}
							}
						}
						scrips.add(scripDetailsResp);
					}
					detailsResp.setScrips(scrips);
					detailsResp.setImageUrl(eqSectorEntity.getImageUrl());
					detailsResp.setOneYear(eqSectorEntity.getOneYear());
					detailsResp.setSixMonths(eqSectorEntity.getSixMonths());
					detailsResp.setThreeMonths(eqSectorEntity.getThreeMonths());

					sectorDetailsResp.add(detailsResp);
				}
				HazelcastConfig.getInstance().getEqSectorDetailsCn().put(AppConstants.HAZEL_KEY_EQSECTOR,
						sectorDetailsResp);
			}
			if (StringUtil.isListNotNullOrEmpty(sectorDetailsResp)) {
				return prepareResponse.prepareSuccessResponseObject(sectorDetailsResp);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			Log.error(e);
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
	}

	/**
	 * 
	 * Method to get EQ Sector data
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public List<EQSectorEntity> getEqSector() {
		List<EQSectorEntity> eqSectorListModels = new ArrayList<EQSectorEntity>();
		try {
			if (StringUtil.isListNotNullOrEmpty(
					HazelcastConfig.getInstance().getEqSectorDetails().get(AppConstants.HAZEL_KEY_EQSECTOR))) {
				eqSectorListModels = HazelcastConfig.getInstance().getEqSectorDetails()
						.get(AppConstants.HAZEL_KEY_EQSECTOR);
			} else {
				eqSectorListModels = loadEQSectorData();
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return eqSectorListModels;
	}

	/**
	 * method to load eq sector
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	public List<EQSectorEntity> loadEQSectorData() {
		List<EQSectorEntity> eqSectorMasterEntities = eqSectorRepo.findAll();
		try {
			if (StringUtil.isListNotNullOrEmpty(eqSectorMasterEntities)) {
				HazelcastConfig.getInstance().getEqSectorDetails().clear();
				HazelcastConfig.getInstance().getEqSectorDetails().put(AppConstants.HAZEL_KEY_EQSECTOR,
						eqSectorMasterEntities);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return eqSectorMasterEntities;
	}

	/**
	 * Method to EQSector data.This is for Admin
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> insertEQSectorData() {
		try {

			List<Integer> sectorIds = eqMappingRepo.findByDistinctSectorId();
			List<EQSectorEntity> dataToInsert = new ArrayList<>();

			if (StringUtil.isListNullOrEmpty(sectorIds))
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			for (Integer sectorId : sectorIds) {
				EQSectorEntity eqSectorEntity = new EQSectorEntity();
				List<EQSectorMappingEntity> mappingEntities = eqMappingRepo.findAllBySectorIdAndActiveStatus(sectorId,
						1);
				List<String> scrips = new ArrayList<>();
				for (EQSectorMappingEntity entity : mappingEntities) {
					scrips.add(entity.getScrips());
				}
				eqSectorEntity.setSectorName(mappingEntities.get(0).getSectorName());
				eqSectorEntity.setSectorList(sectorId);
				eqSectorEntity.setOneYear("+10.46% ");
				eqSectorEntity.setSixMonths("+10.46% ");
				eqSectorEntity.setThreeMonths("+10.46% ");
				eqSectorEntity.setImageUrl("https://image-utl");
				List<EQSectorDetailsEntity> eqSectorEntities = entityManger.getEQSectorDetails(scrips);
				eqSectorEntity.setScrips(eqSectorEntities);
				dataToInsert.add(eqSectorEntity);
			}

			if (StringUtil.isListNotNullOrEmpty(dataToInsert)) {
				eqSectorRepo.deleteAll();
				eqSectorRepo.saveAll(dataToInsert);
				loadEQSectorData();
				return prepareResponse.prepareSuccessResponseObject(AppConstants.INSERTED);
			}
		} catch (Exception e) {
			Log.error(e);

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to add scrips in ETF mapping scrips. This is for admin
	 * 
	 * @author SOWMIYA
	 *
	 * @param entities
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addMappingScrips(List<EQSectorMappingEntity> entities, ClinetInfoModel info) {
		try {
			if (StringUtil.isListNullOrEmpty(entities))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			List<EQSectorMappingEntity> dataToSave = new ArrayList<>();
			for (EQSectorMappingEntity eQSectorMappingEntity : entities) {
				EQSectorMappingEntity dataFromDB = new EQSectorMappingEntity();
				dataFromDB = eqMappingRepo.findByScrips(eQSectorMappingEntity.getScrips());
				if (dataFromDB != null) {
					dataFromDB.setActiveStatus(1);
					dataFromDB.setUpdatedBy(info.getUserId());
					dataToSave.add(dataFromDB);
				} else {
					eQSectorMappingEntity.setCreatedBy(info.getUserId());
					dataToSave.add(eQSectorMappingEntity);
				}
			}
			List<EQSectorMappingEntity> eQSectorMappingEntities = eqMappingRepo.saveAll(dataToSave);
			return prepareResponse.prepareSuccessResponseObject(eQSectorMappingEntities);
		} catch (Exception e) {
			Log.error(e);

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to delete ETF mapping scrips. This is for admin
	 * 
	 * @author SOWMIYA
	 *
	 * @param ids
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteMappingScrips(MapReqModel request, ClinetInfoModel info) {
		try {
			if (request != null && StringUtil.isListNullOrEmpty(request.getIds()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			int activeStatus = 0;
			eqMappingRepo.updateActiveStatus(request.getIds(), info.getUserId(), activeStatus);
			return prepareResponse.prepareSuccessMessage(AppConstants.DELETED);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get EQSector mapping scrips. This is for admin
	 * 
	 * @author SOWMIYA
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getMappingScrips() {
		try {
			List<EQSectorMappingEntity> entities = new ArrayList<>();
			entities = eqMappingRepo.findAllByActiveStatus(1);
			if (StringUtil.isListNullOrEmpty(entities))
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			return prepareResponse.prepareSuccessResponseObject(entities);
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

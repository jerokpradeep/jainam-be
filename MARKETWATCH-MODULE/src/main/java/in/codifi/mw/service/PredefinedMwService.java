package in.codifi.mw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ContractMasterModel;
import in.codifi.mw.config.HazelCacheController;
import in.codifi.mw.entity.primary.PredefinedMwEntity;
import in.codifi.mw.entity.primary.PredefinedMwScripsEntity;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.repository.PredefinedMwRepository;
import in.codifi.mw.repository.PredefinedMwScripsRepository;
import in.codifi.mw.service.spec.IPredefinedMwService;
import in.codifi.mw.utility.AppConstants;
import in.codifi.mw.utility.PrepareResponse;
import in.codifi.mw.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PredefinedMwService implements IPredefinedMwService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	PredefinedMwRepository predefinedMwRepository;
	@Inject
	PredefinedMwScripsRepository predefinedMwScripsRepository;

	/**
	 * Method to get all predefined Market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAllPrefedinedMwScrips() {
		try {
			List<PredefinedMwEntity> result = new ArrayList<>();

			/** Check predefined MW list exist in cache or not **/
			result = HazelCacheController.getInstance().getPredefinedMwList().get(AppConstants.PREDEFINED_MW);

			/** if data exist in cache then return **/
			if (result != null && result.size() > 0)
				return prepareResponse.prepareSuccessResponseObject(result);

			/** If data does not exist in cache load it and return **/
			result = loadPrededinedMWData();
			if (result != null && result.size() > 0)
				return prepareResponse.prepareSuccessResponseObject(result);

			return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to load predefined MW data into cache
	 * 
	 * @return
	 */
	public List<PredefinedMwEntity> loadPrededinedMWData() {
		List<PredefinedMwEntity> result = new ArrayList<>();
		try {
			result = (List<PredefinedMwEntity>) predefinedMwRepository.findAll();
			if (result != null && result.size() > 0) {
				HazelCacheController.getInstance().getPredefinedMwList().clear();
				HazelCacheController.getInstance().getPredefinedMwList().put(AppConstants.PREDEFINED_MW, result);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return result;
	}

	/**
	 * method to add script
	 * 
	 * @author Gowthaman M
	 * @return
	 */

	@Override
	public RestResponse<GenericResponse> addscrip(PredefinedMwEntity predefinedEntity) {
		try {
			/** Check the list not null or empty */
			if (StringUtil.isListNotNullOrEmpty(predefinedEntity.getScrips()) && predefinedEntity.getMwId() > 0
					&& StringUtil.isNotNullOrEmpty(predefinedEntity.getMwName())) {
				int currentSortOrder = 0;
				List<PredefinedMwScripsEntity> mwScripModels = new ArrayList<>();
				for (PredefinedMwScripsEntity model : predefinedEntity.getScrips()) {
					currentSortOrder = currentSortOrder + 1;
					model.setSortOrder(currentSortOrder);
					mwScripModels.add(model);
				}

				/** method to get mw scrips **/
				List<PredefinedMwScripsEntity> scripDetails = getScripMW(mwScripModels, predefinedEntity.getMwId(),
						predefinedEntity.getMwName());
				if (scripDetails != null && scripDetails.size() > 0) {
					/** method to add mw scrips into cache **/
					List<PredefinedMwScripsEntity> newScripDetails = addNewScipsForMwIntoCache(scripDetails,
							predefinedEntity.getMwId(), predefinedEntity.getMwName());
					if (newScripDetails != null && newScripDetails.size() > 0) {

						/** method to add mw scrips into data base **/
						insertNewScipsForMwIntoDataBase(newScripDetails, predefinedEntity.getMwName(),
								predefinedEntity.getMwId());
					}
					return prepareResponse.prepareSuccessResponseObject(scripDetails);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NOT_ABLE_TO_ADD_CONTRACT);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * Method to get the scrips from the cache for Market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<PredefinedMwScripsEntity> getScripMW(List<PredefinedMwScripsEntity> mwScripModels, int mwId,
			String mwName) {
		List<PredefinedMwScripsEntity> response = new ArrayList<>();
		try {
			for (int itr = 0; itr < mwScripModels.size(); itr++) {
				PredefinedMwScripsEntity result = new PredefinedMwScripsEntity();
				result = mwScripModels.get(itr);
				String exch = result.getExchange();
				String token = result.getToken();
				if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {

					ContractMasterModel masterData = HazelCacheController.getInstance().getContractMaster()
							.get(exch + "_" + token);

					result.setMwId(mwId);
					result.setSymbol(masterData.getSymbol());
					result.setTradingSymbol(masterData.getTradingSymbol());
					result.setFormattedInsName(masterData.getFormattedInsName());
					result.setToken(masterData.getToken());
					result.setExchange(masterData.getExch());
					result.setSegment(masterData.getSegment());
					result.setSortOrder(result.getSortOrder());
					result.setPdc(masterData.getPdc());
					response.add(result);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to add the New Scrips in Market Watch New
	 * 
	 * @author Gowthaman M
	 * @return
	 */

	private List<PredefinedMwScripsEntity> addNewScipsForMwIntoCache(List<PredefinedMwScripsEntity> newScripDetails,
			int mwId, String mwName) {
		List<PredefinedMwScripsEntity> responseModel = new ArrayList<>();
		List<PredefinedMwEntity> predefinedMW = new ArrayList<>();
		for (PredefinedMwScripsEntity model : newScripDetails) {
			PredefinedMwScripsEntity predscrips = new PredefinedMwScripsEntity();
			PredefinedMwEntity predMwEntity = new PredefinedMwEntity();
			predscrips.setMwId(mwId);
			predscrips.setSegment(model.getSegment());
			predscrips.setExchange(model.getExchange());
			predscrips.setToken(model.getToken());
			predscrips.setFormattedInsName(model.getFormattedInsName());
			predscrips.setSortOrder(model.getSortOrder());
			predscrips.setPdc(model.getPdc());
			predscrips.setSymbol(model.getSymbol());
			predscrips.setTradingSymbol(model.getTradingSymbol());
			responseModel.add(predscrips);
			predMwEntity.setMwId(mwId);
			predMwEntity.setMwName(mwName);
			predMwEntity.setScrips(responseModel);
			predefinedMW.add(predMwEntity);
		}

		HazelCacheController.getInstance().getPredefinedMwList().remove(mwName);
		HazelCacheController.getInstance().getPredefinedMwList().put(mwName, predefinedMW);
		return responseModel;
	}

	/**
	 * Method to insert new script to db
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private void insertNewScipsForMwIntoDataBase(List<PredefinedMwScripsEntity> newScripDetails, String mwName,
			int mwId) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {

					List<PredefinedMwScripsEntity> scripsDto = prepareMarketWatchEntity(newScripDetails, mwName, mwId);
					/*
					 * Insert the scrip details into the data base
					 */
					if (scripsDto != null && scripsDto.size() > 0) {
						predefinedMwScripsRepository.saveAll(scripsDto);
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}

		});
		pool.shutdown();
	}

	/**
	 * Method to prepare scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<PredefinedMwScripsEntity> prepareMarketWatchEntity(List<PredefinedMwScripsEntity> newScripDetails,
			String mwName, int mwId) {

		List<PredefinedMwScripsEntity> PredefinedMWScrips = new ArrayList<>();
		for (int i = 0; i < newScripDetails.size(); i++) {
			PredefinedMwScripsEntity model = newScripDetails.get(i);
			PredefinedMwScripsEntity resultDto = new PredefinedMwScripsEntity();
			String exch = model.getExchange();
			String token = model.getToken();
			if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {
				ContractMasterModel masterData = HazelCacheController.getInstance().getContractMaster()
						.get(exch + "_" + token);

				resultDto.setMwId(mwId);
				resultDto.setExchange(exch);
				resultDto.setToken(token);
				resultDto.setTradingSymbol(masterData.getTradingSymbol());
				resultDto.setSegment(masterData.getSegment());
				resultDto.setToken(masterData.getToken());
				resultDto.setSymbol(masterData.getSymbol());
				resultDto.setLotSize(masterData.getLotSize());
				resultDto.setTickSize(masterData.getTickSize());
				resultDto.setFormattedInsName(masterData.getFormattedInsName());
				resultDto.setPdc(masterData.getPdc());
				resultDto.setSortOrder(model.getSortOrder());
				PredefinedMWScrips.add(resultDto);
			}
		}
		return PredefinedMWScrips;
	}

	/**
	 * Method to delete the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deletescrip(PredefinedMwEntity predefinedEntity) {
		try {
			int mwId = predefinedEntity.getMwId();
			String mwName = predefinedEntity.getMwName();
			List<PredefinedMwScripsEntity> predefinedMWScripsEntity = predefinedEntity.getScrips();
			if (StringUtil.isNotNullOrEmpty(mwName) && StringUtil.isListNotNullOrEmpty(predefinedMWScripsEntity)
					&& mwId > 0) {
				deleteFromCache(predefinedMWScripsEntity, mwName, mwId);
				deleteFromDB(predefinedMWScripsEntity, mwName, mwId);
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to delete the script from the cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void deleteFromCache(List<PredefinedMwScripsEntity> predefinedMWScripsEntity, String mwName, int mwId) {
		if (predefinedMWScripsEntity != null && predefinedMWScripsEntity.size() > 0) {
			List<PredefinedMwEntity> res = HazelCacheController.getInstance().getPredefinedMwList().get(mwName);
			String marketWatchId = String.valueOf(mwId);
			PredefinedMwEntity result = null;
			int indexOfRes = 0;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = res.get(itr);
					int mwId1 = result.getMwId();
					if (marketWatchId.equals(mwId1)) {
						indexOfRes = itr;
						break;
					}
				}
				if (result != null) {
					List<PredefinedMwScripsEntity> scripDetails = (List<PredefinedMwScripsEntity>) result.getScrips();
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < predefinedMWScripsEntity.size(); i++) {
							PredefinedMwScripsEntity tempDTO = predefinedMWScripsEntity.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExchange();
							for (int j = 0; j < scripDetails.size(); j++) {
								PredefinedMwScripsEntity tempScripDTO = scripDetails.get(j);
								String scripToken = tempScripDTO.getToken();
								String scripExch = tempScripDTO.getExchange();
								if (scripToken.equalsIgnoreCase(token) && scripExch.equalsIgnoreCase(exch)) {
									scripDetails.remove(j);
								}
							}
						}
						result.setScrips(predefinedMWScripsEntity);
						res.remove(indexOfRes);
						res.add(indexOfRes, result);
						HazelCacheController.getInstance().getPredefinedMwList().remove(mwName);
						HazelCacheController.getInstance().getPredefinedMwList().put(mwName, res);
					}
				}
			}
		}
	}

	/**
	 * Method to delete the script to db
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private void deleteFromDB(List<PredefinedMwScripsEntity> predefinedMWScripsEntity, String mwName, int mwId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (predefinedMWScripsEntity != null && predefinedMWScripsEntity.size() > 0) {
						for (int i = 0; i < predefinedMWScripsEntity.size(); i++) {
							PredefinedMwScripsEntity tempDTO = predefinedMWScripsEntity.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExchange();
//							predefinedMwScripsRepository.deleteScripFomDataBase(mwId, mwName, token, exch);
							predefinedMwScripsRepository.deleteByMwIdAndTokenAndExchange(mwId, token, exch);
						}
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * Method to sort mw scrips
	 * 
	 * @author SOWMIYA
	 */
	@Override
	public RestResponse<GenericResponse> sortMwScrips(PredefinedMwEntity predefinedEntity) {
		try {
			if (StringUtil.isNotNullOrEmpty(predefinedEntity.getMwName())
					&& StringUtil.isListNotNullOrEmpty(predefinedEntity.getScrips())
					&& predefinedEntity.getMwId() > 0) {
				sortFromCache(predefinedEntity.getScrips(), predefinedEntity.getMwName(), predefinedEntity.getMwId());
				sortScripInDataBase(predefinedEntity.getScrips(), predefinedEntity.getMwName(),
						predefinedEntity.getMwId());
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method sort from cache
	 * 
	 * @param scrips
	 * @param mwName
	 * @param mwId
	 */

	@SuppressWarnings("unlikely-arg-type")
	private void sortFromCache(List<PredefinedMwScripsEntity> scrips, String mwName, int mwId) {
		if (scrips != null && scrips.size() > 0) {
			List<PredefinedMwEntity> res = HazelCacheController.getInstance().getPredefinedMwList().get(mwName);
			String marketWatchId = String.valueOf(mwId);
			PredefinedMwEntity result = null;
			int indexOfRes = 0;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = res.get(itr);
					int mwId1 = result.getMwId();
					if (marketWatchId.equals(mwId1)) {
						indexOfRes = itr;
						break;
					}
				}
				if (result != null) {
					List<PredefinedMwScripsEntity> scripDetails = (List<PredefinedMwScripsEntity>) result.getScrips();
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < scrips.size(); i++) {
							PredefinedMwScripsEntity tempDTO = scrips.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExchange();
							int sortOrder = tempDTO.getSortOrder();
							for (int j = 0; j < scripDetails.size(); j++) {
								PredefinedMwScripsEntity tempScripDTO = scripDetails.get(j);
								String scripToken = tempScripDTO.getToken();
								String scripExch = tempScripDTO.getExchange();
								if (scripToken.equalsIgnoreCase(token) && scripExch.equalsIgnoreCase(exch)) {
									tempScripDTO.setSortOrder(sortOrder);
									scripDetails.remove(j);
									scripDetails.add(tempScripDTO);
								}
							}
						}
						result.setScrips(scripDetails);
						res.remove(indexOfRes);
						res.add(indexOfRes, result);
						HazelCacheController.getInstance().getPredefinedMwList().remove(mwName);
						HazelCacheController.getInstance().getPredefinedMwList().put(mwName, res);
					}
				}
			}

		}
	}

	/**
	 * method to sort scrip details in db
	 * 
	 * @param list
	 * @param mwName
	 * @param mwId
	 */
	private void sortScripInDataBase(List<PredefinedMwScripsEntity> list, String mwName, int mwId) {
		if (list != null && list.size() > 0) {
			PredefinedMwEntity mwList = predefinedMwRepository.findAllByMwNameAndMwId(mwName, mwId);
			List<PredefinedMwScripsEntity> newScripDetails = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				PredefinedMwScripsEntity model = new PredefinedMwScripsEntity();
				model = list.get(i);
				for (int j = 0; j < mwList.getScrips().size(); j++) {
					PredefinedMwScripsEntity dbData = new PredefinedMwScripsEntity();
					dbData = mwList.getScrips().get(j);
					if (dbData.getToken().equalsIgnoreCase(model.getToken())
							&& dbData.getExchange().equalsIgnoreCase(model.getExchange())) {
						dbData.setSortOrder(model.getSortOrder());
						newScripDetails.add(dbData);
					}
				}
			}
			if (newScripDetails != null && newScripDetails.size() > 0) {
				predefinedMwScripsRepository.saveAll(newScripDetails);
			} else {
				prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}
		}
	}

}

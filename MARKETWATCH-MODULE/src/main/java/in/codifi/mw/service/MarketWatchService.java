package in.codifi.mw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONObject;

import in.codifi.cache.model.ContractMasterModel;
import in.codifi.mw.config.HazelCacheController;
import in.codifi.mw.entity.primary.MarketWatchEntity;
import in.codifi.mw.entity.primary.MarketWatchScripEntity;
import in.codifi.mw.model.request.MwRequestModel;
import in.codifi.mw.model.request.MwScripModel;
import in.codifi.mw.model.response.CacheMwDetailsModel;
import in.codifi.mw.model.response.GenericResponse;
import in.codifi.mw.repository.MarketWatchEntityManager;
import in.codifi.mw.repository.MarketWatchRepository;
import in.codifi.mw.repository.MarketWatchScripRepository;
import in.codifi.mw.service.spec.IMarketWatchService;
import in.codifi.mw.utility.AppConstants;
import in.codifi.mw.utility.PrepareResponse;
import in.codifi.mw.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class MarketWatchService implements IMarketWatchService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	MarketWatchEntityManager entityManager;
	@Inject
	MarketWatchRepository masterRepository;
	@Inject
	MarketWatchScripRepository detailRepository;

	/**
	 * Method to provide the User scrips details from the data base or cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAllMwScrips(String pUserId) {
		try {
			/** Check the user has the scrips in cache or not */
			List<JSONObject> result = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			if (result != null && result.size() > 0) {
				/** if cache is there return from then return from cache */

				return prepareResponse.prepareSuccessResponseObject(result);
			} else {
				/** take the scrip details from the Data base for the user*/
				List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
				if (scripDetails != null && scripDetails.size() > 0) {
					/** Populate the filed for Marketwatch as per the requirement*/
					List<JSONObject> tempResult = populateFields(scripDetails, pUserId);
					if (tempResult != null && tempResult.size() > 0) {
						return prepareResponse.prepareSuccessResponseObject(tempResult);
					}
				} else {

					/** Create New market watch if does not exist */
					List<JSONObject> resp = create(pUserId);
					if (resp != null && resp.size() > 0) {
						return prepareResponse.prepareSuccessResponseObject(resp);
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to populate the fields for the user scrips details for given user
	 * 
	 * @author Gowthaman M
	 * @param userScripDetails
	 * @param pUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<JSONObject> populateFields(List<CacheMwDetailsModel> cacheMwDetailsModels, String pUserId) {
		List<JSONObject> response = new ArrayList<>();
		try {
			JSONObject tempResponse = new JSONObject();
			for (CacheMwDetailsModel tempModel : cacheMwDetailsModels) {
				String mwName = tempModel.getMwName();
				String mwId = String.valueOf(tempModel.getMwId());
				String tempMwID = pUserId + "_" + mwId + "_" + mwName;
				String scripName = tempModel.getFormattedInsName();
				if (scripName != null && !scripName.isEmpty()) {
					if (tempResponse.containsKey(tempMwID)) {
						List<CacheMwDetailsModel> tempList = new ArrayList<>();
						if (tempResponse.get(tempMwID) != null) {
							tempList = (List<CacheMwDetailsModel>) tempResponse.get(tempMwID);
						}
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					} else {
						List<CacheMwDetailsModel> tempList = new ArrayList<>();
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					}
				} else if (tempResponse.get(tempMwID) == null) {
					tempResponse.put(tempMwID, null);
				}
			}
			if (tempResponse != null) {
				response = getCacheListForScrips(tempResponse);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * method to save the scrip details of user in cache
	 * 
	 * @author Gowthaman M
	 * @param mwResponse
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> getCacheListForScrips(JSONObject mwResponse) {
		List<JSONObject> response = new ArrayList<JSONObject>();
		try {
			Iterator<String> itr = mwResponse.keySet().iterator();
			itr = sortedIterator(itr);
			while (itr.hasNext()) {
				String tempStr = itr.next();
				String[] tempStrArr = tempStr.split("_");
				String user = tempStrArr[0];
				String mwId = tempStrArr[1];
				String mwName = tempStrArr[2];
				JSONObject result = new JSONObject();
				List<CacheMwDetailsModel> tempJsonObject = new ArrayList<CacheMwDetailsModel>();
				tempJsonObject = (List<CacheMwDetailsModel>) mwResponse.get(tempStr);
				result.put("mwId", mwId);
				result.put("mwName", mwName);
				if (tempJsonObject != null && tempJsonObject.size() > 0) {
					result.put("scrips", tempJsonObject);
				} else {
					result.put("scrips", null);
				}

				response = HazelCacheController.getInstance().getMwListByUserId().get(user);
				if (response != null) {
					response = HazelCacheController.getInstance().getMwListByUserId().get(user);
					response.add(result);
					HazelCacheController.getInstance().getMwListByUserId().put(user, response);
				} else {
					response = new ArrayList<JSONObject>();
					response.add(result);
					HazelCacheController.getInstance().getMwListByUserId().put(user, response);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Sorting ITR
	 * 
	 * @param it
	 * @return
	 */
	public Iterator<String> sortedIterator(Iterator<String> it) {
		List<String> list = new ArrayList<>();
		while (it.hasNext()) {
			list.add((String) it.next());
		}
		Collections.sort(list);
		return list.iterator();
	}

	/**
	 * Method to create new market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<JSONObject> create(String pUserId) {
		List<JSONObject> response = new ArrayList<>();
		try {
			if (StringUtil.isNotNullOrEmpty(pUserId)) {
				/* Check user has how many market watch */
				List<MarketWatchEntity> mwList = masterRepository.findAllByUserId(pUserId);
				/* If null or size is lesser than 5 create a new Market Watch */
				if (mwList == null || mwList.size() == 0) {
					/* Create the new Market Watch */
					List<MarketWatchEntity> newMwList = new ArrayList<MarketWatchEntity>();
					for (int i = 0; i < AppConstants.MW_SIZE; i++) {
						MarketWatchEntity newDto = new MarketWatchEntity();
						newDto.setUserId(pUserId);
						newDto.setMwId(i + 1);
						newDto.setMwName(AppConstants.MARKET_WATCH_LIST_NAME + (i + 1));
						newDto.setPosition(Long.valueOf(i));
						newMwList.add(newDto);
					}
					masterRepository.saveAll(newMwList);
					List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
					if (scripDetails != null && scripDetails.size() > 0) {
						response = populateFields(scripDetails, pUserId);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);

		}
		return response;
	}

	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RestResponse<GenericResponse> getMWScrips(MwRequestModel reqModel) {
		try {
			List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(reqModel.getUserId());
			String marketWatchId = String.valueOf(reqModel.getMwId());
			JSONObject result = null;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = new JSONObject();
					result = res.get(itr);
					String mwId = (String) result.get("mwId");
					if (marketWatchId.equalsIgnoreCase(mwId)) {
						break;
					}
				}
				if (result != null && !result.isEmpty()) {
					List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						return prepareResponse.prepareSuccessResponseObject(scripDetails);
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deletescrip(MwRequestModel reqModel) {
		try {
			int mwId = reqModel.getMwId();
			String useriD = reqModel.getUserId();
			List<MwScripModel> dataToDelete = reqModel.getScripData();
			if (StringUtil.isNotNullOrEmpty(useriD) && StringUtil.isListNotNullOrEmpty(dataToDelete) && mwId > 0) {
				deleteFromCache(dataToDelete, useriD, mwId);
				deleteFromDB(dataToDelete, useriD, mwId);
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
	 * Method to delete the scrips from the cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void deleteFromCache(List<MwScripModel> dataToDelete, String pUserId, int userMwId) {
		if (dataToDelete != null && dataToDelete.size() > 0) {
			List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			String marketWatchId = String.valueOf(userMwId);
			JSONObject result = null;
			int indexOfRes = 0;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = new JSONObject();
					result = res.get(itr);
					String mwId = (String) result.get("mwId").toString();
					if (marketWatchId.equalsIgnoreCase(mwId)) {
						indexOfRes = itr;
						break;
					}
				}
				if (result != null && !result.isEmpty()) {
					List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < dataToDelete.size(); i++) {
							MwScripModel tempDTO = dataToDelete.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExch();
							for (int j = 0; j < scripDetails.size(); j++) {
								CacheMwDetailsModel tempScripDTO = scripDetails.get(j);
								String scripToken = tempScripDTO.getToken();
								String scripExch = tempScripDTO.getExchange();
								if (scripToken.equalsIgnoreCase(token) && scripExch.equalsIgnoreCase(exch)) {
									scripDetails.remove(j);
								}
							}
						}
						result.remove("scrips");
						result.put("scrips", scripDetails);
						res.remove(indexOfRes);
						res.add(indexOfRes, result);
						HazelCacheController.getInstance().getMwListByUserId().remove(pUserId);
						HazelCacheController.getInstance().getMwListByUserId().put(pUserId, res);
					}
				}
			}
		}
	}

	/**
	 * Method to delete the scrips from the cache
	 * 
	 * @author Gowthaman M
	 */
	public void deleteFromDB(List<MwScripModel> dataToDelete, String pUserId, int userMwId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (dataToDelete != null && dataToDelete.size() > 0) {
						for (int i = 0; i < dataToDelete.size(); i++) {
							MwScripModel tempDTO = dataToDelete.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExch();
//							detailRepository.deleteScripFomDataBase(pUserId, exch, token, userMwId);
							detailRepository.deleteByMwIdAndUserIdAndTokenAndExch(userMwId, pUserId, token, exch);
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
	 * Method to add the scrip into cache and data baseF
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addscrip(MwRequestModel parmDto) {
		try {
			/*
			 * Check the list not null or empty
			 */
			if (StringUtil.isListNotNullOrEmpty(parmDto.getScripData())
					&& StringUtil.isNotNullOrEmpty(parmDto.getUserId()) && parmDto.getMwId() > 0) {

				int curentSortOrder = getExistingSortOrder(parmDto.getUserId(), parmDto.getMwId());
				List<MwScripModel> mwScripModels = new ArrayList<>();
				for (MwScripModel model : parmDto.getScripData()) {
					curentSortOrder = curentSortOrder + 1;
					model.setSortingOrder(curentSortOrder);
					mwScripModels.add(model);
				}
				List<CacheMwDetailsModel> scripDetails = getScripMW(mwScripModels);
				if (scripDetails != null && scripDetails.size() > 0) {
					List<CacheMwDetailsModel> newScripDetails = addNewScipsForMwIntoCache(scripDetails,
							parmDto.getUserId(), parmDto.getMwId());
					if (newScripDetails != null && newScripDetails.size() > 0) {
						insertNewScipsForMwIntoDataBase(newScripDetails, parmDto.getUserId(), parmDto.getMwId());
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
	 * Method to get the sorting order from the cache
	 * 
	 * @author Gowthaman M
	 * @param pUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int getExistingSortOrder(String pUserId, int mwid) {
		int sortingOrder = 0;
		List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
		JSONObject result = null;
		String marketWatchId = String.valueOf(mwid);
		if (res != null && res.size() > 0) {
			for (int itr = 0; itr < res.size(); itr++) {
				result = new JSONObject();
				result = res.get(itr);
				String tempMwId = (String) result.get("mwId");
				if (tempMwId.equalsIgnoreCase(marketWatchId)) {
					List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						Optional<CacheMwDetailsModel> maxByOrder = scripDetails.stream()
								.max(Comparator.comparing(CacheMwDetailsModel::getSortOrder));
						CacheMwDetailsModel model = maxByOrder.get();
						if (model != null && model.getSortOrder() > 0) {
							sortingOrder = model.getSortOrder();
						}
					}
					break;
				}
			}
		}
		return sortingOrder;
	}

	/**
	 * Method to get the scrip from the cache for Market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<CacheMwDetailsModel> getScripMW(List<MwScripModel> pDto) {
		List<CacheMwDetailsModel> response = new ArrayList<>();
		try {
			for (int itr = 0; itr < pDto.size(); itr++) {
				MwScripModel result = new MwScripModel();
				result = pDto.get(itr);
				String exch = result.getExch();
				String token = result.getToken();
				if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {
					CacheMwDetailsModel fResult = new CacheMwDetailsModel();
					ContractMasterModel masterData = HazelCacheController.getInstance().getContractMaster()
							.get(exch + "_" + token);
					fResult.setSymbol(masterData.getSymbol());
					fResult.setTradingSymbol(masterData.getTradingSymbol());
					fResult.setFormattedInsName(masterData.getFormattedInsName());
					fResult.setToken(masterData.getToken());
					fResult.setExchange(masterData.getExch());
					fResult.setSegment(masterData.getSegment());
					fResult.setExpiry(masterData.getExpiry());
					fResult.setSortOrder(result.getSortingOrder());
					fResult.setPdc(masterData.getPdc());
					fResult.setWeekTag(masterData.getWeekTag());
					response.add(fResult);
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
	@SuppressWarnings("unchecked")
	public List<CacheMwDetailsModel> addNewScipsForMwIntoCache(List<CacheMwDetailsModel> newScripDetails,
			String pUserId, int userMwId) {
		List<CacheMwDetailsModel> responseModel = new ArrayList<>();
		responseModel.addAll(newScripDetails);
		List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
		String marketWatchId = String.valueOf(userMwId);
		JSONObject result = null;
		int indexOfRes = 0;
		if (res != null && res.size() > 0) {
			for (int itr = 0; itr < res.size(); itr++) {
				result = new JSONObject();
				result = res.get(itr);
				String mwId = (String) result.get("mwId").toString();
				if (marketWatchId.equalsIgnoreCase(mwId)) {
					indexOfRes = itr;
					break;
				}
			}
			if (result != null && !result.isEmpty()) {
				List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
				List<CacheMwDetailsModel> latestScripDetails = new ArrayList<>();
				if (scripDetails != null && scripDetails.size() > 0) {
					latestScripDetails.addAll(scripDetails);
					for (int i = 0; i < newScripDetails.size(); i++) {
						CacheMwDetailsModel tempNewScrip = newScripDetails.get(i);
						String tempNewToken = tempNewScrip.getToken();
						String tempNewExch = tempNewScrip.getExchange();
						int alreadyAdded = 0;
						for (int j = 0; j < scripDetails.size(); j++) {
							CacheMwDetailsModel scrip = scripDetails.get(j);
							String token = scrip.getToken();
							String exch = scrip.getExchange();
							if (tempNewToken.equalsIgnoreCase(token) && tempNewExch.equalsIgnoreCase(exch)) {
								alreadyAdded = 1;
								break;
							}
						}
						if (alreadyAdded == 0) {
							latestScripDetails.add(tempNewScrip);
						} else {
							// If already exist remove it from list to avoid duplicate insert on DB
							responseModel.remove(i);
						}
					}
				} else {
					latestScripDetails.addAll(newScripDetails);
				}
				result.remove("scrips");
				result.put("scrips", latestScripDetails);
				res.remove(indexOfRes);
				res.add(indexOfRes, result);
				HazelCacheController.getInstance().getMwListByUserId().remove(pUserId);
				HazelCacheController.getInstance().getMwListByUserId().put(pUserId, res);
			}
		}
		return responseModel;
	}

	/**
	 * Method to insert into data base in thread
	 * 
	 * @author Gowthaman M
	 */
	private void insertNewScipsForMwIntoDataBase(List<CacheMwDetailsModel> scripDetails, String userId, int mwId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					List<MarketWatchScripEntity> marketWatchNameDto = prepareMarketWatchEntity(scripDetails, userId,
							mwId);

					/*
					 * Insert the scrip details into the data base
					 */
					if (marketWatchNameDto != null && marketWatchNameDto.size() > 0) {
						detailRepository.saveAll(marketWatchNameDto);
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	private List<MarketWatchScripEntity> prepareMarketWatchEntity(List<CacheMwDetailsModel> scripDetails, String userId,
			int mwId) {

		List<MarketWatchScripEntity> marketWatchScripDetailsDTOs = new ArrayList();
		for (int i = 0; i < scripDetails.size(); i++) {
			CacheMwDetailsModel model = scripDetails.get(i);
			MarketWatchScripEntity resultDto = new MarketWatchScripEntity();
			String exch = model.getExchange();
			String token = model.getToken();
			if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {
				ContractMasterModel masterData = HazelCacheController.getInstance().getContractMaster()
						.get(exch + "_" + token);
				resultDto.setUserId(userId);
				resultDto.setMwId(mwId);
				resultDto.setExch(exch);
				resultDto.setToken(token);
				resultDto.setTradingSymbol(masterData.getTradingSymbol());
				resultDto.setExch(masterData.getExch());
				resultDto.setExSeg(masterData.getSegment());
				resultDto.setToken(masterData.getToken());
				resultDto.setSymbol(masterData.getSymbol());
				resultDto.setGroupName(masterData.getGroupName());
				resultDto.setInstrumentType(masterData.getInsType());
				resultDto.setOptionType(masterData.getOptionType());
				resultDto.setStrikePrice(masterData.getStrikePrice());
				resultDto.setExpDt(masterData.getExpiry());
				resultDto.setLotSize(masterData.getLotSize());
				resultDto.setTickSize(masterData.getTickSize());
				resultDto.setFormattedName(masterData.getFormattedInsName());
				resultDto.setPdc(masterData.getPdc());
				resultDto.setAlterToken(masterData.getAlterToken());
				resultDto.setSortingOrder(model.getSortOrder());
				resultDto.setWeekTag(masterData.getWeekTag());
				marketWatchScripDetailsDTOs.add(resultDto);
			}

		}

		return marketWatchScripDetailsDTOs;
	}

	/**
	 * Method to Sort MW scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> sortMwScrips(MwRequestModel reqModel) {
		try {
			if (StringUtil.isNotNullOrEmpty(reqModel.getUserId())
					&& StringUtil.isListNotNullOrEmpty(reqModel.getScripData()) && reqModel.getMwId() > 0) {
				sortFromCache(reqModel.getScripData(), reqModel.getUserId(), reqModel.getMwId());
				sortScripInDataBase(reqModel.getScripData(), reqModel.getUserId(), reqModel.getMwId());
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
	 * Method to Sort From Cache
	 * 
	 * @author Gowthaman M
	 */
	@SuppressWarnings("unchecked")
	public void sortFromCache(List<MwScripModel> dataToSort, String pUserId, int userMwId) {
		if (dataToSort != null && dataToSort.size() > 0) {
			List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			String marketWatchId = String.valueOf(userMwId);
			JSONObject result = null;
			int indexOfRes = 0;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = new JSONObject();
					result = res.get(itr);
					String mwId = (String) result.get("mwId").toString();
					if (marketWatchId.equalsIgnoreCase(mwId)) {
						indexOfRes = itr;
						break;
					}
				}
				if (result != null && !result.isEmpty()) {
					List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < dataToSort.size(); i++) {
							MwScripModel tempDTO = dataToSort.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExch();
							int sortOrder = tempDTO.getSortingOrder();
							for (int j = 0; j < scripDetails.size(); j++) {
								CacheMwDetailsModel tempScripDTO = scripDetails.get(j);
								String scripToken = tempScripDTO.getToken();
								String scripExch = tempScripDTO.getExchange();
								if (scripToken.equalsIgnoreCase(token) && scripExch.equalsIgnoreCase(exch)) {
									tempScripDTO.setSortOrder(sortOrder);
									scripDetails.remove(j);
									scripDetails.add(tempScripDTO);
								}
							}
						}
						result.remove("scrips");
						result.put("scrips", scripDetails);
						res.remove(indexOfRes);
						res.add(indexOfRes, result);
						HazelCacheController.getInstance().getMwListByUserId().remove(pUserId);
						HazelCacheController.getInstance().getMwListByUserId().put(pUserId, res);
					}
				}
			}
		}
	}

	/**
	 * Method to sort Scrip In DataBase
	 * 
	 * @author Gowthaman M
	 */
	private void sortScripInDataBase(List<MwScripModel> scripDataToSort, String userId, int mwId) {

		if (scripDataToSort != null && scripDataToSort.size() > 0) {
			MarketWatchEntity mwList = masterRepository.findAllByUserIdAndMwId(userId, mwId);
			List<MarketWatchScripEntity> newScripDetails = new ArrayList<>();
			for (int i = 0; i < scripDataToSort.size(); i++) {
				MwScripModel model = new MwScripModel();
				model = scripDataToSort.get(i);
				for (int j = 0; j < mwList.getMwDetailsDTO().size(); j++) {
					MarketWatchScripEntity dbData = new MarketWatchScripEntity();
					dbData = mwList.getMwDetailsDTO().get(j);
					if (dbData.getToken().equalsIgnoreCase(model.getToken())
							&& dbData.getExch().equalsIgnoreCase(model.getExch())) {
						dbData.setSortingOrder(model.getSortingOrder());
						newScripDetails.add(dbData);
					}
				}
			}
			if (newScripDetails != null && newScripDetails.size() > 0) {
				detailRepository.saveAll(newScripDetails);
			}
		}
	}

	/**
	 * Method to create new market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> createMW(String pUserId) {
		try {
			if (StringUtil.isNotNullOrEmpty(pUserId)) {
				/** Check user has how many market watch */
				List<MarketWatchEntity> mwList = masterRepository.findAllByUserId(pUserId);
				/** If null or size is lesser than 5 create a new Market Watch */
				if (mwList == null || mwList.size() == 0) {
					/** Create the new Market Watch */
					List<MarketWatchEntity> newMwList = new ArrayList<MarketWatchEntity>();
					for (int i = 0; i < 5; i++) {
						MarketWatchEntity newDto = new MarketWatchEntity();
						newDto.setUserId(pUserId);
						newDto.setMwId(i + 1);
						newDto.setMwName(AppConstants.MARKET_WATCH_LIST_NAME + (i + 1));
						newDto.setPosition(Long.valueOf(i));
						newMwList.add(newDto);
					}
					masterRepository.saveAll(newMwList);
					List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
					if (scripDetails != null && scripDetails.size() > 0) {
						List<JSONObject> tempResult = populateFields(scripDetails, pUserId);
						if (tempResult != null && tempResult.size() > 0) {
							return prepareResponse.prepareSuccessResponseWithMessage(tempResult,
									AppConstants.MARKET_WATCH_CREATED);
						}
					}
				} else {
					/** Else send the error response */
					return prepareResponse.prepareFailedResponse(AppConstants.LIMIT_REACHED_MW);
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
	 * Method to Rename Market Watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> renameMarketWatch(MwRequestModel reqModel) {
		try {
			if (reqModel != null && StringUtil.isNotNullOrEmpty(reqModel.getMwName())
					&& StringUtil.isNotNullOrEmpty(reqModel.getUserId()) && reqModel.getMwId() != 0) {

				renameMwInCache(reqModel.getMwName(), reqModel.getMwId(), reqModel.getUserId());
				updateMwName(reqModel.getMwName(), reqModel.getMwId(), reqModel.getUserId());
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);

			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

		} catch (Exception e) {
			Log.error(e);

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/** rename MW in cache **/
	@SuppressWarnings("unchecked")
	private void renameMwInCache(String newWwName, int mwId, String userId) {

		List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(userId);
		String marketWatchId = String.valueOf(mwId);
		JSONObject result = null;
		if (res != null && res.size() > 0) {
			for (int itr = 0; itr < res.size(); itr++) {
				result = new JSONObject();
				result = res.get(itr);
				String mw = (String) result.get("mwId").toString();
				if (marketWatchId.equalsIgnoreCase(mw)) {
					result.remove("mwName");
					result.put("mwName", newWwName);
					res.remove(itr);
					res.add(itr, result);
					HazelCacheController.getInstance().getMwListByUserId().remove(userId);
					HazelCacheController.getInstance().getMwListByUserId().put(userId, res);
					break;
				}
			}
		}
	}

	/**
	 * Method to update Mw Name
	 * 
	 * @author Gowthaman M
	 */
	private void updateMwName(String mwName, int mwId, String userId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					masterRepository.updateMWName(mwName, mwId, userId);
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * Method to Delete expired contract in MW
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteExpiredContract() {

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayDate = format.format(date);
		return entityManager.deleteExpiredContract(todayDate);
	}
	
	/**
	 * method to populate the fields for all users
	 *
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> populateFieldsMWForAll(List<CacheMwDetailsModel> cacheMwDetailsModels) {
		List<JSONObject> response = new ArrayList<JSONObject>();
		try {
			JSONObject tempResponse = new JSONObject();
			for (CacheMwDetailsModel tempModel : cacheMwDetailsModels) {
				String mwName = tempModel.getMwName();
				String mwId = String.valueOf(tempModel.getMwId());
				String pUserId = tempModel.getUserId();
				String tempMwID = pUserId + "_" + mwId + "_" + mwName;
				String scripName = tempModel.getFormattedInsName();
				if (scripName != null && !scripName.isEmpty()) {
					if (tempResponse.containsKey(tempMwID)) {
						List<CacheMwDetailsModel> tempList = new ArrayList<>();
						if (tempResponse.get(tempMwID) != null) {
							tempList = (List<CacheMwDetailsModel>) tempResponse.get(tempMwID);
						}
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					} else {
						List<CacheMwDetailsModel> tempList = new ArrayList<>();
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					}
				} else if (tempResponse.get(tempMwID) == null) {
					tempResponse.put(tempMwID, null);
				}
			}
			if (tempResponse != null) {
				response = getCacheListForScrips(tempResponse);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

}

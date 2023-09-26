package in.codifi.api.service;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.codifi.api.cache.HazelCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.primary.MarketWatchNameDTO;
import in.codifi.api.entity.primary.MarketWatchScripDetailsDTO;
import in.codifi.api.entity.primary.PredefinedMwEntity;
import in.codifi.api.entity.primary.PredefinedMwScripsEntity;
import in.codifi.api.model.AdvancedMWModel;
import in.codifi.api.model.CacheMwDetailsModel;
import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.MwScripModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.ScreenersModel;
import in.codifi.api.repository.MarketWatchEntityManager;
import in.codifi.api.repository.MarketWatchNameRepository;
import in.codifi.api.repository.MarketWatchRepository;
import in.codifi.api.repository.PredefinedMwRepo;
import in.codifi.api.service.spec.IMarketWatchService;
import in.codifi.api.util.AppConstants;
import in.codifi.api.util.PrepareResponse;
import in.codifi.api.util.StringUtil;
import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.cache.model.EventDataModel;
import in.codifi.cache.model.MtfDataModel;
import in.codifi.cache.model.PreferenceModel;
import io.quarkus.logging.Log;

@SuppressWarnings("unchecked")
@Service
public class MarketWatchService implements IMarketWatchService {

	@Autowired
	MarketWatchRepository mwRespo;
	@Autowired
	MarketWatchNameRepository mwNameRepo;
	@Autowired
	MarketWatchRepository marketWatchRepo;
	@Autowired
	PrepareResponse prepareResponse;
	@Autowired
	MarketWatchEntityManager entityManager;
	@Autowired
	PredefinedMwRepo predefinedMwRepo;
	@Inject
	ApplicationProperties props;

	/**
	 * Mwthod to provide the User scrips details from the data base or cache
	 * 
	 * @author Sowmiya
	 */
	@Override
	public RestResponse<ResponseModel> getAllMwScripsMob(String pUserId, boolean predefined) {
		try {
			/** Check the user has the scrips in cache or not */
			List<JSONObject> tempResponse = new ArrayList<JSONObject>();
			List<JSONObject> result = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			if (result != null && result.size() > 0) {
				/** if cache is there return from then return from cache */
				if (predefined) {
					List<JSONObject> predefinedMW = preparePredefinedMw(predefined, pUserId);
					List<JSONObject> combinedList = Stream.concat(predefinedMW.stream(), result.stream())
							.collect(Collectors.toList());
					tempResponse = combinedList;
				} else {
					tempResponse = result;
				}
			} else {
				/** take the scrip details from the Data base for the user */
				List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
				if (scripDetails != null && scripDetails.size() > 0) {
					/** Populate the filed for Marketwatch as per the requirement */
					List<JSONObject> tempResult = populateFields(scripDetails, pUserId);
					if (tempResult != null && tempResult.size() > 0) {
						if (predefined == true) {
							List<JSONObject> predefinedMW = preparePredefinedMw(predefined, pUserId);
							List<JSONObject> combinedList = Stream.concat(predefinedMW.stream(), tempResult.stream())
									.collect(Collectors.toList());
							tempResponse = combinedList;
						} else {
							tempResponse = tempResult;
						}
					}
				} else {

					/**
					 * Create New market watch if does not exist
					 */
					List<JSONObject> resp = create(pUserId);
					if (predefined) {
						List<JSONObject> predefinedMW = preparePredefinedMw(predefined, pUserId);
						List<JSONObject> combinedList = Stream.concat(predefinedMW.stream(), resp.stream())
								.collect(Collectors.toList());
						tempResponse = combinedList;
					} else {
						if (resp != null && resp.size() > 0) {
							tempResponse = resp;
						} else {
							return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
						}
					}
				}
			}
			if (tempResponse != null && tempResponse.size() > 0) {
				List<JSONObject> response = prepareScreeners(tempResponse);
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to provide the User scrips details from the data base or cache
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> getAllMwScrips(String pUserId) {
		boolean isAdvancedMW = true;
		try {
			/*
			 * Check the user has the scrips in cache or not
			 */

			List<JSONObject> tempResponse = new ArrayList<JSONObject>();

			List<JSONObject> result = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			if (result != null && result.size() > 0) {
				/*
				 * if cache is there return from then return from cache
				 */

				tempResponse = result;

			} else {
				/*
				 * take the scrip details from the Data base for the user
				 */
				List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
				if (scripDetails != null && scripDetails.size() > 0) {
					/*
					 * Populate the filed for Marketwatch as per the requirement
					 */
					List<JSONObject> tempResult = populateFields(scripDetails, pUserId);
					if (tempResult != null && tempResult.size() > 0) {
						tempResponse = tempResult;
					}
				} else {
					/**
					 * Create New market watch if does not exist
					 */
					List<JSONObject> resp = create(pUserId);
					if (resp != null && resp.size() > 0) {
						tempResponse = resp;
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
					}
				}
			}
			if (tempResponse != null && tempResponse.size() > 0) {
				if (!isAdvancedMW) {
					return prepareResponse.prepareSuccessResponseObject(tempResponse);
				} else {
					List<JSONObject> response = prepareScreeners(tempResponse);
					return prepareResponse.prepareSuccessResponseObject(response);
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
	 * method to prepare screeners
	 * 
	 * @author sowmiya
	 * @param result
	 * @return
	 */
	public List<JSONObject> prepareScreeners(List<JSONObject> result) {
		/** logic to add mtf margin **/
		for (JSONObject model : result) {
			if (model.get("preDef") != null) {
				List<JSONObject> tempList = (List<JSONObject>) model.get("scrips");
				if (tempList != null && tempList.size() > 0) {
					for (JSONObject tempModel : tempList) {
						String key = tempModel.get("exchange") + "_" + tempModel.get("token");
						AdvancedMWModel advanceModel = HazelCacheController.getInstance().getAdvPredefinedMW().get(key);
						if (advanceModel != null) {
							if (advanceModel.getScreeners() != null
									&& advanceModel.getScreeners().getColorCode() != null
									&& advanceModel.getScreeners().getName() != null) {
								tempModel.put("screeners", advanceModel.getScreeners());
							} else {
								tempModel.put("screeners", "");
							}
							tempModel.put("event", advanceModel.isEvent());
							tempModel.put("research", advanceModel.isResearch());
							tempModel.put("mtfMargin", advanceModel.getMtfMargin());
						}
						ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster()
								.get(key);
						if (contractModel != null) {
							tempModel.put("companyName", contractModel.getCompanyName());
							tempModel.put("pdc", contractModel.getPdc());
						}
					}
				}
			} else {
				String key = "";
				List<CacheMwDetailsModel> tempList = (List<CacheMwDetailsModel>) model.get("scrips");
				if (tempList != null && tempList.size() > 0) {
					for (CacheMwDetailsModel tempModel : tempList) {
						boolean isTopGainer = false;
						boolean is52WeekHigh = false;
						boolean isTopLoser = false;
						boolean is52WeekLow = false;
						AdvancedMWModel advMWModel = new AdvancedMWModel();
						if (tempModel.getExchange() != null && tempModel.getToken() != null) {
							key = tempModel.getExchange() + "_" + tempModel.getToken();

							/** logic to add MTF Margin **/
							MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(key);
							if (mtfData != null && mtfData.getMtfMargin() > 0) {
								tempModel.setMtfMargin(mtfData.getMtfMargin());
								advMWModel.setMtfMargin(mtfData.getMtfMargin());
							}

							/** logic to add Event **/
							EventDataModel eventData = HazelCacheController.getInstance().getEventData().get(key);
							if (eventData != null) {
								tempModel.setEvent(true);
								advMWModel.setEvent(true);
							}

							/** logic to add screener **/
							ScreenersModel screeners = new ScreenersModel();
							/** top gainers **/
							AnalysisRespModel topGainerModel = HazelCacheController.getInstance().getTopGainers()
									.get(key);
							if (topGainerModel != null && topGainerModel.getDirection().equalsIgnoreCase("Bullish")) {
								isTopGainer = true;
								screeners.setName("Top gainers");
								screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
							}
							/** top loser **/
							AnalysisRespModel topLoserModel = HazelCacheController.getInstance().getTopLosers()
									.get(key);
							if (topLoserModel != null && topLoserModel.getDirection().equalsIgnoreCase("Bearish")) {
								isTopLoser = true;
								screeners.setName("Top loser");
								screeners.setColorCode(AppConstants.COLOR_CODE_RED);
							}

							/** fifty Two Week High **/
							AnalysisRespModel fiftyTwoWeekHigh = HazelCacheController.getInstance()
									.getFiftyTwoWeekHigh().get(key);
							if (fiftyTwoWeekHigh != null) {
								is52WeekHigh = true;
								screeners.setName("52 Week high");
								screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
							}
							/** fifty Two Week Low **/
							AnalysisRespModel fiftyTwoWeekLow = HazelCacheController.getInstance().getFiftyTwoWeekLow()
									.get(key);
							if (fiftyTwoWeekLow != null) {
								is52WeekLow = true;
								screeners.setName("52 Week low");
								screeners.setColorCode(AppConstants.COLOR_CODE_RED);
							}
							/** if possible to get top gainers and fifty two week high **/
							if (is52WeekHigh && isTopGainer) {
								screeners.setName("52 Week high");
								screeners.setColorCode(AppConstants.COLOR_CODE_RED);
							}
							/** if possible to get top loser and fifty two week low **/
							if (isTopLoser && is52WeekLow) {
								screeners.setName("52 Week low");
								screeners.setColorCode(AppConstants.COLOR_CODE_RED);
							}
							if (screeners != null && screeners.getColorCode() != null
									&& screeners.getColorCode() != null) {
								tempModel.setScreeners(screeners);
								advMWModel.setScreeners(screeners);
							}
							ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster()
									.get(key);
							if (contractModel != null) {
								tempModel.setCompanyName(contractModel.getCompanyName());
								tempModel.setPdc(contractModel.getPdc());
							}

						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Method to create new market watch
	 * 
	 * @author Dinesh Kumar
	 * @param pDto
	 * @return
	 */
	private List<JSONObject> create(String pUserId) {
		List<JSONObject> response = new ArrayList<>();
		try {
			if (StringUtil.isNotNullOrEmpty(pUserId)) {
				/* Check user has how many market watch */
				List<MarketWatchNameDTO> mwList = mwNameRepo.findAllByUserId(pUserId);
				/* If null or size is lesser than 5 create a new Market Watch */
				if (mwList == null || mwList.size() == 0) {
					/* Create the new Market Watch */
					List<MarketWatchNameDTO> newMwList = new ArrayList<MarketWatchNameDTO>();
					for (int i = 0; i < AppConstants.MW_SIZE; i++) {
						MarketWatchNameDTO newDto = new MarketWatchNameDTO();
						newDto.setUserId(pUserId);
						newDto.setMwId(i + 1);
						newDto.setMwName(AppConstants.MARKET_WATCH_LIST_NAME + (i + 1));
						newDto.setPosition(Long.valueOf(i));
						newMwList.add(newDto);
					}
					mwNameRepo.saveAll(newMwList);
//					List<IMwTblResponse> scripDetails = mwNameRepo.getUserScripDetails(pUserId);
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
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> getMWScrips(MwRequestModel pDto) {
		try {
			List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pDto.getUserId());
			String marketWatchId = String.valueOf(pDto.getMwId());

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
						scripDetails = prepareScreenersForModel(scripDetails);
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
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowrisankar
	 */
	@Override
	public RestResponse<ResponseModel> getMWScripsForMob(MwRequestModel pDto, boolean isPredefined) {
		try {

			if (isPredefined) {
				List<JSONObject> resp = new ArrayList<>();
				List<PredefinedMwEntity> predefinedMwEntities = new ArrayList<>();

				/** Get predefined mw list from cache or DB **/
				if (HazelCacheController.getInstance().getMasterPredefinedMwList()
						.get(AppConstants.PREDEFINED_MW) != null) {
					predefinedMwEntities = HazelCacheController.getInstance().getMasterPredefinedMwList()
							.get(AppConstants.PREDEFINED_MW);
				} else {
					predefinedMwEntities = predefinedMwRepo.findAll();
				}

				for (PredefinedMwEntity predefinedMwEntity : predefinedMwEntities) {
					if (predefinedMwEntity.getMwId() == pDto.getMwId()) {
						resp = preparePredefinedMWScripsList(predefinedMwEntity);
					}
				}

				if (StringUtil.isListNotNullOrEmpty(resp)) {
					for (JSONObject object : resp) {
						String key = object.get("exchange") + "_" + object.get("token");
						AdvancedMWModel model = HazelCacheController.getInstance().getAdvPredefinedMW().get(key);
						if (model.getScreeners() != null && model.getScreeners().getName() != null
								&& model.getScreeners().getColorCode() != null) {
							object.put("screeners", model.getScreeners());
						} else {
							object.put("screeners", "");
						}
						object.put("mtfMargin", model.getMtfMargin());
						object.put("event", model.isEvent());
						object.put("research", model.isResearch());
						ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster()
								.get(key);
						if (contractModel != null) {
							object.put("companyName", contractModel.getCompanyName());
							object.put("pdc", contractModel.getPdc());
						}
					}
					return prepareResponse.prepareSuccessResponseObject(resp);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
				}

			} else {
				List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pDto.getUserId());
				String marketWatchId = String.valueOf(pDto.getMwId());
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
							scripDetails = prepareScreenersForModel(scripDetails);
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
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load top gainers into cache
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<ResponseModel> getTopGainers() {
		try {
			String topGainer = props.getTopGainers() + "_" + "Bullish";
			List<AnalysisRespModel> analysisRespModel = HazelCacheController.getInstance().getAnalysistopGainers()
					.get(topGainer);
			if (analysisRespModel != null && analysisRespModel.size() > 0) {
				HazelCacheController.getInstance().getTopGainers().clear();
				for (AnalysisRespModel model : analysisRespModel) {
					String analysisKey = model.getExch() + "_" + model.getToken();
					HazelCacheController.getInstance().getTopGainers().put(analysisKey, model);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);

			}

		} catch (Exception e) {
			Log.error("getTopGainers", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load top gainers into cache
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<ResponseModel> getTopLosers() {
		try {
			String topLosers = props.getTopGainers() + "_" + "Bearish";
			List<AnalysisRespModel> analysisRespModel = HazelCacheController.getInstance().getAnalysistopLosers()
					.get(topLosers);
			if (analysisRespModel != null && analysisRespModel.size() > 0) {
				HazelCacheController.getInstance().getTopLosers().clear();
				for (AnalysisRespModel model : analysisRespModel) {
					String analysisKey = model.getExch() + "_" + model.getToken();
					HazelCacheController.getInstance().getTopLosers().put(analysisKey, model);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);

			}

		} catch (Exception e) {
			Log.error("getTopGainers", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load fifty two week high
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<ResponseModel> getFiftytwoWeekHigh() {
		try {
			String fiftytwoweekHigh = props.getFiftytwoWeekHigh();
			List<AnalysisRespModel> analysisRespModel = HazelCacheController.getInstance().getAnalysisfiftyTwoWeekHigh()
					.get(fiftytwoweekHigh);
			if (analysisRespModel != null && analysisRespModel.size() > 0) {
				HazelCacheController.getInstance().getFiftyTwoWeekHigh().clear();
				for (AnalysisRespModel model : analysisRespModel) {
					if (model != null) {
						String analysisKey = model.getExch() + "_" + model.getToken();
						HazelCacheController.getInstance().getFiftyTwoWeekHigh().put(analysisKey, model);
					}
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			Log.error("getFiftytwoWeekHigh", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load fifty two week low
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<ResponseModel> getFiftytwoWeekLow() {
		try {
			String fiftytwoweekLow = props.getFiftytwoWeekLow();
			List<AnalysisRespModel> analysisRespModel = HazelCacheController.getInstance().getAnalysisfiftyTwoWeekLow()
					.get(fiftytwoweekLow);
			if (analysisRespModel != null && analysisRespModel.size() > 0) {
				HazelCacheController.getInstance().getFiftyTwoWeekLow().clear();
				for (AnalysisRespModel model : analysisRespModel) {
					if (model != null) {
						String analysisKey = model.getExch() + "_" + model.getToken();
						HazelCacheController.getInstance().getFiftyTwoWeekLow().put(analysisKey, model);
					}
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);

			}

		} catch (Exception e) {
			Log.error("getFiftytwoWeekLow", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to predefined advance key
	 * 
	 * @author sowmiya
	 * @return
	 */
	public RestResponse<ResponseModel> predefinedAdvanceKey() {
		String key = "";
		try {
			List<PredefinedMwEntity> predefinedMwEntities = new ArrayList<>();
			if (HazelCacheController.getInstance().getMasterPredefinedMwList()
					.get(AppConstants.PREDEFINED_MW) != null) {
				predefinedMwEntities = HazelCacheController.getInstance().getMasterPredefinedMwList()
						.get(AppConstants.PREDEFINED_MW);
			} else {
				predefinedMwEntities = predefinedMwRepo.findAll();
			}
			HazelCacheController.getInstance().getAdvPredefinedMW().clear();
			for (PredefinedMwEntity predefinedMW : predefinedMwEntities) {
				for (PredefinedMwScripsEntity scrips : predefinedMW.getScrips()) {
					boolean topGainers = false;
					boolean topLosser = false;
					boolean fiftyTwoWeekHigh = false;
					boolean fiftyTwoWeekLow = false;
					AdvancedMWModel advanceModel = new AdvancedMWModel();
					key = scrips.getExchange() + "_" + scrips.getToken();
					MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(key);
					if (mtfData != null && mtfData.getMtfMargin() > 0) {
						advanceModel.setMtfMargin(mtfData.getMtfMargin());
					}
					EventDataModel eventData = HazelCacheController.getInstance().getEventData().get(key);
					if (eventData != null) {
						advanceModel.setEvent(true);
					}
					AnalysisRespModel topGainersModel = HazelCacheController.getInstance().getTopGainers().get(key);
					ScreenersModel screeners = new ScreenersModel();
					if (topGainersModel != null) {
						topGainers = true;
						System.out.println(key + "-  top gainers");
						screeners.setName("Top gainers");
						screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
					}
					AnalysisRespModel topLossersModel = HazelCacheController.getInstance().getTopLosers().get(key);
					if (topLossersModel != null) {
						topLosser = true;
						System.out.println(key + "-  top loser");
						screeners.setName("Top loser");
						screeners.setColorCode(AppConstants.COLOR_CODE_RED);
					}
					AnalysisRespModel fiftyTwoWeekHighModel = HazelCacheController.getInstance().getFiftyTwoWeekHigh()
							.get(key);
					if (fiftyTwoWeekHighModel != null) {
						fiftyTwoWeekHigh = true;
						System.out.println(key + "-  52 Week high");
						screeners.setName("52 Week high");
						screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
					}
					AnalysisRespModel fiftyTwoWeekLowModel = HazelCacheController.getInstance().getFiftyTwoWeekLow()
							.get(key);
					if (fiftyTwoWeekLowModel != null) {
						fiftyTwoWeekLow = true;
						System.out.println(key + "-  52 Week low");
						screeners.setName("52 Week low");
						screeners.setColorCode(AppConstants.COLOR_CODE_RED);
					}
					if (topGainers && fiftyTwoWeekHigh) {
						screeners.setName("52 Week high");
						System.out.println(key + "-  both top gainers and 52 week high");
						screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
					}
					if (topLosser && fiftyTwoWeekLow) {
						screeners.setName("52 Week low");
						System.out.println(key + "-  both top loser and 52 week low");
						screeners.setColorCode(AppConstants.COLOR_CODE_RED);
					}
					advanceModel.setScreeners(screeners);
					advanceModel.setResearch(false);
					HazelCacheController.getInstance().getAdvPredefinedMW().put(key, advanceModel);
				}

			}

		} catch (Exception e) {
			Log.error("predefinedAdvanceKey", e);
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

//
	/**
	 * method to prepare screeners for model
	 * 
	 * @author SOWMIYA
	 * @param scripDetails
	 * @return
	 */
	private List<CacheMwDetailsModel> prepareScreenersForModel(List<CacheMwDetailsModel> scripDetails) {
		/** logic to add mtf margin **/
		for (CacheMwDetailsModel model : scripDetails) {
			boolean isTopGainer = false;
			boolean is52WeekHigh = false;
			boolean isTopLoser = false;
			boolean is52WeekLow = false;
			AdvancedMWModel advMWModel = new AdvancedMWModel();
			String key = model.getExchange() + "_" + model.getToken();
			if (HazelCacheController.getInstance().getAdvMWData().get(key) != null) {
				advMWModel = HazelCacheController.getInstance().getAdvMWData().get(key);
				model.setScreeners(advMWModel.getScreeners());
				model.setEvent(advMWModel.isEvent());
				model.setResearch(advMWModel.isResearch());
				model.setMtfMargin(advMWModel.getMtfMargin());
				ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
				if (contractModel != null) {
					model.setCompanyName(contractModel.getCompanyName());
					model.setPdc(contractModel.getPdc());
				}

			} else {
				/** logic to add MTF Margin **/
				MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(key);
				if (mtfData != null && mtfData.getMtfMargin() > 0) {
					model.setMtfMargin(mtfData.getMtfMargin());
					advMWModel.setMtfMargin(mtfData.getMtfMargin());
				}

				/** logic to add Event **/
				EventDataModel eventData = HazelCacheController.getInstance().getEventData().get(key);
				if (eventData != null) {
					model.setEvent(true);
					advMWModel.setEvent(true);
				}

				/** logic to add screener **/
				ScreenersModel screeners = new ScreenersModel();
				/** top gainers **/
				AnalysisRespModel topGainerModel = HazelCacheController.getInstance().getTopGainers().get(key);
				if (topGainerModel != null && topGainerModel.getDirection().equalsIgnoreCase("Bullish")) {
					isTopGainer = true;
					screeners.setName("Top gainers");
					screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
				}
				/** top loser **/
				AnalysisRespModel topLoserModel = HazelCacheController.getInstance().getTopLosers().get(key);
				if (topLoserModel != null && topLoserModel.getDirection().equalsIgnoreCase("Bearish")) {
					isTopLoser = true;
					screeners.setName("Top loser");
					screeners.setColorCode(AppConstants.COLOR_CODE_RED);
				}

				/** fifty Two Week High **/
				AnalysisRespModel fiftyTwoWeekHigh = HazelCacheController.getInstance().getFiftyTwoWeekHigh().get(key);
				if (fiftyTwoWeekHigh != null) {
					is52WeekHigh = true;
					screeners.setName("52 Week high");
					screeners.setColorCode(AppConstants.COLOR_CODE_GREEN);
				}

				/** fifty Two Week Low **/
				AnalysisRespModel fiftyTwoWeekLow = HazelCacheController.getInstance().getFiftyTwoWeekLow().get(key);
				if (fiftyTwoWeekLow != null) {
					is52WeekLow = true;
					screeners.setName("52 Week low");
					screeners.setColorCode(AppConstants.COLOR_CODE_RED);
				}
				/** if possible to get top gainers and fifty two week high **/
				if (is52WeekHigh && isTopGainer) {
					screeners.setName("52 Week high");
					screeners.setColorCode(AppConstants.COLOR_CODE_RED);
				}
				/** if possible to get top loser and fifty two week low **/
				if (isTopLoser && is52WeekLow) {
					screeners.setName("52 Week low");
					screeners.setColorCode(AppConstants.COLOR_CODE_RED);
				}
				if (screeners != null && screeners.getColorCode() != null && screeners.getColorCode() != null) {
					model.setScreeners(screeners);
				}
				ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
				if (contractModel != null) {
					model.setCompanyName(contractModel.getCompanyName());
					model.setPdc(contractModel.getPdc());
				}
			}

		}

		return scripDetails;
	}

	/**
	 * method prepare predefined mw scrips list
	 * 
	 * @author SOWMIYA
	 * @param predefinedMw
	 * @return
	 */
	private List<JSONObject> preparePredefinedMWScripsList(PredefinedMwEntity predefinedMw) {
		List<JSONObject> predefinedMWScrips = new ArrayList<>();
		for (PredefinedMwScripsEntity scrips : predefinedMw.getScrips()) {
			JSONObject obj = new JSONObject();
			obj.put("exchange", scrips.getExchange());
			obj.put("segment", scrips.getSegment());
			obj.put("token", scrips.getToken());
			obj.put("tradingSymbol", scrips.getTradingSymbol());
			obj.put("formattedInsName", scrips.getFormattedInsName());
			obj.put("sortOrder", scrips.getSortOrder());
			obj.put("pdc", scrips.getPdc());
			obj.put("symbol", scrips.getSymbol());
			predefinedMWScrips.add(obj);
		}
		return predefinedMWScrips;
	}

	/**
	 * Method to populate the fields for the user scrips details for given user
	 * 
	 * @author Gowrisankar
	 * @param userScripDetails
	 * @param pUserId
	 * @return
	 */
//	private List<JSONObject> populateFields(List<IMwTblResponse> userScripDetails, String pUserId) {
	private List<JSONObject> populateFields(List<CacheMwDetailsModel> cacheMwDetailsModels, String pUserId) {

		List<JSONObject> response = new ArrayList<>();
		try {
//			ObjectMapper mapper = new ObjectMapper();
//			List<CacheMwDetailsModel> cacheMwDetailsModels = new ArrayList<>();
//			for (IMwTblResponse iMwTblResponse : userScripDetails) {
//				CacheMwDetailsModel model = new CacheMwDetailsModel();
//				model = mapper.readValue((mapper.writeValueAsString(iMwTblResponse)), CacheMwDetailsModel.class);
//				cacheMwDetailsModels.add(model);
//			}
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
	 * @author Gowrisankar
	 * @param mwResponse
	 * @return
	 */
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
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> createMW(String pUserId) {
		try {
			if (StringUtil.isNotNullOrEmpty(pUserId)) {
				/* Check user has how many market watch */
				List<MarketWatchNameDTO> mwList = mwNameRepo.findAllByUserId(pUserId);
				/* If null or size is lesser than 5 create a new Market Watch */
				if (mwList == null || mwList.size() == 0) {
					/* Create the new Market Watch */
					List<MarketWatchNameDTO> newMwList = new ArrayList<MarketWatchNameDTO>();
					// TODO change hot code value
//					int mwListSize = Integer.parseInt(CSEnvVariables.getMethodNames(AppConstants.MW_LIST_SIZE));
					for (int i = 0; i < 5; i++) {
						MarketWatchNameDTO newDto = new MarketWatchNameDTO();
						newDto.setUserId(pUserId);
						newDto.setMwId(i + 1);
						newDto.setMwName(AppConstants.MARKET_WATCH_LIST_NAME + (i + 1));
						newDto.setPosition(Long.valueOf(i));
						newMwList.add(newDto);
					}
					mwNameRepo.saveAll(newMwList);
//					List<IMwTblResponse> scripDetails = mwNameRepo.getUserScripDetails(pUserId);
					List<CacheMwDetailsModel> scripDetails = entityManager.getMarketWatchByUserId(pUserId);
					if (scripDetails != null && scripDetails.size() > 0) {
						List<JSONObject> tempResult = populateFields(scripDetails, pUserId);
						if (tempResult != null && tempResult.size() > 0) {
							return prepareResponse.prepareSuccessResponseWithMessage(tempResult,
									AppConstants.MARKET_WATCH_CREATED);
						}
					}
				} else { /* Else send the error response */
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
	 * Method to add the scrip into cache and data baseF
	 * 
	 * @author Dinesh Kumar
	 */
	@Override
	public RestResponse<ResponseModel> addscrip(MwRequestModel parmDto) {
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
					/** logic to add mtf margin **/
					for (CacheMwDetailsModel model : scripDetails) {
						String mtfKey = model.getExchange() + "_" + model.getToken();
						MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(mtfKey);
						if (mtfData != null && mtfData.getMtfMargin() > 0) {
							model.setMtfMargin(mtfData.getMtfMargin());
						}
					}
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
	 * @author Gowrisankar
	 * @param pUserId
	 * @param i
	 * @return
	 */
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
	 * @author Dinesh Kumar
	 * @param pDto
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
				System.out.println(HazelCacheController.getInstance().getContractMaster().size());
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
					response.add(fResult);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to insert into data base in thread
	 * 
	 * @author Dinesh Kumar
	 * @param parmDto
	 */
	private void insertNewScipsForMwIntoDataBase(List<CacheMwDetailsModel> scripDetails, String userId, int mwId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
//					int sortingOrder = getSortingOrder(userId, String.valueOf(mwId));
//					List<MarketWatchScripDetailsDTO> marketWatchNameDto = prepareMarketWatchEntity(parmDto,
//							sortingOrder + 1);
					List<MarketWatchScripDetailsDTO> marketWatchNameDto = prepareMarketWatchEntity(scripDetails, userId,
							mwId);

					/*
					 * Insert the scrip details into the data base
					 */
					if (marketWatchNameDto != null && marketWatchNameDto.size() > 0) {
						mwRespo.saveAll(marketWatchNameDto);
					}
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();
	}

	/**
	 * Method to add the New Scrips in Market Watch New
	 * 
	 * @author Gowrisankar
	 * @param newScripDetails
	 * @param pUserId
	 * @param userMwId
	 */
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

	private List<MarketWatchScripDetailsDTO> prepareMarketWatchEntity(List<CacheMwDetailsModel> scripDetails,
			String userId, int mwId) {

		List<MarketWatchScripDetailsDTO> marketWatchScripDetailsDTOs = new ArrayList();
		for (int i = 0; i < scripDetails.size(); i++) {
			CacheMwDetailsModel model = scripDetails.get(i);
			MarketWatchScripDetailsDTO resultDto = new MarketWatchScripDetailsDTO();
			String exch = model.getExchange();
			String token = model.getToken();
			if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {
				ContractMasterModel masterData = HazelCacheController.getInstance().getContractMaster()
						.get(exch + "_" + token);
				resultDto.setUserId(userId);
				resultDto.setMwId(mwId);
				resultDto.setEx(exch);
				resultDto.setToken(token);
				resultDto.setTradingSymbol(masterData.getTradingSymbol());
				resultDto.setEx(masterData.getExch());
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
				marketWatchScripDetailsDTOs.add(resultDto);
			}

		}

		return marketWatchScripDetailsDTOs;
	}

	/**
	 * method to check wheather the token and exch in given market watch
	 * 
	 * @author Gowrisankar
	 * @param pUserId
	 * @param mwId
	 * @param token
	 * @param exch
	 * @return
	 */
	public int checkTokenInMw(String pUserId, String mwId, String token, String exch) {
		int isPresent = 0;
		try {
			List<JSONObject> res = HazelCacheController.getInstance().getMwListByUserId().get(pUserId);
			JSONObject result = null;
			if (res != null && res.size() > 0) {
				for (int itr = 0; itr < res.size(); itr++) {
					result = new JSONObject();
					result = res.get(itr);
					String tempMwId = (String) result.get("mwId");
					if (tempMwId.equalsIgnoreCase(mwId)) {
						break;
					}
				}
				if (result != null && !result.isEmpty()) {
					List<CacheMwDetailsModel> scripDetails = (List<CacheMwDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						for (CacheMwDetailsModel tempDto : scripDetails) {
							String tempExch = tempDto.getExchange();
							String tempToken = tempDto.getToken();
							if (tempExch.equalsIgnoreCase(exch) && token.equalsIgnoreCase(tempToken)) {
								isPresent = 1;
								return isPresent;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return isPresent;
	}

	/**
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> deletescrip(MwRequestModel pDto) {
		try {
			int mwId = pDto.getMwId();
			String useriD = pDto.getUserId();
			List<MwScripModel> dataToDelete = pDto.getScripData();
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
	 * @author Gowrisankar
	 * @param newScripDetails
	 * @param pUserId
	 * @param userMwId
	 */
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
	 * @author Gowrisankar
	 * @param newScripDetails
	 * @param pUserId
	 * @param userMwId
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
							mwRespo.deleteScripFomDataBase(pUserId, exch, token, userMwId);
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
	 * method to populate the fields for all users
	 *
	 * @author Gowri Sankar
	 * @param userScripDetails
	 * @return
	 */
//	public List<JSONObject> populateFieldsMWForAll(List<IMwTblResponse> userScripDetails) {
	public List<JSONObject> populateFieldsMWForAll(List<CacheMwDetailsModel> cacheMwDetailsModels) {
		List<JSONObject> response = new ArrayList<JSONObject>();
		try {
//			ObjectMapper mapper = new ObjectMapper();
//			List<CacheMwDetailsModel> cacheMwDetailsModels = new ArrayList<>();
//			for (IMwTblResponse iMwTblResponse : userScripDetails) {
//				CacheMwDetailsModel model = new CacheMwDetailsModel();
//				model = mapper.readValue((mapper.writeValueAsString(iMwTblResponse)), CacheMwDetailsModel.class);
//				cacheMwDetailsModels.add(model);
//			}
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

	/**
	 * 
	 * Method to Rename Market Watch
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
	public RestResponse<ResponseModel> renameMarketWatch(MwRequestModel pDto) {
		try {
			if (pDto != null && StringUtil.isNotNullOrEmpty(pDto.getMwName())
					&& StringUtil.isNotNullOrEmpty(pDto.getUserId()) && pDto.getMwId() != 0) {

				renameMwInCache(pDto.getMwName(), pDto.getMwId(), pDto.getUserId());
				updateMwNamw(pDto.getMwName(), pDto.getMwId(), pDto.getUserId());
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);

			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

		} catch (Exception e) {
			Log.error(e);

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private void updateMwNamw(String mwName, int mwId, String userId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					mwNameRepo.updateMWName(mwName, mwId, userId);
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});
		pool.shutdown();

	}

	/** rename MW in cache **/
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
					HazelCacheController.getInstance().getAdvanceMWListByUserId().remove(userId);
//					HazelCacheController.getInstance().getAdvanceMWListByUserId().put(userId, res);
					break;
				}
			}
		}

	}

	/**
	 * 
	 * Method to Sort MW scrips
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> sortMwScrips(MwRequestModel pDto) {
		try {
			if (StringUtil.isNotNullOrEmpty(pDto.getUserId()) && StringUtil.isListNotNullOrEmpty(pDto.getScripData())
					&& pDto.getMwId() > 0) {
				sortFromCache(pDto.getScripData(), pDto.getUserId(), pDto.getMwId());
				sortScripInDataBase(pDto.getScripData(), pDto.getUserId(), pDto.getMwId());
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
	 * method to sort script in data base
	 * 
	 * @author sowmiya
	 * @param scripDataToSort
	 * @param userId
	 * @param mwId
	 */
	private void sortScripInDataBase(List<MwScripModel> scripDataToSort, String userId, int mwId) {

		if (scripDataToSort != null && scripDataToSort.size() > 0) {
			MarketWatchNameDTO mwList = mwNameRepo.findAllByUserIdAndMwId(userId, mwId);
			List<MarketWatchScripDetailsDTO> newScripDetails = new ArrayList<>();
			for (int i = 0; i < scripDataToSort.size(); i++) {
				MwScripModel model = new MwScripModel();
				model = scripDataToSort.get(i);
				for (int j = 0; j < mwList.getMwDetailsDTO().size(); j++) {
					MarketWatchScripDetailsDTO dbData = new MarketWatchScripDetailsDTO();
					dbData = mwList.getMwDetailsDTO().get(j);
					if (dbData.getToken().equalsIgnoreCase(model.getToken())
							&& dbData.getEx().equalsIgnoreCase(model.getExch())) {
						dbData.setSortingOrder(model.getSortingOrder());
						newScripDetails.add(dbData);
					}
				}
			}
			if (newScripDetails != null && newScripDetails.size() > 0) {
				marketWatchRepo.saveAll(newScripDetails);
			}
		}
	}

	/**
	 * method to sort from cache
	 * 
	 * @author sowmiya
	 * @param dataToSort
	 * @param pUserId
	 * @param userMwId
	 */
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
	 * 
	 * Method to Delete expired contract in MW
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> deleteExpiredContract() {

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayDate = format.format(date);
		return entityManager.deleteExpiredContract(todayDate);
	}

	/**
	 * method to prepare predefined market watch
	 * 
	 * @author SOWMIYA
	 * 
	 * @param predefined
	 * @return
	 */
	private List<JSONObject> preparePredefinedMw(boolean predefined, String userId) {
		List<JSONObject> predefinedMW = new ArrayList<>();
		try {
			List<PredefinedMwEntity> predefinedMwEntities = new ArrayList<>();
			List<PredefinedMwEntity> userPredefinedMwEntities = new ArrayList<>();

			/** Get predefined mw list from cache or DB **/
			if (HazelCacheController.getInstance().getMasterPredefinedMwList()
					.get(AppConstants.PREDEFINED_MW) != null) {
				predefinedMwEntities = HazelCacheController.getInstance().getMasterPredefinedMwList()
						.get(AppConstants.PREDEFINED_MW);
			} else {
				predefinedMwEntities = predefinedMwRepo.findAll();
			}

			/** Based on user preference set predefined MW List **/
			if (HazelCacheController.getInstance().getPerference()
					.get(userId + "_" + AppConstants.SOURCE_MOB) != null) {

				List<PreferenceModel> userPreferenceDetails = HazelCacheController.getInstance().getPerference()
						.get(userId + "_" + AppConstants.SOURCE_MOB);

				List<String> values = new ArrayList<>();
				for (PreferenceModel entity : userPreferenceDetails) {
					if (entity.getTag().equalsIgnoreCase("n50") && entity.getValue().equalsIgnoreCase("1")) {
						values.add("NIFTY 50");
					} else if (entity.getTag().equalsIgnoreCase("snx") && entity.getValue().equalsIgnoreCase("1")) {
						values.add("SENSEX");
					} else if (entity.getTag().equalsIgnoreCase("bnf") && entity.getValue().equalsIgnoreCase("1")) {
						values.add("NIFTY BANK");
					}
				}

				for (PredefinedMwEntity entity : predefinedMwEntities) {
					for (String value : values) {
						if (value.equalsIgnoreCase(entity.getMwName())) {
							userPredefinedMwEntities.add(entity);
							break;
						}
					}
				}
			} else {
				userPredefinedMwEntities.addAll(predefinedMwEntities);
			}

			if (StringUtil.isListNotNullOrEmpty(userPredefinedMwEntities)) {
				predefinedMW = preparePredefinedMWList(userPredefinedMwEntities);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return predefinedMW;
	}

	/**
	 * method to prepare predefined mw
	 * 
	 * @author Gowthaman M
	 * @param predefinedMw
	 * @return
	 */
	private List<JSONObject> preparePredefinedMWList(List<PredefinedMwEntity> predefinedMw) {
		List<JSONObject> predefinedMW = new ArrayList<>();
		for (PredefinedMwEntity preMW : predefinedMw) {
			JSONArray predefinedMWScrips = new JSONArray();
			JSONObject jObj1 = new JSONObject();
			jObj1.put("mwId", preMW.getMwId());
			jObj1.put("mwName", preMW.getMwName());
			jObj1.put("preDef", "1");
			for (PredefinedMwScripsEntity scrips : preMW.getScrips()) {
				JSONObject obj = new JSONObject();
				obj.put("exchange", scrips.getExchange());
				obj.put("segment", scrips.getSegment());
				obj.put("token", scrips.getToken());
				obj.put("tradingSymbol", scrips.getTradingSymbol());
				obj.put("formattedInsName", scrips.getFormattedInsName());
				obj.put("sortOrder", scrips.getSortOrder());
				obj.put("pdc", scrips.getPdc());
				obj.put("symbol", scrips.getSymbol());
				predefinedMWScrips.add(obj);
			}
			if (predefinedMWScrips != null && predefinedMWScrips.size() > 0) {
				JSONArray sortedPreMw = sortPreDefinedMW(predefinedMWScrips);
				jObj1.put("scrips", sortedPreMw);
			} else {
				jObj1.put("scrips", null);
			}
			predefinedMW.add(jObj1);
		}
		return predefinedMW;
	}

	/**
	 * Method to sort the predefined market watch
	 * 
	 * @author sowmiya
	 * @param predefinedMw
	 * @return
	 */
	private JSONArray sortPreDefinedMW(JSONArray predefinedMw) {
		JSONArray sorrtedArray = new JSONArray();
		try {
			List<JSONObject> list = new ArrayList<>();
			for (Object obj : predefinedMw) {
				list.add((JSONObject) obj);
			}
			list.sort(Comparator.comparing(o -> (String) o.get("symbol")));
			for (JSONObject jsonObject : list) {
				sorrtedArray.add(jsonObject);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return sorrtedArray;
	}

}

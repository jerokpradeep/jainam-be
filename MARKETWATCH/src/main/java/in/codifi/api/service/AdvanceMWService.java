package in.codifi.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.cache.HazelCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.primary.MarketWatchNameDTO;
import in.codifi.api.entity.primary.MarketWatchScripDetailsDTO;
import in.codifi.api.entity.primary.PredefinedMwEntity;
import in.codifi.api.entity.primary.PredefinedMwScripsEntity;
import in.codifi.api.model.AdvancedMWModel;
import in.codifi.api.model.CacheMwAdvDetailsModel;
import in.codifi.api.model.CacheMwDetailsModel;
import in.codifi.api.model.MWReqModel;
import in.codifi.api.model.MwRequestModel;
import in.codifi.api.model.MwScripModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.ScreenersModel;
import in.codifi.api.model.UserPerferencePreDefModel;
import in.codifi.api.repository.MarketWatchEntityManager;
import in.codifi.api.repository.MarketWatchNameRepository;
import in.codifi.api.repository.MarketWatchRepository;
import in.codifi.api.repository.PredefinedMwRepo;
import in.codifi.api.service.spec.IAdvanceMWService;
import in.codifi.api.util.AppConstants;
import in.codifi.api.util.PrepareResponse;
import in.codifi.api.util.StringUtil;
import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.cache.model.EventDataModel;
import in.codifi.cache.model.MtfDataModel;
import in.codifi.cache.model.PreferenceModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AdvanceMWService implements IAdvanceMWService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	MarketWatchEntityManager entityManager;
	@Inject
	ApplicationProperties props;
	@Inject
	MarketWatchNameRepository mwNameRepo;
	@Inject
	PredefinedMwRepo predefinedMwRepo;
	@Inject
	MarketWatchRepository mwRespo;
	@Inject
	MarketWatchRepository marketWatchRepo;

	/**
	 * method to advance market watch
	 * 
	 * @author sowmiya
	 * @param reqModel
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> advanceMW(MWReqModel reqModel, ClinetInfoModel info) {
		List<JSONObject> tempAdvanceMW = new ArrayList<JSONObject>();
		List<JSONObject> tempPreDefMW = new ArrayList<JSONObject>();
		List<JSONObject> combinedList = new ArrayList<JSONObject>();
		UserPerferencePreDefModel userPref = new UserPerferencePreDefModel();
		try {
			if (reqModel.isDefaultMw()) {
				List<JSONObject> mwScrips = getAllMwScripsAdvFlag(info.getUserId(), reqModel.isLstsFlag());
				if (mwScrips != null && mwScrips.size() > 0) {
					if (reqModel.isAdvFlag()) {
						if (HazelCacheController.getInstance().getUserPerferenceModel()
								.get(info.getUserId() + "_" + reqModel.getSource()) != null) {
							userPref = HazelCacheController.getInstance().getUserPerferenceModel()
									.get(info.getUserId() + "_" + reqModel.getSource());
						} else {
							userPref = prepareUserPreferenceList(info.getUserId(), reqModel.getSource());

						}
						List<JSONObject> advanceFlags = prepareScreeners(mwScrips, userPref);
						tempAdvanceMW = advanceFlags;
					} else {
						tempAdvanceMW = mwScrips;
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
				}
			}
			if (reqModel.isPreDef()) {
				if (StringUtil.isNullOrEmpty(reqModel.getSource()))
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				List<JSONObject> preDef = getAllMwScripsMobAdvanceFlag(info.getUserId(), reqModel.getSource(),
						reqModel.isLstsFlag());
				System.out.println("preDef -- " + preDef);
				if (preDef != null && preDef.size() > 0) {
					if (reqModel.isAdvFlag()) {
						if (HazelCacheController.getInstance().getUserPerferenceModel()
								.get(info.getUserId() + "_" + reqModel.getSource()) != null) {
							userPref = HazelCacheController.getInstance().getUserPerferenceModel()
									.get(info.getUserId() + "_" + reqModel.getSource());
							System.out.println("if userPref -- " + userPref);
						} else {
							userPref = prepareUserPreferenceList(info.getUserId(), reqModel.getSource());
							System.out.println("else userPref -- " + userPref);
						}
						List<JSONObject> advanceFlags = prepareScreeners(preDef, userPref);
						tempPreDefMW = advanceFlags;
						System.out.println("if tempPreDefMW -- " + tempPreDefMW);
					} else {
						tempPreDefMW = preDef;
						System.out.println("else tempPreDefMW -- " + tempPreDefMW);
					}
				}

			}
			if (reqModel.isDefaultMw() && reqModel.isPreDef()) {
				combinedList = Stream.concat(tempPreDefMW.stream(), tempAdvanceMW.stream())
						.collect(Collectors.toList());
				tempAdvanceMW = combinedList;
				System.out.println("tempAdvanceM -- W" + tempAdvanceMW);
				return prepareResponse.prepareSuccessResponseObject(combinedList);
			} else if (reqModel.isDefaultMw()) {
				return prepareResponse.prepareSuccessResponseObject(tempAdvanceMW);
			} else if (reqModel.isPreDef()) {
				return prepareResponse.prepareSuccessResponseObject(tempPreDefMW);
			}

		} catch (Exception e) {
			Log.error("advanceMW", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get advance market watch scrips
	 * 
	 * @author sowmiya
	 * @param reqModel
	 * @return
	 */
	@Override
	public RestResponse<ResponseModel> advanceMWScrips(MWReqModel reqModel, ClinetInfoModel info) {
		UserPerferencePreDefModel userPref = new UserPerferencePreDefModel();
		try {
			if (reqModel.isDefaultMw()) {
				if (reqModel.getMwId() > 0) {
					List<CacheMwAdvDetailsModel> mwScrips = getMWScrips(info.getUserId(), reqModel.getMwId());
					if (mwScrips != null && mwScrips.size() > 0) {
						if (reqModel.isLstsFlag()) {
							for (CacheMwAdvDetailsModel model : mwScrips) {
								String key = model.getExchange() + "_" + model.getToken();
								ContractMasterModel contractModel = HazelCacheController.getInstance()
										.getContractMaster().get(key);
								if (contractModel != null) {
									model.setLotSize(contractModel.getLotSize());
									model.setTickSize(contractModel.getTickSize());
								}
							}
						}
						if (reqModel.isAdvFlag()) {
							if (HazelCacheController.getInstance().getUserPerferenceModel()
									.get(info.getUserId() + "_" + reqModel.getSource()) != null) {
								userPref = HazelCacheController.getInstance().getUserPerferenceModel()
										.get(info.getUserId() + "_" + reqModel.getSource());
							} else {
								userPref = prepareUserPreferenceList(info.getUserId(), reqModel.getSource());
							}
							List<CacheMwDetailsModel> advanceFlags = prepareScreenersForModel(mwScrips, userPref);
							return prepareResponse.prepareSuccessResponseObject(advanceFlags);
						} else {
							return prepareResponse.prepareSuccessResponseObject(mwScrips);
						}
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
					}
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}
			if (reqModel.isPreDef()) {
				if (reqModel.getMwId() > 0) {
					if (HazelCacheController.getInstance().getUserPerferenceModel()
							.get(info.getUserId() + "_" + reqModel.getSource()) != null) {
						userPref = HazelCacheController.getInstance().getUserPerferenceModel()
								.get(info.getUserId() + "_" + reqModel.getSource());
					} else {
						userPref = prepareUserPreferenceList(info.getUserId(), reqModel.getSource());
					}
					List<JSONObject> advFlagForPreDef = getMWScripsForMob(userPref, info.getUserId(),
							reqModel.getMwId(), reqModel.isAdvFlag(), reqModel.isLstsFlag());
					return prepareResponse.prepareSuccessResponseObject(advFlagForPreDef);

				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_MW);
				}
			}
//			if (reqModel.isDefaultMw() && reqModel.isPreDef()) {
//
//			}
		} catch (Exception e) {
			Log.error("advanceMWScrips", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowrisankar
	 * @param userPref
	 * @param predefined, boolean advFlag
	 * @param mwid
	 * @param userId
	 * @param lstsFlag
	 * @param source
	 */
//	@Override
	@SuppressWarnings("unchecked")
	public List<JSONObject> getMWScripsForMob(UserPerferencePreDefModel userPref, String userId, int mwid,
			boolean advFlag, boolean lstsFlag) {
		List<JSONObject> resp = new ArrayList<>();
		try {
			resp = new ArrayList<>();
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
				if (predefinedMwEntity.getMwId() == mwid) {
					resp = preparePredefinedMWScripsList(predefinedMwEntity);
				}
			}

			if (StringUtil.isListNotNullOrEmpty(resp)) {
				for (JSONObject object : resp) {
					String key = object.get("exchange") + "_" + object.get("token");
					ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
					if (contractModel != null) {
						object.put("companyName", contractModel.getCompanyName());
						object.put("pdc", contractModel.getPdc());
					}
					if (lstsFlag) {
						object.put("lotSize", contractModel.getLotSize());
						object.put("tickSize", contractModel.getTickSize());
					}
					if (advFlag) {
						AdvancedMWModel model = HazelCacheController.getInstance().getAdvPredefinedMW().get(key);
						if (userPref.getScreeners() == 1) {
							if (model.getScreeners() != null && model.getScreeners().getName() != null
									&& model.getScreeners().getColorCode() != null) {
								object.put("screeners", model.getScreeners());
							} else {
								object.put("screeners", "");
							}
						}
						if (userPref.getMtfMargin() == 1) {
							object.put("mtfMargin", model.getMtfMargin());
						}
						if (userPref.getEvent() == 1) {
							object.put("event", model.isEvent());
						}
						if (userPref.getResearch() == 1) {
							object.put("research", model.isResearch());
						}
					}

				}
				return resp;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return resp;
	}

	/**
	 * Method to get the Scrip for given user id and market watch Id
	 * 
	 * @author Gowrisankar
	 * @param mwId
	 * @param userId
	 * @param advFlag
	 */
//	@Override
	@SuppressWarnings("unchecked")
	public List<CacheMwAdvDetailsModel> getMWScrips(String userId, int mwid) {
		List<CacheMwAdvDetailsModel> scripDetails = new ArrayList<>();

		try {
			List<JSONObject> res = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(userId);
			String marketWatchId = String.valueOf(mwid);

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
					scripDetails = (List<CacheMwAdvDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						return scripDetails;
					}
				}
			}
		} catch (Exception e) {
			Log.error("getMWScrips", e);
		}
		return scripDetails;
	}

	/**
	 * method to prepare screeners for model
	 * 
	 * @author SOWMIYA
	 * @param scripDetails
	 * @param userPref
	 * @return
	 */
	private List<CacheMwDetailsModel> prepareScreenersForModel(List<CacheMwAdvDetailsModel> scripDetails,
			UserPerferencePreDefModel userPref) {
		List<CacheMwDetailsModel> resultList = new ArrayList<>();
		CacheMwDetailsModel cacheModel = new CacheMwDetailsModel();
		/** logic to add mtf margin **/
		for (CacheMwAdvDetailsModel model : scripDetails) {
			cacheModel = new CacheMwDetailsModel();
			boolean isTopGainer = false;
			boolean is52WeekHigh = false;
			boolean isTopLoser = false;
			boolean is52WeekLow = false;
			AdvancedMWModel advMWModel = new AdvancedMWModel();
			String key = model.getExchange() + "_" + model.getToken();
			cacheModel.setExchange(model.getExchange());
			cacheModel.setExpiry(model.getExpiry());
			cacheModel.setFormattedInsName(model.getFormattedInsName());
			cacheModel.setMwId(model.getMwId());
			cacheModel.setMwName(model.getMwName());
			cacheModel.setToken(model.getToken());
			cacheModel.setSegment(model.getSegment());
			cacheModel.setSortOrder(model.getSortOrder());
			cacheModel.setSymbol(model.getSymbol());
			cacheModel.setTradingSymbol(model.getTradingSymbol());
			cacheModel.setLotSize(model.getLotSize());
			cacheModel.setTickSize(model.getTickSize());

//			if (HazelCacheController.getInstance().getAdvMWData().get(key) != null) {
//				advMWModel = HazelCacheController.getInstance().getAdvMWData().get(key);
//				model.setScreeners(advMWModel.getScreeners());
//				model.setEvent(advMWModel.isEvent());
//				model.setResearch(advMWModel.isResearch());
//				model.setMtfMargin(advMWModel.getMtfMargin());
//				ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
//				if (contractModel != null) {
//					model.setCompanyName(contractModel.getCompanyName());
//					model.setPdc(contractModel.getPdc());
//				}
//
//			} else {
			/** logic to add MTF Margin **/
			if (userPref.getMtfMargin() == 1) {
				MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(key);
				if (mtfData != null && mtfData.getMtfMargin() > 0) {
					cacheModel.setMtfMargin(mtfData.getMtfMargin());
					advMWModel.setMtfMargin(mtfData.getMtfMargin());
				}
			}

			/** logic to add Event **/
			if (userPref.getEvent() == 1) {
				EventDataModel eventData = HazelCacheController.getInstance().getEventData().get(key);
				if (eventData != null) {
					cacheModel.setEvent(true);
					advMWModel.setEvent(true);
				}
			}

			/** logic to add screener **/
			if (userPref.getScreeners() == 1) {
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
					cacheModel.setScreeners(screeners);
				}
			}
			ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
			if (contractModel != null) {
				cacheModel.setCompanyName(contractModel.getCompanyName());
				cacheModel.setPdc(contractModel.getPdc());
			}
			resultList.add(cacheModel);
		}

		return resultList;
	}

	/**
	 * method prepare predefined mw scrips list
	 * 
	 * @author SOWMIYA
	 * @param predefinedMw
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<JSONObject> preparePredefinedMWScripsList(PredefinedMwEntity predefinedMw) {
		List<JSONObject> predefinedMWScrips = new ArrayList<>();
		int count = 0;

		List<String> symbolList = new ArrayList<>();
		for (PredefinedMwScripsEntity scrips : predefinedMw.getScrips()) {
			PredefinedMwScripsEntity scrip = new PredefinedMwScripsEntity();
			scrip.setExchange(scrips.getExchange());
			scrip.setSegment(scrips.getSegment());
			scrip.setToken(scrips.getToken());
			scrip.setTradingSymbol(scrips.getTradingSymbol());
			scrip.setFormattedInsName(scrips.getFormattedInsName());
			scrip.setSortOrder(count);
			scrip.setPdc(scrips.getPdc());
			scrip.setSymbol(scrips.getSymbol());
			symbolList.add(scrips.getSymbol());
			HazelCacheController.getInstance().getSortPredefinedMwScripsEntity().put(scrips.getSymbol(), scrip);
		}

		Iterator<String> itr = symbolList.iterator();
		itr = sortedIterator(itr);
		while (itr.hasNext()) {
			PredefinedMwScripsEntity scrips = HazelCacheController.getInstance().getSortPredefinedMwScripsEntity()
					.get(itr.next());
			JSONObject obj = new JSONObject();
			String key = scrips.getExchange() + "_" + scrips.getToken();
			obj.put("exchange", scrips.getExchange());
			obj.put("segment", scrips.getSegment());
			obj.put("token", scrips.getToken());
			obj.put("tradingSymbol", scrips.getTradingSymbol());
			obj.put("formattedInsName", scrips.getFormattedInsName());
			obj.put("sortOrder", count++);
			obj.put("pdc", scrips.getPdc());
			obj.put("symbol", scrips.getSymbol());
			if (HazelCacheController.getInstance().getContractMaster().get(key) != null) {
				ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
				if (contractModel != null) {
					obj.put("companyName", contractModel.getCompanyName());
					obj.put("pdc", contractModel.getPdc());
				}
			}
			predefinedMWScrips.add(obj);
		}

		HazelCacheController.getInstance().getSortPredefinedMwScripsEntity().clear();

//		for (PredefinedMwScripsEntity scrips : predefinedMw.getScrips()) {
//			JSONObject obj = new JSONObject();
//			String key = scrips.getExchange() + "_" + scrips.getToken();
//			obj.put("exchange", scrips.getExchange());
//			obj.put("segment", scrips.getSegment());
//			obj.put("token", scrips.getToken());
//			obj.put("tradingSymbol", scrips.getTradingSymbol());
//			obj.put("formattedInsName", scrips.getFormattedInsName());
//			obj.put("sortOrder", count++);
//			obj.put("pdc", scrips.getPdc());
//			obj.put("symbol", scrips.getSymbol());
//			if (HazelCacheController.getInstance().getContractMaster().get(key) != null) {
//				ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster().get(key);
//				if (contractModel != null) {
//					obj.put("companyName", contractModel.getCompanyName());
//					obj.put("pdc", contractModel.getPdc());
//				}
//			}
//			predefinedMWScrips.add(obj);
//		}
		return predefinedMWScrips;
	}

	/**
	 * method to get All market watch scrips
	 * 
	 * @author sowmiya
	 * @param userId
	 * @param source
	 * @param lstsFlag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<JSONObject> getAllMwScripsMobAdvanceFlag(String userId, String source, boolean lstsFlag) {
		List<JSONObject> predefinedMW = new ArrayList<JSONObject>();
		try {
//			predefinedMW = preparePredefinedMw(source, userId);
			predefinedMW = preparePredefinedMw(userId, source);
			if (predefinedMW != null && predefinedMW.size() > 0) {
				/** if logic to get lot size and tick size **/
				if (lstsFlag) {
					for (JSONObject scrips : predefinedMW) {
						List<JSONObject> tempList = (List<JSONObject>) scrips.get("scrips");
						for (JSONObject model : tempList) {
							String key = model.get("exchange") + "_" + model.get("token");
							ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster()
									.get(key);
							if (contractModel != null) {
								model.put("lotSize", contractModel.getLotSize());
								model.put("tickSize", contractModel.getTickSize());
							}
						}
					}
				} else {
					return predefinedMW;
				}
			}
		} catch (Exception e) {
			Log.error("getAllMwScripsMobAdv", e);
		}
		return predefinedMW;

	}

	/**
	 * method to get all market watach scrips for advance flag
	 * 
	 * @author sowmiya
	 * @param pUserId
	 * @param lstsFlag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> getAllMwScripsAdvFlag(String pUserId, boolean lstsFlag) {
		List<JSONObject> tempResponse = new ArrayList<JSONObject>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			/*
			 * Check the user has the scrips in cache or not
			 */

			List<JSONObject> result = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
			if (result != null && result.size() > 0) {
				for (JSONObject resultModel : result) {
					List<CacheMwAdvDetailsModel> tempList = (List<CacheMwAdvDetailsModel>) resultModel.get("scrips");
					if (tempList != null && tempList.size() > 0) {
						for (CacheMwAdvDetailsModel cacheMW : tempList) {
							String key = cacheMW.getExchange() + "_" + cacheMW.getToken();
							if (HazelCacheController.getInstance().getContractMaster().get(key) != null) {
								ContractMasterModel contractModel = HazelCacheController.getInstance()
										.getContractMaster().get(key);
								if (contractModel != null) {
									cacheMW.setCompanyName(contractModel.getCompanyName());
									cacheMW.setPdc(contractModel.getPdc());
								}
								/** if logic to get lot size and tick size **/
								if (lstsFlag) {
									cacheMW.setLotSize(contractModel.getLotSize());
									cacheMW.setTickSize(contractModel.getTickSize());
								}
							}
						}

					}
				}

				/*
				 * if cache is there return from then return from cache
				 */

				tempResponse = result;

			} else {
				/*
				 * take the scrip details from the Data base for the user
				 */
				List<CacheMwAdvDetailsModel> scripDetails = entityManager.getMarketWatchAdvByUserId(pUserId);

				if (scripDetails != null && scripDetails.size() > 0) {
					/*
					 * Populate the filed for Marketwatch as per the requirement
					 */
					List<JSONObject> tempResult = populateAdvFields(scripDetails, pUserId);
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
					}
				}
			}
			if (tempResponse != null && tempResponse.size() > 0) {

				return tempResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return tempResponse;
	}

	/**
	 * method to populate advance fields
	 * 
	 * @author sowmiya
	 * @param cacheMwDetailsModels
	 * @param pUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<JSONObject> populateAdvFields(List<CacheMwAdvDetailsModel> cacheMwDetailsModels, String pUserId) {

		List<JSONObject> response = new ArrayList<>();
		try {
			JSONObject tempResponse = new JSONObject();
			for (CacheMwAdvDetailsModel tempModel : cacheMwDetailsModels) {
				String mwName = tempModel.getMwName();
				String mwId = String.valueOf(tempModel.getMwId());
				String tempMwID = pUserId + "_" + mwId + "_" + mwName;
				String scripName = tempModel.getFormattedInsName();
				if (scripName != null && !scripName.isEmpty()) {
					if (tempResponse.containsKey(tempMwID)) {
						List<CacheMwAdvDetailsModel> tempList = new ArrayList<>();
						if (tempResponse.get(tempMwID) != null) {
							tempList = (List<CacheMwAdvDetailsModel>) tempResponse.get(tempMwID);
						}
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					} else {
						List<CacheMwAdvDetailsModel> tempList = new ArrayList<>();
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					}
				} else if (tempResponse.get(tempMwID) == null) {
					tempResponse.put(tempMwID, null);
				}
			}
			if (tempResponse != null) {
				response = getCacheListForScripsAdv(tempResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
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
	@SuppressWarnings("unchecked")
	public List<JSONObject> getCacheListForScripsAdv(JSONObject mwResponse) {
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
				List<CacheMwAdvDetailsModel> tempJsonObject = new ArrayList<CacheMwAdvDetailsModel>();
				tempJsonObject = (List<CacheMwAdvDetailsModel>) mwResponse.get(tempStr);
				result.put("mwId", mwId);
				result.put("mwName", mwName);
				if (tempJsonObject != null && tempJsonObject.size() > 0) {
					result.put("scrips", tempJsonObject);
				} else {
					result.put("scrips", null);
				}

				response = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(user);
				if (response != null) {
					response = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(user);
					response.add(result);
					HazelCacheController.getInstance().getAdvanceMWListByUserId().put(user, response);
				} else {
					response = new ArrayList<JSONObject>();
					response.add(result);
					HazelCacheController.getInstance().getAdvanceMWListByUserId().put(user, response);
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * method to prepare screeners
	 * 
	 * @author sowmiya
	 * @param result
	 * @param userPref
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> prepareScreeners(List<JSONObject> result, UserPerferencePreDefModel userPref) {
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
								if (userPref.getScreeners() == 1) {
									System.out.println("screener put successfully");
									tempModel.put("screeners", advanceModel.getScreeners());
								}
							} else {
								if (userPref.getScreeners() == 1) {
									tempModel.put("screeners", "");
									System.out.println("screener failed successfully" + tempModel.get("screeners"));
								}
							}
							if (userPref.getEvent() == 1) {
								tempModel.put("event", advanceModel.isEvent());
								System.out.println("event put successfully" + tempModel.get("event"));
							}
							if (userPref.getResearch() == 1) {
								tempModel.put("research", advanceModel.isResearch());
								System.out.println("research put successfully" + tempModel.get("research"));
							}
							if (userPref.getMtfMargin() == 1) {
								tempModel.put("mtfMargin", advanceModel.getMtfMargin());
								System.out.println("mtf put successfully" + tempModel.get("mtfMargin"));
							}
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
				List<CacheMwAdvDetailsModel> tempList = (List<CacheMwAdvDetailsModel>) model.get("scrips");
				List<CacheMwDetailsModel> resultList = new ArrayList<>();
				if (tempList != null && tempList.size() > 0) {
					for (CacheMwAdvDetailsModel tempModel : tempList) {
						CacheMwDetailsModel cacheModel = new CacheMwDetailsModel();
						boolean isTopGainer = false;
						boolean is52WeekHigh = false;
						boolean isTopLoser = false;
						boolean is52WeekLow = false;
						cacheModel.setExchange(tempModel.getExchange());
						cacheModel.setExpiry(tempModel.getExpiry());
						cacheModel.setFormattedInsName(tempModel.getFormattedInsName());
						cacheModel.setMwId(tempModel.getMwId());
						cacheModel.setMwName(tempModel.getMwName());
						cacheModel.setSegment(tempModel.getSegment());
						cacheModel.setSortOrder(tempModel.getSortOrder());
						cacheModel.setSymbol(tempModel.getSymbol());
						cacheModel.setToken(tempModel.getToken());
						cacheModel.setTradingSymbol(tempModel.getTradingSymbol());
						cacheModel.setLotSize(tempModel.getLotSize());
						cacheModel.setTickSize(tempModel.getTickSize());

						AdvancedMWModel advMWModel = new AdvancedMWModel();
						if (tempModel.getExchange() != null && tempModel.getToken() != null) {
							key = tempModel.getExchange() + "_" + tempModel.getToken();

							/** logic to add MTF Margin **/
							if (userPref.getMtfMargin() == 1) {
								MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(key);
								if (mtfData != null && mtfData.getMtfMargin() > 0) {

									cacheModel.setMtfMargin(mtfData.getMtfMargin());
									System.out.println("mtf scrips put successfully" + cacheModel.getMtfMargin());
									advMWModel.setMtfMargin(mtfData.getMtfMargin());
								}
							}

							/** logic to add Event **/
							if (userPref.getEvent() == 1) {
								EventDataModel eventData = HazelCacheController.getInstance().getEventData().get(key);
								if (eventData != null) {
									cacheModel.setEvent(true);
									System.out.println("event put successfully" + cacheModel.isEvent());
									advMWModel.setEvent(true);
								}
							}

							/** logic to add screener **/
							if (userPref.getScreeners() == 1) {
								ScreenersModel screeners = new ScreenersModel();
								/** top gainers **/
								AnalysisRespModel topGainerModel = HazelCacheController.getInstance().getTopGainers()
										.get(key);
								if (topGainerModel != null
										&& topGainerModel.getDirection().equalsIgnoreCase("Bullish")) {
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
								AnalysisRespModel fiftyTwoWeekLow = HazelCacheController.getInstance()
										.getFiftyTwoWeekLow().get(key);
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
									cacheModel.setScreeners(screeners);
									System.out.println("screeners put successfully" + cacheModel.getScreeners());
									advMWModel.setScreeners(screeners);
								}
							}
							ContractMasterModel contractModel = HazelCacheController.getInstance().getContractMaster()
									.get(key);
							if (contractModel != null) {
								cacheModel.setCompanyName(contractModel.getCompanyName());
								cacheModel.setPdc(contractModel.getPdc());
							}
							resultList.add(cacheModel);
						}
					}
					model.put("scrips", resultList);
				}
			}
		}
		return result;
	}

	/**
	 * method to prepare user preference
	 * 
	 * @author sowmiya
	 * @param pUserId
	 * @return
	 */
	private UserPerferencePreDefModel prepareUserPreferenceList(String pUserId, String source) {
		UserPerferencePreDefModel predefModel = new UserPerferencePreDefModel();
		try {
			/** Based on user preference set predefined MW List **/
			if (HazelCacheController.getInstance().getPerference().get(pUserId + "_" + source) != null) {
				List<PreferenceModel> userPreferenceDetails = HazelCacheController.getInstance().getPerference()
						.get(pUserId + "_" + source);
				HazelCacheController.getInstance().getUserPerferenceModel().clear();
				for (PreferenceModel entity : userPreferenceDetails) {
					if (entity.getTag().equalsIgnoreCase("scn") && entity.getValue().equalsIgnoreCase("1")) {
						predefModel.setScreeners(1);
					} else if (entity.getTag().equalsIgnoreCase("evt") && entity.getValue().equalsIgnoreCase("1")) {
						predefModel.setEvent(1);
					} else if (entity.getTag().equalsIgnoreCase("rvs") && entity.getValue().equalsIgnoreCase("1")) {
						predefModel.setResearch(1);
					} else if (entity.getTag().equalsIgnoreCase("mtf") && entity.getValue().equalsIgnoreCase("1")) {
						predefModel.setMtfMargin(1);
					}
					HazelCacheController.getInstance().getUserPerferenceModel().put(pUserId + "_" + source,
							predefModel);
				}
			} else {
				predefModel.setEvent(1);
				predefModel.setMtfMargin(1);
				predefModel.setResearch(1);
				predefModel.setScreeners(1);

			}
		} catch (Exception e) {
			Log.error(e);
		}
		return predefModel;
	}

	/**
	 * Method to populate the fields for the user scrips details for given user
	 * 
	 * @author Gowrisankar
	 * @param userScripDetails
	 * @param pUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<JSONObject> populateFields(List<CacheMwAdvDetailsModel> cacheMwDetailsModels, String pUserId) {

		List<JSONObject> response = new ArrayList<>();
		try {
			JSONObject tempResponse = new JSONObject();
			for (CacheMwAdvDetailsModel tempModel : cacheMwDetailsModels) {
				String mwName = tempModel.getMwName();
				String mwId = String.valueOf(tempModel.getMwId());
				String tempMwID = pUserId + "_" + mwId + "_" + mwName;
				String scripName = tempModel.getFormattedInsName();
				if (scripName != null && !scripName.isEmpty()) {
					if (tempResponse.containsKey(tempMwID)) {
						List<CacheMwAdvDetailsModel> tempList = new ArrayList<>();
						if (tempResponse.get(tempMwID) != null) {
							tempList = (List<CacheMwAdvDetailsModel>) tempResponse.get(tempMwID);
						}
						tempList.add(tempModel);
						tempResponse.put(tempMwID, tempList);
					} else {
						List<CacheMwAdvDetailsModel> tempList = new ArrayList<>();
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
	@SuppressWarnings({ "unchecked" })
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
				List<CacheMwAdvDetailsModel> tempJsonObject = new ArrayList<CacheMwAdvDetailsModel>();
				tempJsonObject = (List<CacheMwAdvDetailsModel>) mwResponse.get(tempStr);
				result.put("mwId", mwId);
				result.put("mwName", mwName);
				if (tempJsonObject != null && tempJsonObject.size() > 0) {
					result.put("scrips", tempJsonObject);
				} else {
					result.put("scrips", null);
				}

				response = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(user);
				if (response != null) {
					response = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(user);
					response.add(result);
					HazelCacheController.getInstance().getAdvanceMWListByUserId().put(user, response);
				} else {
					response = new ArrayList<JSONObject>();
					response.add(result);
					HazelCacheController.getInstance().getAdvanceMWListByUserId().put(user, response);
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
					List<CacheMwAdvDetailsModel> scripDetails = entityManager.getAdvMarketWatchByUserId(pUserId);
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
	 * method to prepare predefined market watch
	 * 
	 * @author SOWMIYA
	 * 
	 * @param predefined
	 * @return
	 */
	private List<JSONObject> preparePredefinedMw(String userId, String source) {
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
			if (HazelCacheController.getInstance().getPerference().get(userId + "_" + source) != null) {

				List<PreferenceModel> userPreferenceDetails = HazelCacheController.getInstance().getPerference()
						.get(userId + "_" + source);

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
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return predefinedMW;
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
					List<CacheMwAdvDetailsModel> scripDetails = entityManager.getAdvMarketWatchByUserId(pUserId);
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
	@SuppressWarnings("unchecked")
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

	/**
	 * Method to provide the User scrips details from the data base or cache
	 * 
	 * @author Gowrisankar
	 */
//	@Override
	public RestResponse<ResponseModel> getAllMwScrips(String pUserId) {
		boolean isAdvancedMW = true;
		try {
			/*
			 * Check the user has the scrips in cache or not
			 */

			List<JSONObject> tempResponse = new ArrayList<JSONObject>();

			List<JSONObject> result = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
			if (result != null && result.size() > 0) {
				/*
				 * if cache is there return from then return from cache
				 */

				tempResponse = result;

			} else {
				/*
				 * take the scrip details from the Data base for the user
				 */
				List<CacheMwAdvDetailsModel> scripDetails = entityManager.getAdvMarketWatchByUserId(pUserId);
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
//					UserPerferencePreDefModel userPref = prepareUserPreferenceList(pUserId,);
//					List<JSONObject> response = prepareScreeners(tempResponse, userPref);
					return prepareResponse.prepareSuccessResponseObject(tempResponse);
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
	 * Method to add the scrip into cache and data baseF
	 * 
	 * @author Dinesh Kumar
	 */
//	@Override
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
				List<CacheMwAdvDetailsModel> scripDetails = getScripMW(mwScripModels);
				List<CacheMwDetailsModel> mwScrips = new ArrayList<>();
				if (scripDetails != null && scripDetails.size() > 0) {
					/** logic to add mtf margin **/
					for (CacheMwAdvDetailsModel model : scripDetails) {
						CacheMwDetailsModel cacheModel = new CacheMwDetailsModel();
						cacheModel.setCompanyName(model.getCompanyName());
						cacheModel.setExchange(model.getExchange());
						cacheModel.setExpiry(model.getExpiry());
						cacheModel.setPdc(model.getPdc());
						cacheModel.setSegment(model.getSegment());
						cacheModel.setTradingSymbol(model.getTradingSymbol());
						cacheModel.setFormattedInsName(model.getFormattedInsName());
						cacheModel.setLotSize(model.getLotSize());
						cacheModel.setMwId(model.getMwId());
						cacheModel.setMwName(model.getMwName());
						cacheModel.setTickSize(model.getTickSize());
						cacheModel.setToken(model.getToken());
						cacheModel.setUserId(model.getUserId());
						cacheModel.setSymbol(model.getSymbol());
						String mtfKey = model.getExchange() + "_" + model.getToken();
						MtfDataModel mtfData = HazelCacheController.getInstance().getMtfDataModel().get(mtfKey);
						if (mtfData != null && mtfData.getMtfMargin() > 0) {
							cacheModel.setMtfMargin(mtfData.getMtfMargin());
						}
						mwScrips.add(cacheModel);
					}
					List<CacheMwAdvDetailsModel> newScripDetails = addNewScipsForMwIntoCache(scripDetails,
							parmDto.getUserId(), parmDto.getMwId());
					if (newScripDetails != null && newScripDetails.size() > 0) {
						insertNewScipsForMwIntoDataBase(newScripDetails, parmDto.getUserId(), parmDto.getMwId());
					}
					return prepareResponse.prepareSuccessResponseObject(mwScrips);
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
		List<JSONObject> res = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
		JSONObject result = null;
		String marketWatchId = String.valueOf(mwid);
		if (res != null && res.size() > 0) {
			for (int itr = 0; itr < res.size(); itr++) {
				result = new JSONObject();
				result = res.get(itr);
				String tempMwId = (String) result.get("mwId");
				if (tempMwId.equalsIgnoreCase(marketWatchId)) {
					@SuppressWarnings("unchecked")
					List<CacheMwAdvDetailsModel> scripDetails = (List<CacheMwAdvDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						Optional<CacheMwAdvDetailsModel> maxByOrder = scripDetails.stream()
								.max(Comparator.comparing(CacheMwAdvDetailsModel::getSortOrder));
						CacheMwAdvDetailsModel model = maxByOrder.get();
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
	public List<CacheMwAdvDetailsModel> getScripMW(List<MwScripModel> pDto) {
		List<CacheMwAdvDetailsModel> response = new ArrayList<>();
		try {
			for (int itr = 0; itr < pDto.size(); itr++) {
				MwScripModel result = new MwScripModel();
				result = pDto.get(itr);
				String exch = result.getExch();
				String token = result.getToken();
				System.out.println(HazelCacheController.getInstance().getContractMaster().size());
				if (HazelCacheController.getInstance().getContractMaster().get(exch + "_" + token) != null) {
					CacheMwAdvDetailsModel fResult = new CacheMwAdvDetailsModel();
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
					fResult.setCompanyName(masterData.getCompanyName());
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
	 * @author Gowrisankar
	 * @param newScripDetails
	 * @param pUserId
	 * @param userMwId
	 */
	@SuppressWarnings("unchecked")
	public List<CacheMwAdvDetailsModel> addNewScipsForMwIntoCache(List<CacheMwAdvDetailsModel> newScripDetails,
			String pUserId, int userMwId) {
		List<CacheMwAdvDetailsModel> responseModel = new ArrayList<>();
		responseModel.addAll(newScripDetails);
		List<JSONObject> res = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
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
				List<CacheMwAdvDetailsModel> scripDetails = (List<CacheMwAdvDetailsModel>) result.get("scrips");
				List<CacheMwAdvDetailsModel> latestScripDetails = new ArrayList<>();
				if (scripDetails != null && scripDetails.size() > 0) {
					latestScripDetails.addAll(scripDetails);
					for (int i = 0; i < newScripDetails.size(); i++) {
						CacheMwAdvDetailsModel tempNewScrip = newScripDetails.get(i);
						String tempNewToken = tempNewScrip.getToken();
						String tempNewExch = tempNewScrip.getExchange();
						int alreadyAdded = 0;
						for (int j = 0; j < scripDetails.size(); j++) {
							CacheMwAdvDetailsModel scrip = scripDetails.get(j);
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
				HazelCacheController.getInstance().getAdvanceMWListByUserId().remove(pUserId);
				HazelCacheController.getInstance().getAdvanceMWListByUserId().put(pUserId, res);
			}
		}
		return responseModel;
	}

	/**
	 * Method to insert into data base in thread
	 * 
	 * @author Dinesh Kumar
	 * @param parmDto
	 */
	private void insertNewScipsForMwIntoDataBase(List<CacheMwAdvDetailsModel> scripDetails, String userId, int mwId) {
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
	 * method to prepare market watch entity
	 * 
	 * @author Sowmiya
	 * @param scripDetails
	 * @param userId
	 * @param mwId
	 * @return
	 */
	private List<MarketWatchScripDetailsDTO> prepareMarketWatchEntity(List<CacheMwAdvDetailsModel> scripDetails,
			String userId, int mwId) {

		List<MarketWatchScripDetailsDTO> marketWatchScripDetailsDTOs = new ArrayList<MarketWatchScripDetailsDTO>();
		for (int i = 0; i < scripDetails.size(); i++) {
			CacheMwAdvDetailsModel model = scripDetails.get(i);
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
	 * 
	 * Method to Sort MW scrips
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param pDto
	 * @return
	 */
//	@Override
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
	 * method to sort from cache
	 * 
	 * @author sowmiya
	 * @param dataToSort
	 * @param pUserId
	 * @param userMwId
	 */
	@SuppressWarnings("unchecked")
	public void sortFromCache(List<MwScripModel> dataToSort, String pUserId, int userMwId) {
		if (dataToSort != null && dataToSort.size() > 0) {
			List<JSONObject> res = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
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
					List<CacheMwAdvDetailsModel> scripDetails = (List<CacheMwAdvDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < dataToSort.size(); i++) {
							MwScripModel tempDTO = dataToSort.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExch();
							int sortOrder = tempDTO.getSortingOrder();
							for (int j = 0; j < scripDetails.size(); j++) {
								CacheMwAdvDetailsModel tempScripDTO = scripDetails.get(j);
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
						HazelCacheController.getInstance().getAdvanceMWListByUserId().remove(pUserId);
						HazelCacheController.getInstance().getAdvanceMWListByUserId().put(pUserId, res);
					}
				}
			}
		}
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
	 * Method to delete the scrips from the cache and market watch
	 * 
	 * @author Gowrisankar
	 * @param pDto
	 * @return
	 */
//	@Override
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
	@SuppressWarnings("unchecked")
	public void deleteFromCache(List<MwScripModel> dataToDelete, String pUserId, int userMwId) {
		if (dataToDelete != null && dataToDelete.size() > 0) {
			List<JSONObject> res = HazelCacheController.getInstance().getAdvanceMWListByUserId().get(pUserId);
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
					List<CacheMwAdvDetailsModel> scripDetails = (List<CacheMwAdvDetailsModel>) result.get("scrips");
					if (scripDetails != null && scripDetails.size() > 0) {
						for (int i = 0; i < dataToDelete.size(); i++) {
							MwScripModel tempDTO = dataToDelete.get(i);
							String token = tempDTO.getToken();
							String exch = tempDTO.getExch();
							for (int j = 0; j < scripDetails.size(); j++) {
								CacheMwAdvDetailsModel tempScripDTO = scripDetails.get(j);
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
						HazelCacheController.getInstance().getAdvanceMWListByUserId().remove(pUserId);
						HazelCacheController.getInstance().getAdvanceMWListByUserId().put(pUserId, res);
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

}

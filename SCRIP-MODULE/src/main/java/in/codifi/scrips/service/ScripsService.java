package in.codifi.scrips.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.AdminPreferenceModel;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.cache.model.MtfDataModel;
import in.codifi.scrips.config.HazelcastConfig;
import in.codifi.scrips.entity.chartdb.PromptModel;
import in.codifi.scrips.entity.primary.FiftytwoWeekDataEntity;
import in.codifi.scrips.entity.primary.PromptEntity;
import in.codifi.scrips.model.request.GetContractInfoReqModel;
import in.codifi.scrips.model.request.SearchScripReqModel;
import in.codifi.scrips.model.request.SecurityInfoReqModel;
import in.codifi.scrips.model.response.ContractInfoDetails;
import in.codifi.scrips.model.response.ContractInfoRespModel;
import in.codifi.scrips.model.response.GenericResponse;
import in.codifi.scrips.model.response.ScripSearchResp;
import in.codifi.scrips.repository.AsmGsmRepository;
import in.codifi.scrips.repository.ScripSearchEntityManager;
import in.codifi.scrips.service.spec.ScripsServiceSpecs;
import in.codifi.scrips.utility.AppConstants;
import in.codifi.scrips.utility.AppUtil;
import in.codifi.scrips.utility.PrepareResponse;
import in.codifi.scrips.utility.StringUtil;
import in.codifi.scrips.ws.model.SecurityInfoRestReq;
import in.codifi.scrips.ws.service.SecurityInfoRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ScripsService implements ScripsServiceSpecs {

	@Inject
	ScripSearchEntityManager scripSearchRepo;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	SecurityInfoRestService restService;
	@Inject
	AsmGsmRepository asmGsmRepo;

	/**
	 * Method to get all scrips by search
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScrips(SearchScripReqModel reqModel) {
		List<ScripSearchResp> responses = new ArrayList<>();
		try {
			/** To check where to fetch data */
			if (HazelcastConfig.getInstance().getFetchDataFromCache().get(AppConstants.FETCH_DATA_FROM_CACHE) != null
					&& HazelcastConfig.getInstance().getFetchDataFromCache().get(AppConstants.FETCH_DATA_FROM_CACHE)) {

				if (reqModel.getSymbol().trim().length() < 3) {
					if (HazelcastConfig.getInstance().getDistinctSymbols()
							.get(reqModel.getSymbol().trim().length()) != null
							&& HazelcastConfig.getInstance().getDistinctSymbols()
									.get(reqModel.getSymbol().trim().length()).size() > 0
							&& HazelcastConfig.getInstance().getDistinctSymbols()
									.get(reqModel.getSymbol().trim().length())
									.contains(reqModel.getSymbol().trim().toUpperCase())) {
						responses = getSearchDetailsFromCache(reqModel);
					}
				} else {
					responses = getSearchDetailsFromCache(reqModel);
				}
			} else {
				if (reqModel.getSymbol().trim().length() < 3) {
					if (HazelcastConfig.getInstance().getDistinctSymbols()
							.get(reqModel.getSymbol().trim().length()) != null
							&& HazelcastConfig.getInstance().getDistinctSymbols()
									.get(reqModel.getSymbol().trim().length()).size() > 0
							&& HazelcastConfig.getInstance().getDistinctSymbols()
									.get(reqModel.getSymbol().trim().length())
									.contains(reqModel.getSymbol().trim().toUpperCase())) {
						responses = scripSearchRepo.getScrips(reqModel);
					}
				} else {
					responses = scripSearchRepo.getScrips(reqModel);
				}
			}
			if (responses != null && responses.size() > 0) {
				return prepareResponse.prepareSuccessResponseObject(responses);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/** Get search details from cache */
	private List<ScripSearchResp> getSearchDetailsFromCache(SearchScripReqModel reqModel) {
		List<ScripSearchResp> responses = new ArrayList<>();
		/** Check the cache is not and storing is enabled or not */
		String[] exchange = reqModel.getExchange();
		/** Check Exchange array contains ALL */
		if (Arrays.stream(exchange).anyMatch("all"::equalsIgnoreCase)) {
			if (HazelcastConfig.getInstance().getLoadedSearchData()
					.get(reqModel.getSymbol().trim().toUpperCase()) != null) {
				responses = HazelcastConfig.getInstance().getLoadedSearchData()
						.get(reqModel.getSymbol().trim().toUpperCase());
			} else {
				responses = scripSearchRepo.getScrips(reqModel);
				if (responses != null && responses.size() > 0) {
					if (HazelcastConfig.getInstance().getIndexDetails()
							.get(reqModel.getSymbol().trim().toUpperCase()) != null) {
						ScripSearchResp result = HazelcastConfig.getInstance().getIndexDetails()
								.get(reqModel.getSymbol().trim().toUpperCase());
						responses.set(0, result);
						if (responses.size() > 24) {
							responses.remove(25);
						}
					}
					HazelcastConfig.getInstance().getLoadedSearchData().put(reqModel.getSymbol().trim().toUpperCase(),
							responses);
				}
			}
		} else {
			responses = scripSearchRepo.getScrips(reqModel);
		}
		return responses;
	}

//	/**
//	 * Method to get contract info
//	 * 
//	 * @author SOWMIYA
//	 * @return
//	 */
//	@Override
//	public RestResponse<GenericResponse> getContractInfo(GetContractInfoReqModel model) {
//		try {
//			ContractInfoRespModel response = new ContractInfoRespModel();
//			List<ContractInfoDetails> detailsList = new ArrayList<>();
//			if (model != null && StringUtil.isNotNullOrEmpty(model.getToken())
//					&& StringUtil.isNotNullOrEmpty(model.getExch())) {
//				String token = model.getToken();
//				String exch = model.getExch().toUpperCase();
//				ContractMasterModel contractMasterModel = HazelcastConfig.getInstance().getContractMaster()
//						.get(exch + "_" + token);
//				if (ObjectUtils.isNotEmpty(contractMasterModel)) {
//					ContractInfoDetails details = prepareContractInfoResp(contractMasterModel);
//					detailsList.add(details);
//
//					/** Logic to add the Promotion Message from the cache **/
//					String isin = contractMasterModel.getIsin();
//					if (HazelcastConfig.getInstance().getPromtMsgCache().get(isin + "_" + exch) != null
//							&& HazelcastConfig.getInstance().getPromtMsgCache().get(isin + "_" + exch).size() > 0) {
//						List<PromptEntity> promotion = HazelcastConfig.getInstance().getPromtMsgCache()
//								.get(isin + "_" + exch);
//						if (promotion != null && promotion.size() > 0) {
//							response.setPrompt(promotion);
//						}
//					}
//
//					/** To add alter token details **/
//					if (contractMasterModel != null && (exch.equalsIgnoreCase("NSE") || exch.equalsIgnoreCase("BSE"))
//							&& StringUtil.isNotNullOrEmpty(contractMasterModel.getAlterToken())) {
//						String altExch = exch.equalsIgnoreCase("BSE") ? "NSE" : "BSE";
//						ContractMasterModel alterContractMasterModel = HazelcastConfig.getInstance().getContractMaster()
//								.get(altExch + "_" + contractMasterModel.getAlterToken());
//						if (alterContractMasterModel != null) {
//							ContractInfoDetails altDetails = prepareContractInfoResp(alterContractMasterModel);
//							detailsList.add(altDetails);
//						}
//						/** Logic to add the Promotion Message from the cache **/
//						String alterIsin = alterContractMasterModel.getIsin();
//						String alterExch = alterContractMasterModel.getExch();
//						if (HazelcastConfig.getInstance().getPromtMsgCache().get(alterIsin + "_" + alterExch) != null
//								&& HazelcastConfig.getInstance().getPromtMsgCache().get(alterIsin + "_" + alterExch)
//										.size() > 0) {
//							List<PromptEntity> promotion = HazelcastConfig.getInstance().getPromtMsgCache()
//									.get(alterIsin + "_" + alterExch);
//							if (promotion != null && promotion.size() > 0) {
//								response.setPrompt(promotion);
//							}
//						}
//					}
//					response.setFreezeQty(contractMasterModel.getFreezQty());
//					response.setIsin(contractMasterModel.getIsin());
//					response.setScrips(detailsList);
//					return prepareResponse.prepareSuccessResponseObject(response);
//				} else {
//					return prepareResponse.prepareFailedResponse(AppConstants.TOKEN_NOT_EXISTS);
//				}
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
//			}
//		} catch (Exception e) {
//			Log.error(e);
//		}
//
//		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//	}

	/**
	 * Method to get contract info
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param model
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getContractInfo(GetContractInfoReqModel model) {
		try {
			ContractInfoRespModel response = new ContractInfoRespModel();
			List<ContractInfoDetails> detailsList = new ArrayList<>();
			if (model != null && StringUtil.isNotNullOrEmpty(model.getToken())
					&& StringUtil.isNotNullOrEmpty(model.getExch())) {
				String token = model.getToken();
				String exch = model.getExch().toUpperCase();
				ContractMasterModel contractMasterModel = HazelcastConfig.getInstance().getContractMaster()
						.get(exch + "_" + token);
				if (ObjectUtils.isNotEmpty(contractMasterModel)) {
					ContractInfoDetails details = prepareContractInfoResp(contractMasterModel);
					detailsList.add(details);

					/** To add alter token details **/
					if (contractMasterModel != null && (exch.equalsIgnoreCase("NSE") || exch.equalsIgnoreCase("BSE"))
							&& StringUtil.isNotNullOrEmpty(contractMasterModel.getAlterToken())) {
						String altExch = exch.equalsIgnoreCase("BSE") ? "NSE" : "BSE";

						ContractMasterModel alterContractMasterModel = HazelcastConfig.getInstance().getContractMaster()
								.get(altExch + "_" + contractMasterModel.getAlterToken());
						if (alterContractMasterModel != null) {
							ContractInfoDetails altDetails = prepareContractInfoResp(alterContractMasterModel);
							detailsList.add(altDetails);
						}
					}
					response.setFreezeQty(contractMasterModel.getFreezQty());
					response.setIsin(contractMasterModel.getIsin());
					response.setScrips(detailsList);
					return prepareResponse.prepareSuccessResponseObject(response);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.TOKEN_NOT_EXISTS);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load prompt details
	 * 
	 * @author SOWMIYA
	 */
	public RestResponse<GenericResponse> loadAsmGsm() {
		List<PromptEntity> promptEntity = new ArrayList<>();
		try {
			promptEntity = asmGsmRepo.findAll();
			if (promptEntity.size() > 0 && promptEntity != null) {
				HazelcastConfig.getInstance().getPromtMsgCache().clear();
				for (PromptEntity entity : promptEntity) {
					String isin = entity.getIsin();
					String exch = entity.getExch();
					String key = (isin + "_" + exch).toUpperCase();
					PromptEntity prompt = new PromptEntity();
					prompt.setIsin(entity.getIsin());
					prompt.setExch(entity.getExch());
					prompt.setCompanyName(entity.getCompanyName());
					prompt.setMsg(entity.getMsg());
					prompt.setType(entity.getType());
					prompt.setSeverity(entity.getSeverity());
					promptEntity = HazelcastConfig.getInstance().getPromtMsgCache().get(key);
					if (promptEntity != null && promptEntity.size() > 0) {
						promptEntity = HazelcastConfig.getInstance().getPromtMsgCache().get(key);
						promptEntity.add(prompt);
						HazelcastConfig.getInstance().getPromtMsgCache().put(key, promptEntity);
					} else {
						promptEntity = new ArrayList<>();
						promptEntity.add(prompt);
						HazelcastConfig.getInstance().getPromtMsgCache().put(key, promptEntity);
					}
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

//	/**
//	 * Method to prepare response for get contract info
//	 * 
//	 * @param tempDTO
//	 * @author dinesh
//	 * @return
//	 */
//	private ContractInfoDetails prepareContractInfoResp(ContractMasterModel model) {
//		ContractInfoDetails details = new ContractInfoDetails();
//		details.setExchange(model.getExch());
//		details.setLotSize(model.getLotSize());
//		details.setTickSize(model.getTickSize());
//		details.setToken(model.getToken());
//		details.setTradingSymbol(model.getTradingSymbol());
//		details.setSymbol(model.getSymbol());
//		details.setFormattedInsName(model.getFormattedInsName().toUpperCase());
//		details.setPdc(model.getPdc());
//		details.setInsType(model.getInsType());
//		details.setExpiry(model.getExpiry());
//		return details;
//	}

	/**
	 * Method to prepare response for get contract info
	 * 
	 * @param tempDTO
	 * @author dinesh
	 * @return
	 */
	private ContractInfoDetails prepareContractInfoResp(ContractMasterModel model) {
		ContractInfoDetails details = new ContractInfoDetails();

		/** To add prompt message **/
		if (model != null && (model.getExch().equalsIgnoreCase("NSE") || model.getExch().equalsIgnoreCase("BSE"))) {
			if (HazelcastConfig.getInstance().getPromptMaster().get(model.getIsin() + "_" + model.getExch()) != null
					&& HazelcastConfig.getInstance().getPromptMaster().get(model.getIsin() + "_" + model.getExch())
							.size() > 0) {
				List<PromptModel> prompt = HazelcastConfig.getInstance().getPromptMaster()
						.get(model.getIsin() + "_" + model.getExch());
				if (prompt != null && prompt.size() > 0) {
					details.setPrompt(prompt);
				}
			}
		}
		details.setExchange(model.getExch());
		details.setLotSize(model.getLotSize());
		details.setTickSize(model.getTickSize());
		details.setToken(model.getToken());
		details.setTradingSymbol(model.getTradingSymbol());
		details.setSymbol(model.getSymbol());
		details.setFormattedInsName(model.getFormattedInsName());
		details.setPdc(model.getPdc());
		if (model.getExch().equalsIgnoreCase(AppConstants.NSE) || model.getExch().equalsIgnoreCase(AppConstants.BSE)) {
			String key = model.getExch() + "_" + model.getToken();
			System.out.println("key -- " + key);
			FiftytwoWeekDataEntity fiftytwoWeekData = HazelcastConfig.getInstance().getFiftytwoWeekData().get(key);
			System.out.println("fiftytwoWeekData -- " + fiftytwoWeekData);
			if (fiftytwoWeekData != null) {
				details.setFiftyWeekHigh(fiftytwoWeekData.getHigh());
				details.setFiftyWeeklow(fiftytwoWeekData.getLow());
			}
		}

		String key = model.getExch() + "_" + model.getToken();
		AdminPreferenceModel adminPreferenceModel = HazelcastConfig.getInstance().getAdminPreferenceModel().get("mtf");
		if (adminPreferenceModel != null) {
			if (adminPreferenceModel.getAdminValue() == 1) {
				MtfDataModel mtf = HazelcastConfig.getInstance().getMtfDataModel().get(key);
				if (mtf != null) {
					details.setMtf(true);
				} else {
					details.setMtf(false);
				}
			} else {
				details.setMtf(false);
			}
		} else {
			MtfDataModel mtf = HazelcastConfig.getInstance().getMtfDataModel().get(key);
			if (mtf != null) {
				details.setMtf(true);
			} else {
				details.setMtf(false);
			}
		}

		details.setInsType(model.getInsType());
		details.setExpiry(model.getExpiry());
		return details;
	}

	/**
	 * Method to get security info
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSecurityInfo(SecurityInfoReqModel model, ClinetInfoModel info) {
		try {
			/** Verify session **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			System.out.println("userId -- " + info.getUserId());
			System.out.println("userSession -- " +userSession);
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDEzMDMxMTQ1QjFEODQ2Mjg4QTVFRTJGOEY4MzBCIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODY5MDU5OSwiaWF0IjoxNjk4Njc0ODEwfQ.EGoJi_2xxRayMjfJZ7wZT21DhVSD6yRSvMVKAwtKcqo";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			SecurityInfoRestReq request = prepareSecurityInfoRequest(model);
			if (request == null)
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** connect to odin server **/
			return restService.getSecurityInfo(userSession, request, info);

		} catch (Exception e) {
			Log.error(e);
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to prepare security info request
	 * 
	 * @author SOWMIYA
	 * @param model
	 * @return
	 */
	private SecurityInfoRestReq prepareSecurityInfoRequest(SecurityInfoReqModel model) {
		SecurityInfoRestReq request = new SecurityInfoRestReq();
		try {
			if (model.getExch().equalsIgnoreCase("NSE")) {
				request.setMktSegmentId("1");
			} else if (model.getExch().equalsIgnoreCase("NFO")) {
				request.setMktSegmentId("2");
			} else if (model.getExch().equalsIgnoreCase("BSE")) {
				request.setMktSegmentId("3");
			}
			request.setToken(model.getToken());

		} catch (Exception e) {
			Log.error("prepareSecurityInfoRequest", e);
		}
		return request;
	}
}

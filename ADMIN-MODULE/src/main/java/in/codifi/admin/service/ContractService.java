package in.codifi.admin.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONObject;

import in.codifi.admin.config.HazelcastConfig;
import in.codifi.admin.entity.ContractEntity;
import in.codifi.admin.model.response.ContractMasterRespModel;
import in.codifi.admin.model.response.ContractSymbolRespModel;
import in.codifi.admin.model.response.ExchangeResponseModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.repository.ContractEntityManager;
import in.codifi.admin.repository.ContractRepository;
import in.codifi.admin.req.model.ContractMasterReqModel;
import in.codifi.admin.service.spec.ContractServiceSpec;
import in.codifi.admin.utility.AppConstants;
import in.codifi.admin.utility.PrepareResponse;
import in.codifi.admin.utility.StringUtil;
import in.codifi.cache.model.ContractMasterModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ContractService implements ContractServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ContractEntityManager contractEntityManager;
	@Inject
	ContractRepository contractRepository;

	/**
	 * method to get contract master list
	 * 
	 * @author LOKESH
	 * 
	 */
	@Override
	public RestResponse<GenericResponse> getContractMasterList(ContractMasterReqModel reqModel) {
		try {
			if (!validateContractMasterParams(reqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			String symbol = reqModel.getSymbol();
			if (reqModel.getExch().equalsIgnoreCase("nse_fo") || reqModel.getExch().equalsIgnoreCase("nse_cm")
					|| reqModel.getExch().equalsIgnoreCase("bse_cm") || reqModel.getExch().equalsIgnoreCase("cde_fo")
					|| reqModel.getExch().equalsIgnoreCase("nse_idx")
					|| reqModel.getExch().equalsIgnoreCase("bse_idx")) {
				if (!symbol.isEmpty() || !symbol.equalsIgnoreCase("") && !reqModel.getExch().isEmpty()
						|| reqModel.getExch().equalsIgnoreCase("")) {
					symbol = symbol.replace(" ", "%");
					ContractMasterRespModel contractModel = contractEntityManager.getContractMasterList(
							reqModel.getExch(), reqModel.getExpiry(), reqModel.getGroup(), reqModel.getSymbol());
					if (contractModel != null) {
						return prepareResponse.prepareSuccessResponseObject(contractModel);
					}
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_EXCH);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate contract master params
	 * 
	 * @author LOKESH
	 * @param reqModel
	 * @return
	 */
	private boolean validateContractMasterParams(ContractMasterReqModel reqModel) {
		if (StringUtil.isNotNullOrEmpty(reqModel.getExch()) && StringUtil.isNotNullOrEmpty(reqModel.getSymbol())) {
			return true;
		}
		return false;
	}

	/**
	 * method to get newly added list
	 * 
	 * @author LOKESH
	 * @return
	 */
	public RestResponse<GenericResponse> getNewlyAddedList() {
		List<ContractSymbolRespModel> symbolResponseModel = new ArrayList<>();
		try {
			List<String> exchSeg = contractEntityManager.getDistinctExchSeg();
			if (exchSeg.size() > 0) {
				for (String exchangeSeg : exchSeg) {
					List<String> exchSegDetails = contractEntityManager.getNewlyAddedListSymbol(exchangeSeg);
					if (exchSegDetails.size() > 0) {
						for (String exchSegment : exchSegDetails) {
							String[] a2 = exchSegment.split("-");
							String exSegment = a2[0];
							if (exSegment.equalsIgnoreCase("bcs_fo")) {
								exSegment = "BSE Currency";
							} else if (exSegment.equalsIgnoreCase("bse_com")) {
								exSegment = "BSE Commodities ";
							} else if (exSegment.equalsIgnoreCase("bse_cm")) {
								exSegment = "BSE Cash ";
							} else if (exSegment.equalsIgnoreCase("bse_fo")) {
								exSegment = "BSE FNO";
							} else if (exSegment.equalsIgnoreCase("cde_fo")) {
								exSegment = "NSE Cash";
							} else if (exSegment.equalsIgnoreCase("mcx_fo")) {
								exSegment = "MCX FNO";
							} else if (exSegment.equalsIgnoreCase("nse_cm")) {
								exSegment = "NSE Cash";
							} else if (exSegment.equalsIgnoreCase("nse_com")) {
								exSegment = "NSE Commodities";
							} else if (exSegment.equalsIgnoreCase("nse_fo")) {
								exSegment = "NSE FNO";
							}
							String groupName = a2[1];
							if (groupName.equalsIgnoreCase("null")) {
								groupName = "-";
							}
							String sym = a2[2];
							String insName = a2[3];
							String token = a2[4];
							ContractSymbolRespModel response = new ContractSymbolRespModel();
							response.setExchSegment(exSegment);
							response.setGroupName(groupName);
							response.setInstrumentName(insName);
							response.setSymbol(sym);
							response.setToken(token);
							symbolResponseModel.add(response);
						}
					}
				}

			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}
			return prepareResponse.prepareSuccessResponseObject(symbolResponseModel);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to get deactivaed symbol
	 * 
	 * @author LOKESH
	 * @return
	 */
	public RestResponse<GenericResponse> getDeactivatedList() {
		List<ContractSymbolRespModel> symbolResponseModel = new ArrayList<>();
		try {
			List<String> exchSeg = contractEntityManager.getDistinctExchSeg();
			if (exchSeg.size() > 0) {
				for (String exchangeSeg : exchSeg) {
					List<String> exchSegDetails = contractEntityManager.getDeactivatedSymbol(exchangeSeg);
					if (exchSegDetails.size() > 0) {
						for (String exchSegment : exchSegDetails) {
							String[] a2 = exchSegment.split(",");
							String exSegment = a2[0];
							if (exSegment.equalsIgnoreCase("bcs_fo")) {
								exSegment = "BSE Currency";
							} else if (exSegment.equalsIgnoreCase("bse_com")) {
								exSegment = "BSE Commodities ";
							} else if (exSegment.equalsIgnoreCase("bse_cm")) {
								exSegment = "BSE Cash ";
							} else if (exSegment.equalsIgnoreCase("bse_fo")) {
								exSegment = "BSE FNO";
							} else if (exSegment.equalsIgnoreCase("cde_fo")) {
								exSegment = "NSE Cash";
							} else if (exSegment.equalsIgnoreCase("mcx_fo")) {
								exSegment = "MCX FNO";
							} else if (exSegment.equalsIgnoreCase("nse_cm")) {
								exSegment = "NSE Cash";
							} else if (exSegment.equalsIgnoreCase("nse_com")) {
								exSegment = "NSE Commodities";
							} else if (exSegment.equalsIgnoreCase("nse_fo")) {
								exSegment = "NSE FNO";
							}
							String groupName = a2[1];
							if (groupName.equalsIgnoreCase("null")) {
								groupName = "-";
							}
							String sym = a2[2];
							String insName = a2[3];
							String token = a2[4];
							ContractSymbolRespModel response = new ContractSymbolRespModel();
							response.setExchSegment(exSegment);
							response.setGroupName(groupName);
							response.setInstrumentName(insName);
							response.setSymbol(sym);
							response.setToken(token);
							symbolResponseModel.add(response);
						}
					} else {
						return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
					}
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}

			return prepareResponse.prepareSuccessResponseObject(symbolResponseModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to add the contract into the master details
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addContractMaster(ExchangeResponseModel exchangeModel) {
		if (!validateContractMasterParam(exchangeModel))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);

		boolean isInserted = contractEntityManager.addNewContractInMaster(exchangeModel);
		if (!isInserted) {
			return prepareResponse.prepareSuccessResponseObject(AppConstants.INSERTED);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private boolean validateContractMasterParam(ExchangeResponseModel exchangeModel) {
		if (StringUtil.isNotNullOrEmpty(exchangeModel.getExch())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getExchange_segment())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getSymbol())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getToken())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getInstrument_type())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getExchange_segment())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getStrike_price())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getFormatted_ins_name())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getTrading_symbol())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getCompany_name())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getExpiry_date())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getLot_size())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getTick_size())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getPdc())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getAlter_token())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getFreeze_qty())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getIsin())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getWeek_tag())
				&& StringUtil.isNotNullOrEmpty(exchangeModel.getInstrument_name())) {
			return true;
		}
		return false;
	}

	/**
	 * Method to get the duplicate symbols list from contract master
	 * 
	 * @author LOKESH
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RestResponse<GenericResponse> getDuplicateList() {
		List<JSONObject> result = new ArrayList<JSONObject>();
		List<String> duplicateSymbols = contractEntityManager.getDuplicateSymbols();
		for (int i = 0; i < duplicateSymbols.size(); i++) {
			String a = duplicateSymbols.get(i);
			String[] a2 = a.split("-");
			String duplicateSegmentSymbol = a2[0];
			if (duplicateSegmentSymbol.equalsIgnoreCase("bse_cm")) {
				duplicateSegmentSymbol = "BSE Cash";
			} else if (duplicateSegmentSymbol.equalsIgnoreCase("nse_cm")) {
				duplicateSegmentSymbol = "NSE Cash";
			}
			String duplicateSymbol = a2[1];
			String duplicateSymbolCount = a2[2];
			JSONObject tempJson = new JSONObject();
			tempJson.put("exch_segment", duplicateSegmentSymbol);
			tempJson.put("duplicate_symbols", duplicateSymbol);
			tempJson.put("count", duplicateSymbolCount);
			result.add(tempJson);
		}
		if (result != null && result.size() > 0) {
			return prepareResponse.prepareSuccessResponseObject(result);
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
		}
	}

	/**
	 * Method To load contract master into cache
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> loadContractMaster() {
		try {
			List<ContractEntity> contractList = new ArrayList<>();
			contractList = contractRepository.findAll();
			if (contractList.size() > 0)
				HazelcastConfig.getInstance().getContractMaster().clear();
			for (ContractEntity contractEntity : contractList) {
				ContractMasterModel result = new ContractMasterModel();

				result.setExch(contractEntity.getExch());
				result.setSegment(contractEntity.getSegment());
				result.setSymbol(contractEntity.getSymbol());
				result.setIsin(contractEntity.getIsin());
				result.setFormattedInsName(contractEntity.getFormattedInsName());
				result.setToken(contractEntity.getToken());
				result.setTradingSymbol(contractEntity.getTradingSymbol());
				result.setGroupName(contractEntity.getGroupName());
				result.setInsType(contractEntity.getInsType());
				result.setOptionType(contractEntity.getOptionType());
				result.setStrikePrice(contractEntity.getStrikePrice());
				result.setExpiry(contractEntity.getExpiryDate());
				result.setLotSize(contractEntity.getLotSize());
				result.setTickSize(contractEntity.getTickSize());
				result.setPdc(contractEntity.getPdc());
				result.setWeekTag(contractEntity.getWeekTag());
				result.setFreezQty(contractEntity.getFreezeQty());
				result.setAlterToken(contractEntity.getAlterToken());
				result.setCompanyName(contractEntity.getCompanyName());
				String key = contractEntity.getExch() + "_" + contractEntity.getToken();
				HazelcastConfig.getInstance().getContractMaster().put(key, result);
				System.out.println("Key -- " + key + " -- Result -- " + result);
			}
			System.out.println("Loaded SucessFully");
			System.out.println("Full Size " + HazelcastConfig.getInstance().getContractMaster().size());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e);
			return prepareResponse.prepareFailedResponse(AppConstants.CONTRACT_LOAD_FAILED);
		}
		return prepareResponse.prepareSuccessMessage(AppConstants.CONTRACT_LOAD_SUCESS);
	}
}

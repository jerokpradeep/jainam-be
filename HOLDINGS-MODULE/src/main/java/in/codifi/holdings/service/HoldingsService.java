package in.codifi.holdings.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.config.HazelcastConfig;
import in.codifi.holdings.config.RestServiceProperties;
import in.codifi.holdings.entity.primary.PoaEntity;
import in.codifi.holdings.model.request.HoldingsReqModel;
import in.codifi.holdings.model.response.GenericResponse;
import in.codifi.holdings.repository.PoaRepository;
import in.codifi.holdings.service.spec.IHoldingsService;
import in.codifi.holdings.utility.AppConstants;
import in.codifi.holdings.utility.AppUtil;
import in.codifi.holdings.utility.PrepareResponse;
import in.codifi.holdings.utility.StringUtil;
import in.codifi.holdings.ws.model.RestHoldingsReqModel;
import in.codifi.holdings.ws.service.HoldingsRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class HoldingsService implements IHoldingsService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	HoldingsRestService restService;
	@Inject
	RestServiceProperties props;
	@Inject
	PoaRepository poaRepository;

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Method to get holdings for CNC product
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getHoldings(ClinetInfoModel info) {
		try {
			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFCMEU4Q0QxMDZBMDRCRjhDRTIxQjFFNjAyNDBEIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTEwOTc5OSwiaWF0IjoxNzAxMDYzODM2fQ.7d4jygqeqQy0Z_yuI6CVazVqshNQvDZjp6rjvweSt28";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();
			/** Prepare holding request **/
			HoldingsReqModel reqModel = new HoldingsReqModel();
			reqModel.setProduct(AppConstants.REST_PRODUCT_CNC);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			/** Get holding data from Odin API **/
			return restService.getHoldings(userSession, info.getUserId());
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare holding request
	 * 
	 * @author Gowthaman M
	 * @param reqModel
	 * @param userSession
	 * @return
	 */
	private String prepareHodingRequest(HoldingsReqModel reqModel, String userSession, ClinetInfoModel info) {
		String request = "";
		try {
			RestHoldingsReqModel model = new RestHoldingsReqModel();

			model.setUid(info.getUcc());
			model.setActid(info.getUcc());
			if (reqModel != null && StringUtil.isNotNullOrEmpty(reqModel.getProduct())) {
				model.setPrd(reqModel.getProduct());
			}
//			String json = mapper.writeValueAsString(model);
//			request = AppConstants.JDATA + AppConstants.SYMBOL_EQUAL + json + AppConstants.SYMBOL_AND
//					+ AppConstants.JKEY + AppConstants.SYMBOL_EQUAL + userSession;
		} catch (Exception e) {
			Log.error(e);
		}
		return request;
	}

	/**
	 * Method to update poa status
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getPoa() {
		try {
			List<PoaEntity> poaList = new ArrayList<>();
			poaList = poaRepository.findAll();
			System.out.println("poaList -- " + poaList);
			if (StringUtil.isListNotNullOrEmpty(poaList)) {
				HazelcastConfig.getInstance().getPoaEntity().clear();
				for (PoaEntity poaEntity : poaList) {
					PoaEntity entity = new PoaEntity();
					entity.setId(poaEntity.getId());
					entity.setClDefault(poaEntity.getClDefault());
					entity.setClientCode(poaEntity.getClientCode());
					entity.setDpCode(poaEntity.getDpCode());
					entity.setDpid(poaEntity.getDpid());
					entity.setPoa(poaEntity.getPoa());
					entity.setUserId(poaEntity.getUserId());
					entity.setCreatedBy(poaEntity.getCreatedBy());
					entity.setCreatedon(poaEntity.getCreatedon());
					HazelcastConfig.getInstance().getPoaEntity().put(poaEntity.getUserId(), entity);
				}
				return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to get holdings for MTF product
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> getMTFHoldings(ClinetInfoModel info) {
		try {
			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());

//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDFCMEU4Q0QxMDZBMDRCRjhDRTIxQjFFNjAyNDBEIiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTcwMTEwOTc5OSwiaWF0IjoxNzAxMDYzODM2fQ.7d4jygqeqQy0Z_yuI6CVazVqshNQvDZjp6rjvweSt28";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Prepare holding request **/
			HoldingsReqModel reqModel = new HoldingsReqModel();
			reqModel.setProduct(AppConstants.REST_PRODUCT_MTF);
//			String request = prepareHodingRequest(reqModel, userSession, info);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Get holding data from Odin API **/
			return restService.getHoldings(userSession, info.getUserId());
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to get holdings data by product
	 * 
	 * @author Gowthaman M
	 * @param reqModel
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> getHoldingsByProduct(HoldingsReqModel reqModel, ClinetInfoModel info) {
		try {
			/** To validate request **/
			if (!validateRequest(reqModel))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** Prepare holding request **/
			String request = prepareHodingRequest(reqModel, userSession, info);
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

			/** Get holding data from Odin API **/
			return restService.getHoldings(userSession, reqModel.getProduct());// TODO need to check product
		} catch (Exception e) {
			Log.error(e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * To validate the request
	 * 
	 * @author Gowthaman M
	 * @param reqModel
	 * @return
	 */
	private boolean validateRequest(HoldingsReqModel reqModel) {
		if (StringUtil.isNotNullOrEmpty(reqModel.getProduct())) {
			return true;
		}
		return false;
	}

}

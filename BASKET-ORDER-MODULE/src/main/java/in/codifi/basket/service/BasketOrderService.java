package in.codifi.basket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.basket.entity.primary.BasketNameEntity;
import in.codifi.basket.entity.primary.BasketScripEntity;
import in.codifi.basket.model.request.BasketOrderReq;
import in.codifi.basket.model.request.ExecuteBasketOrderReq;
import in.codifi.basket.model.request.RetrieveBasketModel;
import in.codifi.basket.model.request.ScripRequestModel;
import in.codifi.basket.model.request.SpanMarginReq;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.model.response.SpanMarginResp;
import in.codifi.basket.repository.BasketOrderEntityManager;
import in.codifi.basket.repository.BasketOrderRepository;
import in.codifi.basket.repository.BasketScripRepository;
import in.codifi.basket.service.spec.IBasketOrderService;
import in.codifi.basket.utility.AppConstants;
import in.codifi.basket.utility.AppUtil;
import in.codifi.basket.utility.PrepareResponse;
import in.codifi.basket.utility.StringUtil;
import in.codifi.basket.ws.model.OrderDetails;
import in.codifi.basket.ws.model.SpanMarginPos;
import in.codifi.basket.ws.model.SpanMarginRestReq;
import in.codifi.basket.ws.service.InternalRestService;
import in.codifi.basket.ws.service.SpanMarginRestService;
import in.codifi.cache.CacheService;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class BasketOrderService implements IBasketOrderService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	BasketOrderRepository basketOrderRepository;
	@Inject
	BasketScripRepository basketScripRepository;
	@Inject
	BasketOrderEntityManager basketOrderEntityManager;
	@Inject
	CacheService cacheService;
	@Inject
	InternalRestService internalRestService;
	@Inject
	AppUtil appUtil;
	@Inject
	SpanMarginRestService spanMarginRestService;

	/**
	 * method to get basket scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<BasketScripEntity> getBasketScrips(long basketId) {
		List<BasketScripEntity> basketScripEntity = new ArrayList<>();
		try {
			basketScripEntity = basketScripRepository.findByBasketId(basketId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return basketScripEntity;
	}

	/**
	 * method to Create basket order
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BasketNameEntity createBasket(BasketNameEntity basketOrder) {
		BasketNameEntity saveBasketName = new BasketNameEntity();
		try {
			ExecutorService pool = Executors.newSingleThreadExecutor();
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						basketOrderRepository.save(basketOrder);
					} catch (Exception ex) {
						ex.printStackTrace();
						Log.info(ex.getMessage());
					}
				}
			});
			pool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return saveBasketName;
	}

	/**
	 * method to get basket id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public long getMaxBasketId() {
		long bId = 1;
		try {
			List<BasketNameEntity> basketNameEntity = basketOrderRepository.findAll();
			if (StringUtil.isListNotNullOrEmpty(basketNameEntity)) {
				Optional<BasketNameEntity> getBasketId = basketNameEntity.stream()
						.max(Comparator.comparing(BasketNameEntity::getBasketId));
				BasketNameEntity maxBasketId = getBasketId.get();
				bId = maxBasketId.getBasketId() + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return bId;
	}

	/**
	 * method to get return response
	 * 
	 * @author Gowthaman M
	 * @return
	 */
//	public List<BasketNameEntity> returnResponse(String userId) {
//		List<BasketNameEntity> responseEntity = new ArrayList<>();
//		List<BasketNameEntity> basketEntity = new ArrayList<>();
//		try {
//			List<BasketNameEntity> getCacheData = HazleCacheController.getInstance().getBasketOrderDto().get(userId);
//			for (int i = 0; i < getCacheData.size(); i++) {
//				if (getCacheData.get(i).getBasketScrip() != null) {
//					responseEntity.add(getCacheData.get(i));
//				} else {
//					basketEntity.add(getCacheData.get(i));
//				}
//			}
//			responseEntity = getBasketName(responseEntity);
//			responseEntity.addAll(basketEntity);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.error(e.getMessage());
//		}
//		return responseEntity;
//	}

	/**
	 * method to validate the Basket Id and userId are match
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean getValiedBasket(long basketId, String userId) {
		BasketNameEntity basketNameEntity = new BasketNameEntity();
		try {
			basketNameEntity = basketOrderRepository.findValiedBasketId(basketId, userId);
			if (basketNameEntity != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * Method to basketName is exist or not
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BasketNameEntity getBasketNameAndUserId(String basketName, String userId) {
		BasketNameEntity basketNameEntity = new BasketNameEntity();
		try {
			basketNameEntity = basketOrderRepository.findByBasketName(basketName, userId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
			basketNameEntity = null;
		}
		return basketNameEntity;
	}

	/**
	 * method to Create Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createBasketName(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		try {
			if (StringUtil.isNullOrEmpty(basketOrderReq.getBasketName()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			/** validate basket name is exit or not **/
			if (getBasketNameAndUserId(basketOrderReq.getBasketName(), info.getUserId()) != null)
				return prepareResponse.prepareFailedResponse(AppConstants.BASKET_NAME_ALREADY_EXIST);

			/** Get basketId to insert **/
			BasketNameEntity entity = new BasketNameEntity();
			entity.setBasketId(getMaxBasketId());
			entity.setUserId(info.getUserId());
			entity.setCreatedBy(info.getUserId());
			entity.setBasketName(basketOrderReq.getBasketName());
			/** Method to create basket **/
			BasketNameEntity basketNameEntity = basketOrderRepository.save(entity);

			/** Method to get return latest data **/
			if (basketNameEntity != null) {
				return prepareResponse
						.prepareSuccessResponseObject(basketOrderEntityManager.getBasketReports(info.getUserId()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to Add Scrips to Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addBasketScrip(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		try {
			/** Validate the request **/
			if (!validateAddBasketReq(basketOrderReq))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Method to validate basketId **/
			if (!getValiedBasket(basketOrderReq.getBasketId(), info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			/** Prepare scrip entity **/
			BasketScripEntity basketScripEntity = prepareBasketScripEntity(basketOrderReq, info);
			BasketScripEntity saveBasketScrip = basketScripRepository.saveAndFlush(basketScripEntity);
			if (saveBasketScrip != null)
				/** Return latest data **/
				return prepareResponse.prepareSuccessResponseObject(getBasketScrips(basketOrderReq.getBasketId()));
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate Basket Scrip
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateAddBasketReq(BasketOrderReq basketOrder) {
		try {
			if (basketOrder.getBasketId() > 0 && StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getExchange())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getToken())
//					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getTradingSymbol())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getQty())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPrice())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getProduct())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getTransType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPriceType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getOrderType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getRet())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getSource())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * method to Map Basket Scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BasketScripEntity prepareBasketScripEntity(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		BasketScripEntity basketScripEntity = new BasketScripEntity();
		try {

			ContractMasterModel contractMasterModel = appUtil.getContractInfo(basketOrderReq.getScrips().getExchange(),
					basketOrderReq.getScrips().getToken());
			if (contractMasterModel != null) {
				basketScripEntity.setExchange(contractMasterModel.getExch());
				basketScripEntity.setExpiry(contractMasterModel.getExpiry());
				basketScripEntity.setToken(contractMasterModel.getToken());
				basketScripEntity.setTradingSymbol(contractMasterModel.getTradingSymbol());
				basketScripEntity.setFormattedInsName(contractMasterModel.getFormattedInsName());
				basketScripEntity.setWeekTag(contractMasterModel.getWeekTag());

				basketScripEntity.setBasketId(basketOrderReq.getBasketId());
				basketScripEntity.setCreatedBy(info.getUserId());
				basketScripEntity.setDisClosedQty(basketOrderReq.getScrips().getDisClosedQty());
				basketScripEntity.setMktProtection(basketOrderReq.getScrips().getMktProtection());
				basketScripEntity.setOrderType(basketOrderReq.getScrips().getOrderType());
				basketScripEntity.setPrice(basketOrderReq.getScrips().getPrice());
				basketScripEntity.setPriceType(basketOrderReq.getScrips().getPriceType());
				basketScripEntity.setProduct(basketOrderReq.getScrips().getProduct());
				basketScripEntity.setQty(basketOrderReq.getScrips().getQty());
				basketScripEntity.setRet(basketOrderReq.getScrips().getRet());
				basketScripEntity.setSortOrder(basketOrderReq.getScrips().getSortOrder());
				basketScripEntity.setStopLoss(basketOrderReq.getScrips().getStopLoss());
				basketScripEntity.setTarget(basketOrderReq.getScrips().getTarget());
				basketScripEntity.setTrailingStopLoss(basketOrderReq.getScrips().getTrailingStopLoss());
				basketScripEntity.setTransType(basketOrderReq.getScrips().getTransType());
				basketScripEntity.setTriggerPrice(basketOrderReq.getScrips().getTriggerPrice());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return basketScripEntity;
	}

//		basketScripEntity.setBasketId(basketOrderReq.getBasketId());
//		basketScripEntity.setCreatedBy(info.getUserId());
//		basketScripEntity.setDisClosedQty(basketOrderReq.getScrips().getDisClosedQty());
//		basketScripEntity.setExchange(basketOrderReq.getScrips().getExchange());
//		basketScripEntity.setMktProtection(basketOrderReq.getScrips().getMktProtection());
//		basketScripEntity.setOrderType(basketOrderReq.getScrips().getOrderType());
//		basketScripEntity.setPrice(basketOrderReq.getScrips().getPrice());
//		basketScripEntity.setExpiry(basketOrderReq.getScrips().getExpiry());
//		basketScripEntity.setPriceType(basketOrderReq.getScrips().getPriceType());
//		basketScripEntity.setProduct(basketOrderReq.getScrips().getProduct());
//		basketScripEntity.setQty(basketOrderReq.getScrips().getQty());
//		basketScripEntity.setRet(basketOrderReq.getScrips().getRet());
//		basketScripEntity.setSortOrder(basketOrderReq.getScrips().getSortOrder());
//		basketScripEntity.setStopLoss(basketOrderReq.getScrips().getStopLoss());
//		basketScripEntity.setTarget(basketOrderReq.getScrips().getTarget());
//		basketScripEntity.setToken(basketOrderReq.getScrips().getToken());
//		basketScripEntity.setTradingSymbol(basketOrderReq.getScrips().getTradingSymbol());
//		basketScripEntity.setTrailingStopLoss(basketOrderReq.getScrips().getTrailingStopLoss());
//		basketScripEntity.setTransType(basketOrderReq.getScrips().getTransType());
//		basketScripEntity.setTriggerPrice(basketOrderReq.getScrips().getTriggerPrice());
//		return basketScripEntity;
//	}

	/**
	 * method to Delete Scrips in Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteBasketScrip(BasketOrderReq req, ClinetInfoModel info) {
		try {
			if (StringUtil.isListNullOrEmpty(req.getScripsId()) || req.getBasketId() < 1)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Validate the basket Id **/
			BasketNameEntity basketNameEntity = basketOrderRepository.findByBasketId(req.getBasketId());
			if (basketNameEntity == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			long deleteBasketScrip = basketOrderEntityManager.deleteBasketScrip(req.getBasketId(), req.getScripsId());
			if (deleteBasketScrip <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			/** Return latest data **/
			return prepareResponse.prepareSuccessResponseObject(getBasketScrips(req.getBasketId()));
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to Delete the Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteBasket(int basketId, ClinetInfoModel info) {
		try {
			if (basketId < 1)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Method to validate basketId **/
			if (!getValiedBasket(basketId, info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			long deleteBasketOrder = basketOrderRepository.deleteByBasketId(basketId);
			if (deleteBasketOrder <= 0)
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			/** Return latest data **/
			return prepareResponse
					.prepareSuccessResponseObject(basketOrderEntityManager.getBasketReports(info.getUserId()));
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to Rename Basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateBasketName(BasketOrderReq basketOrder, ClinetInfoModel info) {
		try {
			if (!validateUpdateBasketNameReq(basketOrder))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Method to basketName is exist or not **/
			if (getBasketNameAndUserId(basketOrder.getBasketName(), info.getUserId()) != null)
				return prepareResponse.prepareFailedResponse(AppConstants.BASKET_NAME_ALREADY_EXIST);
			int renameBasketName = basketOrderRepository.updateBasketName(basketOrder.getBasketName(), info.getUserId(),
					info.getUserId(), basketOrder.getBasketId());
			if (renameBasketName > 0) {
				/** Return latest data **/
				return prepareResponse
						.prepareSuccessResponseObject(basketOrderEntityManager.getBasketReports(info.getUserId()));
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate Basket Name
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateUpdateBasketNameReq(BasketOrderReq basketOrder) {
		try {
			if (StringUtil.isNotNullOrEmpty(basketOrder.getBasketName()) && basketOrder.getBasketId() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * method to Retrieve Basket Details
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> basketdetails(ClinetInfoModel info) {
		try {
			List<RetrieveBasketModel> scripReport = basketOrderEntityManager.getBasketReports(info.getUserId());
			if (StringUtil.isListNullOrEmpty(scripReport))
				return prepareResponse.prepareSuccessMessage(AppConstants.NO_RECORDS_FOUND);
			return prepareResponse.prepareSuccessResponseObject(scripReport);
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to Retrieve Scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> retrieveScrips(int basketId, ClinetInfoModel info) {
		try {
			if (basketId < 1)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);

			if (!getValiedBasket(basketId, info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);

			List<BasketScripEntity> scrips = basketScripRepository.findByBasketId(basketId);
			if (StringUtil.isListNullOrEmpty(scrips))
				return prepareResponse.prepareSuccessMessage(AppConstants.NO_RECORDS_FOUND);
			return prepareResponse.prepareSuccessResponseObject(scrips);
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to execute basket order
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param executeBasketOrderReq
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<List<GenericResponse>> excuteBasketOrder(ExecuteBasketOrderReq executeBasketOrderReq,
			ClinetInfoModel info) {
		try {
			if (executeBasketOrderReq.getBasketId() < 1 || executeBasketOrderReq.getScrips().size() <= 0)
				return prepareResponse.prepareFailedResponseForList(AppConstants.INVALID_PARAMETER);

			for (ScripRequestModel model : executeBasketOrderReq.getScrips()) {
				if (!validateExecuteBasketReq(model))
					return prepareResponse.prepareFailedResponseForList(AppConstants.INVALID_PARAMETER);
			}
			/** Get access **/
			String token = appUtil.getAccessToken();
//			String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IlMwMDYwMCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYWUxM2M2MWZjNDM1OTY1NiIsIm9jVG9rZW4iOiIweDAxRTM4RUE0QTg4NDdENkQzQjE4NUYyNTQ5MDc1MiIsInVzZXJDb2RlIjoiQUJWSFciLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4NzM3MjE5OSwiaWF0IjoxNjg3MzQ3MjU0fQ.Esw9WgtsukiMxcu3jAs0VwaGBU3sb1sAnO8BkjOo9zM";
			if (StringUtil.isNullOrEmpty(token))
				return prepareResponse.prepareUnauthorizedResponseForList();
			if (!getValiedBasket(executeBasketOrderReq.getBasketId(), info.getUserId()))
				return prepareResponse.prepareFailedResponseForList(AppConstants.INVALID_BASKET);
			List<OrderDetails> request = prepareExecuteOrderReq(executeBasketOrderReq.getScrips());

			if (request == null || request.size() <= 0)
				return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);

			List<GenericResponse> res = internalRestService.executeOrders(request, token);
			System.out.println("Excute Basket res--" + res);
			if (res != null) {
				basketOrderRepository.updateExecutionStatus("1", info.getUserId(), info.getUserId(),
						executeBasketOrderReq.getBasketId());
				return prepareResponse.prepareSuccessMessageForList(AppConstants.BASKET_EXECUTED);
			}

		} catch (ClientWebApplicationException e) {
			e.printStackTrace();
			Log.error(e.getMessage());
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401) {
				return prepareResponse.prepareUnauthorizedResponseForList();
			}
		}
		return prepareResponse.prepareFailedResponseForList(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate execute basket
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateExecuteBasketReq(ScripRequestModel basketOrder) {
		try {
			if (StringUtil.isNotNullOrEmpty(basketOrder.getExchange())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getToken())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getTradingSymbol())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getQty())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getPrice())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getProduct())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getTransType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getPriceType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getOrderType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getRet())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getSource())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * Methos to preapare request to execute basket order
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param scrips
	 * @return
	 */
	private List<OrderDetails> prepareExecuteOrderReq(List<ScripRequestModel> scrips) {
		List<OrderDetails> request = new ArrayList<>();
		for (ScripRequestModel model : scrips) {
			OrderDetails details = new OrderDetails();
			details.setExchange(model.getExchange());
			details.setTradingSymbol(model.getTradingSymbol());
			details.setQty(model.getQty());
			details.setPrice(model.getPrice());
			details.setOrderType(model.getOrderType());
			details.setProduct(model.getProduct());
			details.setPriceType(model.getPriceType());
			details.setTransType(model.getTransType());
			details.setRet(model.getRet());
			details.setTriggerPrice(model.getTriggerPrice());
			details.setDisclosedQty(model.getDisClosedQty());
			details.setMktProtection(model.getMktProtection());
			details.setTarget(model.getTarget());
			details.setStopLoss(model.getStopLoss());
			details.setTrailingStopLoss(model.getTrailingStopLoss());
			details.setSource(model.getSource());
			details.setToken(model.getToken());
			request.add(details);
		}
		return request;

	}

	/**
	 * Method to reset execution status
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param basketOrderReq
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> resetExecutionStatus(int basketId, ClinetInfoModel info) {
		try {

			/** Method to validate basketId **/
			if (!getValiedBasket(basketId, info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			int update = basketOrderRepository.updateExecutionStatus("0", info.getUserId(), info.getUserId(), basketId);
			if (update > 0) {
				/** Return latest data **/
				return prepareResponse
						.prepareSuccessResponseObject(basketOrderEntityManager.getBasketReports(info.getUserId()));
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to update basket scrips
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param basketOrderReq
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateBasketScrip(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		try {
			/** Validate the request **/
			if (!validateUpdateBasketReq(basketOrderReq))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Method to validate basketId **/
			if (!getValiedBasket(basketOrderReq.getBasketId(), info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);

			Optional<BasketScripEntity> optional = basketScripRepository.findById(basketOrderReq.getScrips().getId());
			if (optional == null || optional.isEmpty())
				return prepareResponse.prepareSuccessMessage(AppConstants.INVALID_BASKET);
			BasketScripEntity dbScrips = optional.get();
			prepareBasketScripEntityForUpdate(basketOrderReq, info, dbScrips);
			basketScripRepository.save(dbScrips);
			return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	public boolean validateUpdateBasketReq(BasketOrderReq basketOrder) {
		try {
			if (basketOrder.getBasketId() > 0 && basketOrder.getScrips().getId() > 0
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getExchange())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getToken())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getQty())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPrice())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getProduct())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getTransType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPriceType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getOrderType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getRet())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getSource())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	public BasketScripEntity prepareBasketScripEntityForUpdate(BasketOrderReq basketOrderReq, ClinetInfoModel info,
			BasketScripEntity basketScripEntity) {
		try {

			ContractMasterModel contractMasterModel = appUtil.getContractInfo(basketOrderReq.getScrips().getExchange(),
					basketOrderReq.getScrips().getToken());
			if (contractMasterModel != null) {
				basketScripEntity.setExchange(contractMasterModel.getExch());
				basketScripEntity.setExpiry(contractMasterModel.getExpiry());
				basketScripEntity.setToken(contractMasterModel.getToken());
				basketScripEntity.setTradingSymbol(contractMasterModel.getTradingSymbol());
				basketScripEntity.setFormattedInsName(contractMasterModel.getFormattedInsName());
				basketScripEntity.setWeekTag(contractMasterModel.getWeekTag());

				basketScripEntity.setUpdatedBy(info.getUserId());
				basketScripEntity.setDisClosedQty(basketOrderReq.getScrips().getDisClosedQty());
				basketScripEntity.setMktProtection(basketOrderReq.getScrips().getMktProtection());
				basketScripEntity.setOrderType(basketOrderReq.getScrips().getOrderType());
				basketScripEntity.setPrice(basketOrderReq.getScrips().getPrice());
				basketScripEntity.setPriceType(basketOrderReq.getScrips().getPriceType());
				basketScripEntity.setProduct(basketOrderReq.getScrips().getProduct());
				basketScripEntity.setQty(basketOrderReq.getScrips().getQty());
				basketScripEntity.setRet(basketOrderReq.getScrips().getRet());
				basketScripEntity.setSortOrder(basketOrderReq.getScrips().getSortOrder());
				basketScripEntity.setStopLoss(basketOrderReq.getScrips().getStopLoss());
				basketScripEntity.setTarget(basketOrderReq.getScrips().getTarget());
				basketScripEntity.setTrailingStopLoss(basketOrderReq.getScrips().getTrailingStopLoss());
				basketScripEntity.setTransType(basketOrderReq.getScrips().getTransType());
				basketScripEntity.setTriggerPrice(basketOrderReq.getScrips().getTriggerPrice());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return basketScripEntity;
	}

	/**
	 * Method to get span margin
	 * 
	 * @author SOWMIYA
	 *
	 * @param spanMarginEntity
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getSpanMargin(List<SpanMarginReq> spanMarginReq, ClinetInfoModel info) {
		try {

			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			for (SpanMarginReq model : spanMarginReq) {
				if (!validateSpanMarginReq(model))
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

			return getSpanMargin(spanMarginReq, info.getUserId());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to validate span margin request
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param model
	 * @return
	 */
	private boolean validateSpanMarginReq(SpanMarginReq model) {
		try {
			if (StringUtil.isNotNullOrEmpty(model.getExchange()) && StringUtil.isNotNullOrEmpty(model.getToken())
					&& StringUtil.isNotNullOrEmpty(model.getQty()) && StringUtil.isNotNullOrEmpty(model.getPrice())
					&& StringUtil.isNotNullOrEmpty(model.getTransType())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * 
	 * Method to prepare span margin request
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param spanMarginReq
	 * @param userSession
	 * @return
	 */
	private RestResponse<GenericResponse> getSpanMargin(List<SpanMarginReq> spanMarginReq, String userId) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			double totalSpan = 0;
			List<SpanMarginPos> marginPosList = new ArrayList<>();
			for (SpanMarginReq entity : spanMarginReq) {
				SpanMarginPos restReqModel = new SpanMarginPos();
				String exch = entity.getExchange();
				String token = entity.getToken();
				int qty = Integer.valueOf(entity.getQty());
				double price = Double.valueOf(entity.getPrice());
				String transType = entity.getTransType();
				if (exch.equalsIgnoreCase("NSE") || exch.equalsIgnoreCase("BSE")) {
					double tempPrice = qty * price;
					tempPrice = tempPrice / 5;
					totalSpan = totalSpan + tempPrice;
				} else {
					ContractMasterModel contractMasterModel = appUtil.getContractInfo(exch, token);
					if (contractMasterModel != null) {
						if (exch.equalsIgnoreCase("MCX")) {
							qty = qty * Integer.parseInt(contractMasterModel.getLotSize());
						}
						restReqModel.setPrd("H");
						restReqModel.setExch(exch);
						String exDate = "";
						if (contractMasterModel.getExpiry() != null) {
							SimpleDateFormat formater = new SimpleDateFormat("dd-MMM-yyyy");
							exDate = formater.format(contractMasterModel.getExpiry());
							restReqModel.setExd(exDate.toUpperCase());
						}

						if (StringUtil.isNotNullOrEmpty(contractMasterModel.getStrikePrice())) {
							restReqModel.setStrprc(contractMasterModel.getStrikePrice());
						}

						restReqModel.setInstname(contractMasterModel.getInsType());

						if (contractMasterModel.getInsType().startsWith("OPT")
								|| contractMasterModel.getInsType().endsWith("OPT")) {
							if (contractMasterModel.getOptionType().equalsIgnoreCase("PE")) {
								restReqModel.setOptt("PE");
							} else if (contractMasterModel.getOptionType().equalsIgnoreCase("CE")) {
								restReqModel.setOptt("CE");
							}
						} else {
							restReqModel.setOptt("XX");
						}
						restReqModel.setSymname(contractMasterModel.getSymbol());

						if (transType.equalsIgnoreCase("buy") || transType.equalsIgnoreCase("b")) {
							restReqModel.setBuyqty(String.valueOf(qty));
						} else if (transType.equalsIgnoreCase("sell") || transType.equalsIgnoreCase("s")) {
							restReqModel.setSellqty(String.valueOf(qty));
						}
						marginPosList.add(restReqModel);
					} else {
						Log.info("ContractMasterModel is empty");
					}
				}
			}

			if (StringUtil.isListNotNullOrEmpty(marginPosList)) {
				SpanMarginRestReq spanMarginRestReq = new SpanMarginRestReq();
				spanMarginRestReq.setActid("HI");
				spanMarginRestReq.setPos(marginPosList);
				String request = mapper.writeValueAsString(spanMarginRestReq);
				String requestParam = "jData=" + request;
				if (request != null) {
					String span = spanMarginRestService.getSpanMargin(requestParam);
					if (StringUtil.isNotNullOrEmpty(span) && !span.equalsIgnoreCase(AppConstants.FAILED_STATUS)) {
						totalSpan = totalSpan + Double.valueOf(span);
					}
				}
			}

			SpanMarginResp resp = new SpanMarginResp();
			BigDecimal span = new BigDecimal(totalSpan).setScale(2, RoundingMode.HALF_EVEN);
			resp.setSpan(String.valueOf(span));
			return prepareResponse.prepareSuccessResponseObject(resp);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}
}

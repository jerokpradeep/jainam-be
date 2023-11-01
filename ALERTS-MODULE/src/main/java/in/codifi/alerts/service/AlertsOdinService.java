package in.codifi.alerts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.config.HazelcastConfig;
import in.codifi.alerts.entity.primary.AlertsEntity;
import in.codifi.alerts.model.request.RequestModel;
import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.repository.AlertsRepository;
import in.codifi.alerts.service.spec.AlertsOdinServiceSpec;
import in.codifi.alerts.utility.AppConstants;
import in.codifi.alerts.utility.AppUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.model.ConditionRespModel;
import in.codifi.alerts.ws.model.DataRespModel;
import in.codifi.alerts.ws.model.ModifyAlertsReqModel;
import in.codifi.alerts.ws.model.Operand1Model;
import in.codifi.alerts.ws.model.Operand2Model;
import in.codifi.alerts.ws.service.AlertsOdinRestService;
import in.codifi.cache.model.ClientInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AlertsOdinService implements AlertsOdinServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AlertsOdinRestService restService;
	@Inject
	AlertsRepository alertsRepository;

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAlerts(ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDEyOEYyNUNFMDkzQjRGNjZDQkZFNTk4REMyMEU1IiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODc3Njk5OSwiaWF0IjoxNjk4NzM5NjQzfQ.HBTZnlT7UjTlzCht-fzv8gQCl_r_lZpiVjNsDywgQG0";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		restService.getAlerts(userSession, info);
		List<AlertsEntity> getAlert = alertsRepository.getAlertDetails(info.getUserId());
		if (StringUtil.isListNullOrEmpty(getAlert))
			return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA);
		return prepareResponse.prepareSuccessResponseObject(getAlert);
	}

	/**
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createAlerts(RequestModel req, ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjQxOSIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IkozMyIsInRlbXBsYXRlSWQiOiJVQVQiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDEyOEYyNUNFMDkzQjRGNjZDQkZFNTk4REMyMEU1IiwidXNlckNvZGUiOiJOWlNQSSIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiNDE5IiwiU3ViVGVuYW50SWQiOiIiLCJQcm9kdWN0U291cmNlIjoiV0FWRUFQSSIsImV4cCI6MTgyMDgzMTI4MCwiaWF0IjoxNjkxMjMxMjkzfSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5ODc3Njk5OSwiaWF0IjoxNjk4NzM5NjQzfQ.HBTZnlT7UjTlzCht-fzv8gQCl_r_lZpiVjNsDywgQG0";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();

		if (!validateCreateAlert(req))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		/***/
		AlertsReqModel createAlertsReq = prepareAlertsReqModel(req, info);
		RestResponse<GenericResponse> alert = restService.createAlerts(createAlertsReq, userSession, info);
		String alertId = alert.getEntity().getMessage();
		AlertsEntity alertsEntity = prepareCreateAlert(req, info, alertId);
		AlertsEntity createAlert = alertsRepository.save(alertsEntity);
		if (createAlert == null)
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);
	}

	/**
	 * method to validate create alert
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateCreateAlert(RequestModel reqModel) {
		try {
			if (StringUtil.isNotNullOrEmpty(reqModel.getAlertName())
					&& StringUtil.isNotNullOrEmpty(reqModel.getAlertType())
					&& StringUtil.isNotNullOrEmpty(reqModel.getExch())
					&& StringUtil.isNotNullOrEmpty(reqModel.getOperator())
					&& StringUtil.isNotNullOrEmpty(reqModel.getScripName())
					&& StringUtil.isNotNullOrEmpty(reqModel.getToken()) && reqModel.getValue() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * method to create Alert
	 * 
	 * @author Gowthaman M
	 * @param alertId
	 * @return
	 */
	public AlertsEntity prepareCreateAlert(RequestModel reqModel, ClientInfoModel info, String alertId) {
		AlertsEntity alertsEntity = new AlertsEntity();
		try {
			String req = "";
			if (reqModel.getOperator().equalsIgnoreCase(">")) {
				req = "greater";
			} else if (reqModel.getOperator().equalsIgnoreCase(">= or <=")) {
				req = "equal";
			} else if (reqModel.getOperator().equalsIgnoreCase("<")) {
				req = "lesser";
			} else if (reqModel.getOperator().equalsIgnoreCase(">=")) {
				req = "greaterequal";
			} else if (reqModel.getOperator().equalsIgnoreCase("<=")) {
				req = "lesserequal";
			} else {
				req = "greater";
			}
			alertsEntity.setAlertId(alertId);
			alertsEntity.setAlertName(reqModel.getAlertName());
			alertsEntity.setAlertType(reqModel.getAlertType());
			alertsEntity.setExch(reqModel.getExch());
			alertsEntity.setExchSeg(reqModel.getExchSeg());
			alertsEntity.setExpiry(reqModel.getExpiry());
			alertsEntity.setOperator(req);
			alertsEntity.setScripName(reqModel.getScripName());
			alertsEntity.setToken(reqModel.getToken());
			alertsEntity.setTriggeredTime(reqModel.getTriggeredTime());
			alertsEntity.setTriggerStatus(reqModel.getTriggerStatus());
			alertsEntity.setUserId(info.getUserId());
			alertsEntity.setValue(reqModel.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return alertsEntity;
	}

	/**
	 * Method to prepare Alerts Request Model
	 * 
	 * @author Gowthaman
	 * @param req
	 * @created on 27-Sep-2023
	 * @return
	 */
	public AlertsReqModel prepareAlertsReqModel(RequestModel req, ClientInfoModel info) {
		AlertsReqModel response = new AlertsReqModel();
		List<ConditionRespModel> conditionRespModelsList = new ArrayList<>();
		ConditionRespModel conditionRespModel = new ConditionRespModel();
		Operand1Model operand1Model = new Operand1Model();
		Operand2Model operand2Model = new Operand2Model();
		DataRespModel dataRespModel = new DataRespModel();
		
		String token = req.getToken();
		String exch = req.getExch().toUpperCase();
		ContractMasterModel contractMasterModel = HazelcastConfig.getInstance().getContractMaster()
				.get(exch + "_" + token);
		
		try {
			operand1Model.setField("LastTradedPrice");
			if (req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
				operand1Model.setMarketsegment(1);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.BSE)) {
				operand1Model.setMarketsegment(8);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.NFO)) {
				operand1Model.setMarketsegment(2);
			}
			operand1Model.setToken(Integer.parseInt(req.getToken()));

			operand2Model.setField("USE_VALUE");
			if (req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
				operand2Model.setMarketsegment(1);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.BSE)) {
				operand2Model.setMarketsegment(8);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.NFO)) {
				operand2Model.setMarketsegment(2);
			}
			operand2Model.setToken(Integer.parseInt(req.getToken()));
			operand2Model.setValue((int) req.getValue());

			conditionRespModel.setOperand1(operand1Model);
			conditionRespModel.setOperand2(operand2Model);
			conditionRespModel.setOperator(req.getOperator());

			conditionRespModelsList.add(conditionRespModel);

			dataRespModel.setAlertType(AppConstants.PRICE);
			if(req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
				dataRespModel.setCategory(contractMasterModel.getGroupName());
			} else {
				dataRespModel.setCategory("");
			}
			dataRespModel.setRemarks(req.getAlertName());
			dataRespModel.setSymbol(contractMasterModel.getSymbol());
			dataRespModel.setTenantid(AppConstants.TENANT_ID);
			dataRespModel.setUserid(info.getUserId());

			response.setCondition(conditionRespModelsList);
			response.setData(dataRespModel);
			response.setCreateDate(req.getTriggeredTime());
			response.setExpiration(-1);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("prepareAlertsReqModel -- " + e.getMessage());
		}
		return response;
	}

	/**
	 * Method to update Alerts
	 * 
	 * @author Gowthaman
	 * @created on 18-Sep-2023
	 * @param req
	 * @param info
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> updateAlerts(RequestModel req, ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjEyMzMzNiIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiIiwib2NUb2tlbiI6IjB4MDE2NTQzREFFRTY2Nzk0Q0NBNkI4MkY1OTAxM0NEIiwidXNlckNvZGUiOiJBRkxJSyIsImdyb3VwQ29kZSI6IkFBQUFBIiwiYXBpa2V5RGF0YSI6eyJDdXN0b21lcklkIjoiMjE0IiwiZXhwIjoxNzc2MTU5NzIwLCJpYXQiOjE2ODk3NTk3Njd9LCJzb3VyY2UiOiJNT0JJTEVBUEkifSwiZXhwIjoxNjk2MzU3Nzk5LCJpYXQiOjE2OTYzMzAxNDF9.h8h9WA-wRqr5Jh0K0Pu-p3DZpfrTC-5_K5_anwmaszk";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		/** Method to validate update alert request **/
		if (!validateUpdateAlert(req))
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		/** To check ID */
		Optional<AlertsEntity> findById = alertsRepository.findById(req.getId());
		if (findById == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_ALERT);
		/** Method to prepare Update Alerts request **/
		ModifyAlertsReqModel updateReq = prepareUpdateAlerts(req, info);
		/** Method to Update Alerts request in odin**/
		RestResponse<GenericResponse> alert = restService.updateAlerts(updateReq, userSession, info);
		String alertId = alert.getEntity().getMessage();
		AlertsEntity alertsEntity = prepareUpdate(req, info.getUserId(), alertId);
		alertsEntity.setUserId(info.getUserId());
		AlertsEntity updateAlertsEntity = alertsRepository.save(alertsEntity);
		if (updateAlertsEntity == null)
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		return prepareResponse.prepareSuccessResponseObject(AppConstants.EMPTY_ARRAY);

	}

	/**
	 * Method to prepare Update Alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @param userId
	 * @param alertId
	 * @return
	 */
	public AlertsEntity prepareUpdate(RequestModel req, String userId, String alertId) {
		AlertsEntity alertEntity = new AlertsEntity();
		try {
			alertEntity.setId(req.getId());
			alertEntity.setAlertName(req.getAlertName());
			alertEntity.setAlertType(req.getAlertType());
			alertEntity.setExch(req.getExch());
			alertEntity.setExchSeg(req.getExchSeg());
			alertEntity.setExpiry(req.getExpiry());
			alertEntity.setAlertId(alertId);
			String request = "";
			if (req.getOperator().equalsIgnoreCase(">")) {
				request = "greater";
			} else if (req.getOperator().equalsIgnoreCase(">= or <=")) {
				request = "equal";
			} else if (req.getOperator().equalsIgnoreCase("<")) {
				request = "lesser";
			} else if (req.getOperator().equalsIgnoreCase(">=")) {
				request = "greaterequal";
			} else if (req.getOperator().equalsIgnoreCase("<=")) {
				request = "lesserequal";
			} else {
				request = "greater";
			}

			alertEntity.setOperator(request);
			alertEntity.setScripName(req.getScripName());
			alertEntity.setToken(req.getToken());
			alertEntity.setTriggeredTime(req.getTriggeredTime());
			alertEntity.setTriggerStatus(req.getTriggerStatus());
			alertEntity.setUserId(userId);
			alertEntity.setValue(req.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("prepare Update -- " + e.getMessage());
		}
		return alertEntity;
	}

	/**
	 * method to validate update Alert
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateUpdateAlert(RequestModel alertsEntity) {
		try {
			if (alertsEntity.getId() > 0 && StringUtil.isNotNullOrEmpty(alertsEntity.getAlertName())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getAlertId())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getAlertType())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getExch())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getOperator())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getScripName())
					&& StringUtil.isNotNullOrEmpty(alertsEntity.getToken()) && alertsEntity.getValue() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * Method to prepare Update Alerts
	 * 
	 * @author Gowthaman
	 * @param req
	 * @param info
	 * @return
	 */
	public ModifyAlertsReqModel prepareUpdateAlerts(RequestModel req, ClientInfoModel info) {
		ModifyAlertsReqModel response = new ModifyAlertsReqModel();
		List<ConditionRespModel> conditionRespModelsList = new ArrayList<>();
		ConditionRespModel conditionRespModel = new ConditionRespModel();
		Operand1Model operand1Model = new Operand1Model();
		Operand2Model operand2Model = new Operand2Model();
		DataRespModel dataRespModel = new DataRespModel();
		try {
			operand1Model.setField("LastTradedPrice");
			if (req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
				operand1Model.setMarketsegment(1);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.BSE)) {
				operand1Model.setMarketsegment(8);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.NFO)) {
				operand1Model.setMarketsegment(2);
			}
			operand1Model.setToken(Integer.parseInt(req.getToken()));

			operand2Model.setField("USE_VALUE");
			if (req.getExch().equalsIgnoreCase(AppConstants.NSE)) {
				operand2Model.setMarketsegment(1);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.BSE)) {
				operand2Model.setMarketsegment(8);
			} else if (req.getExch().equalsIgnoreCase(AppConstants.NFO)) {
				operand2Model.setMarketsegment(2);
			}
			operand2Model.setToken(Integer.parseInt(req.getToken()));
			operand2Model.setValue((int) req.getValue());

			conditionRespModel.setOperand1(operand1Model);
			conditionRespModel.setOperand2(operand2Model);
			conditionRespModel.setOperator(req.getOperator());

			conditionRespModelsList.add(conditionRespModel);

			dataRespModel.setAlertType(null);
			dataRespModel.setCategory(null);
			dataRespModel.setRemarks(null);
			dataRespModel.setSymbol(null);
			dataRespModel.setTenantid(null);
			dataRespModel.setUserid(info.getUserId());

			response.setServerAlertId(req.getAlertId());
			response.setCondition(conditionRespModelsList);
			response.setData(dataRespModel);
			response.setCreateDate(req.getTriggeredTime());
			response.setExpiration(-1);

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("prepare Update Alerts -- " + e.getMessage());
		}
		return response;
	}

	@Override
	public RestResponse<GenericResponse> deleteAlert(String alertId, ClientInfoModel info) {
//		String userSession = AppUtil.getUserSession(info.getUserId());
		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxNkI0QTE0QzkzMDY2QjIyNzFEMjAzNjlEQ0FBNiIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NjYxNjk5OSwiaWF0IjoxNjk2NTc5NTMwfQ.LU5bgvF84OgubuM5gpp9V8wjaTod4sp75VmMXp2ZhHs";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return restService.deleteAlert(alertId, userSession, info);
	}

}

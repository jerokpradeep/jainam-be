package in.codifi.alerts.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.alerts.model.response.GenericResponse;
import in.codifi.alerts.service.spec.AlertsOdinServiceSpec;
import in.codifi.alerts.utility.AppUtil;
import in.codifi.alerts.utility.PrepareResponse;
import in.codifi.alerts.utility.StringUtil;
import in.codifi.alerts.ws.model.AlertsReqModel;
import in.codifi.alerts.ws.model.ModifyAlertsReqModel;
import in.codifi.alerts.ws.service.AlertsOdinRestService;
import in.codifi.cache.model.ClientInfoModel;

@ApplicationScoped
public class AlertsOdinService implements AlertsOdinServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AlertsOdinRestService restService;

	/**
	 * Method to get Alert
	 * 
	 * @author Gowthaman
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getAlerts(ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxRUE2MTMxNjU5NkE1NDYxQkFENERCRTk2QzUyNSIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NTA2MTc5OSwiaWF0IjoxNjk1MDE4MTE5fQ.JuqnvNmnSlLLPHAZQlqzcH8tyfcCNkdl3M6aZlVletM";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return restService.getAlerts(userSession, info);
	}

	/**
	 * Method to create alert
	 * 
	 * @author Gowthaman
	 * @param req
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> createAlerts(AlertsReqModel req, ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxRUE2MTMxNjU5NkE1NDYxQkFENERCRTk2QzUyNSIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NTA2MTc5OSwiaWF0IjoxNjk1MDE4MTE5fQ.JuqnvNmnSlLLPHAZQlqzcH8tyfcCNkdl3M6aZlVletM";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return restService.createAlerts(req, userSession, info);
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
	public RestResponse<GenericResponse> updateAlerts(ModifyAlertsReqModel req, ClientInfoModel info) {
		String userSession = AppUtil.getUserSession(info.getUserId());
//		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxRUE2MTMxNjU5NkE1NDYxQkFENERCRTk2QzUyNSIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NTA2MTc5OSwiaWF0IjoxNjk1MDE4MTE5fQ.JuqnvNmnSlLLPHAZQlqzcH8tyfcCNkdl3M6aZlVletM";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return restService.updateAlerts(req, userSession, info);
	}

	@Override
	public RestResponse<GenericResponse> deleteAlert(String alertId, ClientInfoModel info) {
//		String userSession = AppUtil.getUserSession(info.getUserId());
		String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6OTA5MDkwLCJ1c2VyaWQiOjkwOTA5MCwidGVuYW50aWQiOjkwOTA5MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNzk5NSIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiYjcwZDZmMTM4MWVjNzUyMSIsIm9jVG9rZW4iOiIweDAxRUE2MTMxNjU5NkE1NDYxQkFENERCRTk2QzUyNSIsInVzZXJDb2RlIjoiQUZET0IiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTc3NjE1OTcyMCwiaWF0IjoxNjg5NzU5NzY3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY5NTA2MTc5OSwiaWF0IjoxNjk1MDE4MTE5fQ.JuqnvNmnSlLLPHAZQlqzcH8tyfcCNkdl3M6aZlVletM";
		System.out.println("userSession--" + userSession);
		if (StringUtil.isNullOrEmpty(userSession))
			return prepareResponse.prepareUnauthorizedResponse();
		return restService.deleteAlert(alertId, userSession, info);
	}

}

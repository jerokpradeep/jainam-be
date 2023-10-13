package in.codifi.funds.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.funds.model.response.GenericResponse;
import in.codifi.funds.service.spec.IFundsService;
import in.codifi.funds.utility.AppConstants;
import in.codifi.funds.utility.AppUtil;
import in.codifi.funds.utility.PrepareResponse;
import in.codifi.funds.utility.StringUtil;
import in.codifi.funds.ws.model.LimitRequest;
import in.codifi.funds.ws.service.FundsRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class FundsService implements IFundsService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	FundsRestService restService;

	/**
	 * Method to get limits
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getLimits(ClinetInfoModel info) {
		try {
			/** Get user session from cache **/
			String userSession = AppUtil.getUserSession(info.getUserId());
//			String userSession = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6NzA3MDcwLCJ1c2VyaWQiOjcwNzA3MCwidGVuYW50aWQiOjcwNzA3MCwibWVtYmVySW5mbyI6eyJ0ZW5hbnRJZCI6IjIxNCIsImdyb3VwSWQiOiJITyIsInVzZXJJZCI6IjExNDAxNCIsInRlbXBsYXRlSWQiOiJETlMiLCJ1ZElkIjoiNWQzMzYxNWI0ZWJkNjIzZSIsIm9jVG9rZW4iOiIweDAxNDg1QzcyNzBDNkQyOTE1RDc5OTlCQjkxODAxMCIsInVzZXJDb2RlIjoiQUVYVUUiLCJncm91cENvZGUiOiJBQUFBQSIsImFwaWtleURhdGEiOnsiQ3VzdG9tZXJJZCI6IjIxNCIsImV4cCI6MTY5MTc2MTE0MCwiaWF0IjoxNjYwMjI1MTk3fSwic291cmNlIjoiTU9CSUxFQVBJIn0sImV4cCI6MTY4ODQ5NTM5OSwiaWF0IjoxNjg4NDc4OTE5fQ.vvjDeZ9hTAb-ZzLgQ8lqJRTs9jen9QJnLAjD0EZzhQ4";
			if (StringUtil.isNullOrEmpty(userSession))
				return prepareResponse.prepareUnauthorizedResponse();

			/** prepare request body */
			LimitRequest request = prepareLimitsRequest(info, userSession);
			return restService.getLimits(request, userSession);

		} catch (Exception e) {
			Log.error(e);
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare limits request
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private LimitRequest prepareLimitsRequest(ClinetInfoModel model, String userSession) {
		LimitRequest reqModel = new LimitRequest();
		try {
			reqModel.setUserId(model.getUserId());
			reqModel.setGroupId("HO");
			reqModel.setPeriodicityName("All Exchange Combined");
			reqModel.setProductType(1);
		} catch (Exception e) {
			Log.error(e);
		}
		return reqModel;
	}

}

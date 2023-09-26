package in.codifi.cache;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.ResponsecodeStatusModel;
import in.codifi.common.service.IndicesService;
import in.codifi.common.service.PrepareResponse;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class CacheService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	IndicesService indicesService;

	/**
	 * Method to clear cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> clearCache() {
		HazelcastConfig.getInstance().getIndicesDetails().clear();
		HazelcastConfig.getInstance().getSectors().clear();
		HazelcastConfig.getInstance().getActivityData().clear();
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	/**
	 * Method to load Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> loadCache() {
		indicesService.getIndices();
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	/**
	 * Method to load AppVersion Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public RestResponse<GenericResponse> clearAppVersionCache() {
		HazelcastConfig.getInstance().getVersion().clear();
		return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
	}

	/**
	 * Method to get Error Count
	 * 
	 * @author Gowthaman
	 * @return
	 */
	public RestResponse<GenericResponse> getErrorCount() {
		List<ResponsecodeStatusModel> responseList = new ArrayList<>();
		try {
			responseList = HazelcastConfig.getInstance().getResponsecodeCount().get("errorCount");
			if (StringUtil.isListNotNullOrEmpty(responseList)) {
				return prepareResponse.prepareSuccessResponseObject(responseList);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

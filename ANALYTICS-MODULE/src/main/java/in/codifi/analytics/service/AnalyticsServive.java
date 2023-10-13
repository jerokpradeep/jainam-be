package in.codifi.analytics.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.analytics.entity.primary.AnalyticsEntity;
import in.codifi.analytics.model.request.AnalyticsRequest;
import in.codifi.analytics.model.response.GenericResponse;
import in.codifi.analytics.repository.AnalyticsRepository;
import in.codifi.analytics.service.spec.AnalyticsServiveSpec;
import in.codifi.analytics.utility.AppConstants;
import in.codifi.analytics.utility.PrepareResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AnalyticsServive implements AnalyticsServiveSpec {

	@Inject
	AnalyticsRepository analyticsRepository;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * Method to add Research Call
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> addResearchCall(AnalyticsRequest req) {
		try {
			AnalyticsEntity entity = prepareResearchCall(req);
			AnalyticsEntity saveEntity = analyticsRepository.save(entity);
			if (saveEntity != null) {
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}
		} catch (Exception e) {
			Log.error(" - addResearchCall service - ", e);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to prepare Research Call
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public AnalyticsEntity prepareResearchCall(AnalyticsRequest req) {
		AnalyticsEntity entity = new AnalyticsEntity();
		try {

			entity.setCategoryMode(req.getCategoryMode());
			entity.setChannelIds(req.getChannelIds());
			entity.setCompanyDesc(req.getCompanyDesc());
			entity.setEndTime(req.getEndTime());
			entity.setIsHot(req.getIsHot());
			entity.setIsMessageBar(req.getIsMessageBar());
			entity.setIsPopup(req.getIsPopup());
			entity.setMarketSegmentID(req.getMarketSegmentID());
			entity.setMessageCategoryId(req.getMessageCategoryId());
			entity.setMessageTitle(req.getMessageTitle());
			entity.setPrice(req.getPrice());
			entity.setRecoType(req.getRecoType());
			entity.setReportUrl(req.getReportUrl());
			entity.setScripDescription(req.getScripDescription());
			entity.setScripToken(req.getScripToken());
			entity.setSmsVendor(req.getSmsVendor());
			entity.setSquareoffPrice(req.getSquareoffPrice());
			entity.setStoplossPrice(req.getStoplossPrice());
			entity.setSymbol(req.getSymbol());
			entity.setTags(req.getTags());
			entity.setTopicName(req.getTopicName());
			entity.setTopicNo(req.getTopicNo());
			entity.setUniqueID(req.getUniqueID());
			entity.setUserCategoryId(req.getUserCategoryId());
			entity.setUserDetails(req.getUserDetails());
		} catch (Exception e) {
			Log.error("-- prepare Research Call -- ", e);
		}

		return entity;
	}

}

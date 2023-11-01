package in.codifi.admin.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.config.HazelcastConfig;
import in.codifi.admin.entity.AdminIndexEntity;
import in.codifi.admin.entity.AdminPreferenceEntity;
import in.codifi.admin.model.request.IndexRequestModel;
import in.codifi.admin.model.request.PreferenceRequestModel;
import in.codifi.admin.model.response.GenericResponse;
import in.codifi.admin.repository.AdminIndexDAO;
import in.codifi.admin.repository.AdminIndexRepository;
import in.codifi.admin.repository.AdminPreferenceRepository;
import in.codifi.admin.service.spec.AdminProductEnableServiceSpec;
import in.codifi.admin.utility.AppConstants;
import in.codifi.admin.utility.PrepareResponse;
import in.codifi.admin.utility.StringUtil;
import in.codifi.cache.model.AdminIndexModel;
import in.codifi.cache.model.AdminPreferenceModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AdminProductEnableService implements AdminProductEnableServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AdminPreferenceRepository adminPreferenceRepository;
	@Inject
	AdminIndexRepository adminIndexRepository;
	@Inject
	AdminIndexDAO adminIndexDAO;

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> updatePreference(PreferenceRequestModel model) {
		try {
			if (!validateUpdateVersionParam(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			int updated = adminPreferenceRepository.updatePreference(model.getValue());

			if (updated > 0) {
				HazelcastConfig.getInstance().getAdminPreferenceEntity().clear();
				RestResponse<GenericResponse> adminPreference = loadAdminPreference();
				if (adminPreference.getEntity().getMessage().equalsIgnoreCase(AppConstants.SUCCESS_STATUS)) {
					return prepareResponse.prepareSuccessMessage(AppConstants.UPDATED);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load Preference value in Hazelcast
	 * 
	 * @author LOKESH
	 */
	public RestResponse<GenericResponse> loadAdminPreference() {
		try {
			List<AdminPreferenceEntity> getPreferenceValue = adminPreferenceRepository.findAll();
			if (StringUtil.isListNotNullOrEmpty(getPreferenceValue)) {
				HazelcastConfig.getInstance().getAdminPreferenceEntity().clear();
				HazelcastConfig.getInstance().getAdminPreferenceModel().clear();
				for (AdminPreferenceEntity rSet : getPreferenceValue) {
					AdminPreferenceModel result = new AdminPreferenceModel();
					result.setAdminKey(rSet.getAdminKey());
					result.setAdminValue(rSet.getAdminValue());
					result.setSource(rSet.getSource());
					HazelcastConfig.getInstance().getAdminPreferenceModel().put(rSet.getAdminKey(), result);
				}
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);

	}

	/**
	 * method to validate update preference value
	 * 
	 * @author LOKESH
	 * @param entity
	 * @return
	 */
	private boolean validateUpdateVersionParam(PreferenceRequestModel model) {
		if (model.getValue() >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * method to get Preference value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getPreference() {
		try {

			List<AdminPreferenceEntity> entity = HazelcastConfig.getInstance().getAdminPreferenceEntity()
					.get("adminPreference");
			if (entity != null) {
				return prepareResponse.prepareSuccessResponseObject(entity);
			} else {
				List<AdminPreferenceEntity> getPreferenceValue = adminPreferenceRepository.findAll();
				if (StringUtil.isListNotNullOrEmpty(getPreferenceValue)) {
					HazelcastConfig.getInstance().getAdminPreferenceEntity().put("adminPreference", getPreferenceValue);
					return prepareResponse.prepareSuccessResponseObject(getPreferenceValue);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to update index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> updateIndexValue(IndexRequestModel model) {
		try {
			if (!validateUpdateIndexValue(model))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
			int updated = adminIndexRepository.updateIndex(model.getAlterToken(), model.getExch(),
					model.getExchangeSegment(), model.getToken());

			if (updated > 0) {
				HazelcastConfig.getInstance().getIndexValue().clear();
				return prepareResponse.prepareSuccessMessage(AppConstants.UPDATED);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	private boolean validateUpdateIndexValue(IndexRequestModel model) {
		if (StringUtil.isNotNullOrEmpty(model.getExch()) && StringUtil.isNotNullOrEmpty(model.getExchangeSegment())
				&& StringUtil.isNotNullOrEmpty(model.getToken())
				&& StringUtil.isNotNullOrEmpty(model.getAlterToken())) {
			return true;
		}
		return false;
	}

	/**
	 * method to get index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> getIndexValue() {
		try {
			List<AdminIndexModel> getIndexValue = HazelcastConfig.getInstance().getIndexValue().get("indxValue");
			if (StringUtil.isListNotNullOrEmpty(getIndexValue)) {
				return prepareResponse.prepareSuccessResponseObject(getIndexValue);
			} else {
				List<AdminIndexEntity> indexValue = adminIndexRepository.findAll();
				if (StringUtil.isListNotNullOrEmpty(indexValue)) {
					List<AdminIndexModel> resultList = new ArrayList<>();
					for (AdminIndexEntity rSet : indexValue) {
						AdminIndexModel result = new AdminIndexModel();
						result.setAlterToken(rSet.getAlterToken());
						result.setExch(rSet.getExch());
						result.setExchangeSegment(rSet.getExchangeSegment());
						result.setToken(rSet.getToken());
						resultList.add(result);
					}
					HazelcastConfig.getInstance().getIndexValue().put("indxValue", resultList);
					return prepareResponse.prepareSuccessResponseObject(resultList);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to truncate index value
	 * 
	 * @author LOKESH
	 */
	@Override
	public RestResponse<GenericResponse> truncateIndexValue() {
		try {
			adminIndexDAO.truncateIndexvalue();
			return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

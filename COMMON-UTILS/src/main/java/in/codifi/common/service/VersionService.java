package in.codifi.common.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.VersionEntity;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.VersionModel;
import in.codifi.common.repository.VersionRepository;
import in.codifi.common.service.spec.VersionServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class VersionService implements VersionServiceSpec {

	@Inject
	VersionRepository versionRepository;
	@Inject
	PrepareResponse prepareResponse;

	/**
	 * method to verify version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> findVersion(VersionEntity versionEntity) {
		if (StringUtil.isNotNullOrEmpty(versionEntity.getVersion())
				&& StringUtil.isNotNullOrEmpty(versionEntity.getOs())) {
			try {
				List<VersionEntity> version = new ArrayList<>();
				VersionModel versionModel = new VersionModel();
				if (HazelcastConfig.getInstance().getVersion().containsKey(AppConstants.HAZEL_KEY_VERSION)) {
					version = HazelcastConfig.getInstance().getVersion().get(AppConstants.HAZEL_KEY_VERSION);
				} else {
					version = loadVersionData();
				}
				// updateRequired = 2 -> Mandatory update required
				int updateRequired = 2;
				if (StringUtil.isListNotNullOrEmpty(version)) {
					for (VersionEntity iterEntity : version) {
						if (iterEntity.getVersion().equalsIgnoreCase(versionEntity.getVersion())
								&& iterEntity.getOs().equalsIgnoreCase(versionEntity.getOs())) {
							versionModel.setIsUpdateAvailable(iterEntity.getIsUpdateAvailable());
							return prepareResponse.prepareSuccessResponseObject(versionModel);
						}
					}
					versionModel.setIsUpdateAvailable(updateRequired);
					return prepareResponse.prepareSuccessResponseObject(versionModel);
				} else {
					versionModel.setIsUpdateAvailable(updateRequired);
					return prepareResponse.prepareSuccessResponseObject(versionModel);
				}
			} catch (Exception e) {
				Log.error(e);

			}
		} else {
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to load version data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public List<VersionEntity> loadVersionData() {
		List<VersionEntity> version = new ArrayList<>();
		try {
			version = versionRepository.findAll();
			if (StringUtil.isListNotNullOrEmpty(version)) {
				HazelcastConfig.getInstance().getVersion().clear();
				HazelcastConfig.getInstance().getVersion().put(AppConstants.HAZEL_KEY_VERSION, version);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return version;
	}

	/**
	 * Method to update version
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> updateVersion(VersionEntity versionEntity) {
		String os = "";
		try {
			if (StringUtil.isNullOrEmpty(versionEntity.getVersion()) || StringUtil.isNullOrEmpty(versionEntity.getOs()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETERS);

			if (versionEntity.getOs().equalsIgnoreCase("ANDROID")) {
				os = versionEntity.getOs();
			} else if (versionEntity.getOs().equalsIgnoreCase("IOS")) {
				os = versionEntity.getOs();
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_OS);
			}

			int versionUpdateStatus = versionRepository.updateVersion(versionEntity.getVersion(),
					versionEntity.getOs());
			if (versionUpdateStatus > 0) {
				loadVersionData();
				return prepareResponse.prepareSuccessMessage(AppConstants.VERSION_UPDATED);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INTERNAL_ERROR);
			}

		} catch (Exception e) {
			Log.error("updateVersion -- " + e.getMessage());
			e.printStackTrace();
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}

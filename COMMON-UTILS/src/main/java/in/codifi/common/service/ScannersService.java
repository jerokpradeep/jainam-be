package in.codifi.common.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.config.RestServiceProperties;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.model.response.ScannersRespModel;
import in.codifi.common.repository.AnnoucementsDataRepository;
import in.codifi.common.repository.ContractEntityManger;
import in.codifi.common.service.spec.ScannersServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import in.codifi.common.ws.service.AnalysisRestService;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ScannersService implements ScannersServiceSpec {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AnalysisRestService analysisRestService;
	@Inject
	RestServiceProperties props;
	@Inject
	AnnoucementsDataRepository annoucementsDataRepository;
	@Inject
	ContractEntityManger contractEntityManger;

	/**
	 * Method to get Scanners
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScannersDetails() {
		List<AnalysisRespModel> topGainers = getTopGainers();
		List<AnalysisRespModel> topLosers = getTopLosers();
		List<AnalysisRespModel> fiftyTwoWeekHigh = getFiftyTwoWeekHigh();
		List<AnalysisRespModel> fiftyTwoWeekLow = getFiftyTwoWeekLow();
		List<AnalysisRespModel> riders = getRiders();
		List<AnalysisRespModel> draggers = getDraggers();
		List<AnalysisRespModel> topVolume = getTopVolume();
		List<AnalysisRespModel> meanReversion = getMeanReversion();

		ScannersRespModel resp = new ScannersRespModel();
		resp.setTopgainers(topGainers);
		resp.setTopLosers(topLosers);
		resp.setFiftyTwoWeekHigh(fiftyTwoWeekHigh);
		resp.setFiftyTwoWeekLow(fiftyTwoWeekLow);
		resp.setRiders(riders);
		resp.setDraggers(draggers);
		resp.setTopVolume(topVolume);
		resp.setMeanReversion(meanReversion);
		return prepareResponse.prepareSuccessResponseObject(resp);
	}

	/**
	 * Method to get MeanReversion
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getMeanReversion() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getMeanreversion();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisData().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisData().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisData().clear();
					HazelcastConfig.getInstance().getAnalysisData().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get TopVolume
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getTopVolume() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getTopVolume();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisData().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisData().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisData().clear();
					HazelcastConfig.getInstance().getAnalysisData().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get Draggers
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getDraggers() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getDraggers();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisData().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisData().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisData().clear();
					HazelcastConfig.getInstance().getAnalysisData().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get Riders
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getRiders() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getRiders();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisData().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisData().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisData().clear();
					HazelcastConfig.getInstance().getAnalysisData().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get FiftyTwoWeekLow
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getFiftyTwoWeekLow() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getFiftyTwoWeekLow();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().clear();
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get FiftyTwoWeekHigh
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getFiftyTwoWeekHigh() {
		List<AnalysisRespModel> response = new ArrayList<>();
		String url = props.getFiftyTwoWeekHigh();
		try {
			long lastGenTime = 0;
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().get(url) != null) {
				response = HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().get(url);
				return (response);
			} else {
				response = analysisRestService.getFundamentalAnalysisData(url);
				if (response != null && response.size() > 0) {
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().clear();
					HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().put(url, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return (response);
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * Method to get TopLosers
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getTopLosers() {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			String topLoser = props.getTopGainersUrl() + "_" + "Bearish";
			String url = props.getTopGainersUrl();
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(topLoser) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysistopLosers().get(topLoser) != null) {
				response = HazelcastConfig.getInstance().getAnalysistopLosers().get(topLoser);
				return response;
			} else {

				List<AnalysisRespModel> result = analysisRestService.getFundamentalAnalysisData(url);
				if (result != null && result.size() > 0) {
					for (AnalysisRespModel model : result) {
						String direction = model.getDirection();
						if (StringUtil.isNotNullOrEmpty(direction) && direction.equalsIgnoreCase("Bearish")) {
							response.add(model);
						}
					}
					HazelcastConfig.getInstance().getAnalysistopLosers().clear();
					HazelcastConfig.getInstance().getAnalysistopLosers().put(topLoser, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return response;
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}

	/**
	 * Method to get TopGainers
	 * 
	 * @author LOKESH
	 * @return
	 */
	private List<AnalysisRespModel> getTopGainers() {
		List<AnalysisRespModel> response = new ArrayList<>();
		try {
			long lastGenTime = 0;
			String topGainerKey = props.getTopGainersUrl() + "_" + "Bullish";
			String url = props.getTopGainersUrl();
			if (HazelcastConfig.getInstance().getAnalysisUpdateTime().get(topGainerKey) != null) {
				lastGenTime = HazelcastConfig.getInstance().getAnalysisUpdateTime().get(url);
			}
			long timeDiff = System.currentTimeMillis() - lastGenTime;
			if (timeDiff < 120000 && HazelcastConfig.getInstance().getAnalysistopGainers().get(topGainerKey) != null) {
				response = HazelcastConfig.getInstance().getAnalysistopGainers().get(url);

				return (response);
			} else {
				List<AnalysisRespModel> result = analysisRestService.getFundamentalAnalysisData(url);
				if (result != null && result.size() > 0) {
					for (AnalysisRespModel model : result) {
						String direction = model.getDirection();
						if (StringUtil.isNotNullOrEmpty(direction) && direction.equalsIgnoreCase("Bullish")) {
							response.add(model);
						}
					}
					HazelcastConfig.getInstance().getAnalysistopGainers().clear();
					HazelcastConfig.getInstance().getAnalysistopGainers().put(topGainerKey, response);
					HazelcastConfig.getInstance().getAnalysisUpdateTime().put(AppConstants.UPDATED_TIME, lastGenTime);
					return response;
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
		return response;
	}
}

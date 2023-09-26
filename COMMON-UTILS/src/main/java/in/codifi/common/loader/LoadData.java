package in.codifi.common.loader;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.service.AnalysisService;
import in.codifi.common.service.IndicesService;
import in.codifi.common.service.ProductMasterService;
import in.codifi.common.ws.service.AnalysisRestService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

public class LoadData {

	@Inject
	ProductMasterService productMasterService;
	@Inject
	IndicesService indicesService;
	@Inject
	AnalysisRestService analysisRestService;
	@Inject
	AnalysisService analysisService;

	/**
	 * Method to load all the start-up enents
	 * 
	 * @author Nesan
	 * @param ev
	 * @throws Exception
	 */
	public void init(@Observes StartupEvent ev) throws Exception {
		Log.info("Started to load all pre-Lodings in common-utils");
		HazelcastConfig.getInstance().getActivityData().clear();
		HazelcastConfig.getInstance().getVersion().clear();
		productMasterService.loadProductMaster();
		indicesService.getIndices();
		analysisRestService.getActivityData();
		analysisRestService.getWorldIndicesData();
		analysisService.loadEventData();
		Log.info("complated all pre-Lodings in common-utils");
	}

}

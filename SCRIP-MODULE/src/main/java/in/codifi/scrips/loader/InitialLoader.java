package in.codifi.scrips.loader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import in.codifi.scrips.config.HazelcastConfig;
import in.codifi.scrips.repository.ContractEntityManager;
import in.codifi.scrips.repository.ScripSearchEntityManager;
import in.codifi.scrips.service.ContractService;
import in.codifi.scrips.service.ScripsService;
import in.codifi.scrips.utility.AppConstants;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@SuppressWarnings("serial")
@ApplicationScoped
public class InitialLoader extends HttpServlet {

	@Inject
	ScripSearchEntityManager scripSearchRepo;
	@Inject
	ContractService contractService;
	@Inject
	ContractEntityManager contractEntityManager;
	@Inject
	ScripsService scripsService;

	public void init(@Observes StartupEvent ev) throws ServletException {
		Log.info("Started scrips Initial Loaders");
		Log.info("Started to Index contract scrips");
		HazelcastConfig.getInstance().getFetchDataFromCache().put(AppConstants.FETCH_DATA_FROM_CACHE, true);
		Log.info("Start to loaded Fiftytwo Week Data successfully");
		contractService.loadFiftytwoWeekData();
		Log.info("Fiftytwo Week Data are loaded successfully");
		scripSearchRepo.loadDistintValue(2);
		scripSearchRepo.loadIndexValue();
		Log.info("Started to loading contract master");
		contractService.reloadContractMasterFile();
		contractService.loadContractMaster();
		contractService.loadMTFData();
		Log.info("All the pre-Lodings are ended");
//		contractEntityManager.deleteBSEContract();
		scripsService.loadAsmGsm();
		Log.info("AsmGsm loaded successfully");

		contractService.loadPromptData();

		Log.info("Ended scrips Initial Loader");

	}

}

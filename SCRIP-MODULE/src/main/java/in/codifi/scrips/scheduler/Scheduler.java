package in.codifi.scrips.scheduler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;

import in.codifi.scrips.repository.ContractEntityManager;
import in.codifi.scrips.repository.ScripSearchEntityManager;
import in.codifi.scrips.service.ContractService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class Scheduler {

	@Inject
	ScripSearchEntityManager entityManager;
	@Inject
	ContractService contractService;
	@Inject
	ContractEntityManager contractEntityManager;

	/**
	 * 
	 * Scheduler to load latest data into cache at morning 6:30 AM (1:00 AM UTC)
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param execution
	 * @throws ServletException
	 */
//	@Scheduled(cron = "0 0 1 * * ?")
//	public void run(ScheduledExecution execution) throws ServletException {
//		Log.info("Scheduler started to clear cache and reload ");
//		HazelcastConfig.getInstance().getFetchDataFromCache().clear();
//		HazelcastConfig.getInstance().getDistinctSymbols().clear();
//		HazelcastConfig.getInstance().getLoadedSearchData().clear();
//		HazelcastConfig.getInstance().getFetchDataFromCache().clear();
//		HazelcastConfig.getInstance().getFetchDataFromCache().put(AppConstants.FETCH_DATA_FROM_CACHE, true);
//		entityManager.loadDistintValue(2);
//		entityManager.loadDistintValue(3);
//		contractService.loadContractMaster();
//		Log.info("Scheduler Completed");
//	}

	/**
	 * Scheduler to Load contract at morning 7:00 AM
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param execution
	 * @throws ServletException
	 */
	@Scheduled(cron = "0 0 7 * * ?")
	public void loadContractMaster(ScheduledExecution execution) throws ServletException {
		Log.info("Scheduler started to Load Contract Master");
		contractService.reloadContractMasterFile();
		contractService.loadContractMaster();
		contractService.loadMTFData();
		Log.info("Started to delete contract master not in NSE, BSE and NSO");
		contractService.deleteBSEContract();
		Log.info("Delete contract master not in NSE, BSE and NSO ended");
		Log.info("Scheduler completed to Load Contract Master");
	}

	/**
	 * Scheduler to Load contract at morning 8:15 AM
	 * 
	 * @author Gowthaman M
	 * @param execution
	 * @throws ServletException
	 */
	@Scheduled(cron = "0 15 7 * * ?")
	public void reLoadContractMaster(ScheduledExecution execution) throws ServletException {
		Log.info("Scheduler started to Load Contract Master");
		contractService.reloadContractMasterFile();
		contractService.loadContractMaster();
		contractService.loadMTFData();
		Log.info("Started to delete contract master not in NSE, BSE and NSO");
		contractService.deleteBSEContract();
		Log.info("Delete contract master not in NSE, BSE and NSO ended");
		Log.info("Scheduler completed to Load Contract Master");
	}

	/**
	 * 
	 * Scheduler to delete contract at morning 6 AM (0:30 AM UTC)
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param execution
	 * @throws ServletException
	 */
	@Scheduled(cron = "0 17 7 * * ?")
	public void removeContract(ScheduledExecution execution) throws ServletException {
		Log.info("Scheduler started to Delete contracts");
		contractService.deleteExpiredContract();
		Log.info("Scheduler completed to Delete contracts");

//		contractEntityManager.deleteBSEContract();
	}

	/**
	 * 
	 * Scheduler to insert index value at morning 7.31 AM
	 * 
	 * @author LOKESH KUMAR
	 *
	 * @param execution
	 * @throws ServletException
	 */
	@Scheduled(cron = "0 31 7 * * ?")
	public void insertIndexValue(ScheduledExecution execution) throws ServletException {
		Log.info("Scheduler started to insert index value ");
		contractService.addIndexValue();
		Log.info("Scheduler completed to insert index value");

//		contractEntityManager.deleteBSEContract();
	}

//	/**
//	 * 
//	 * Scheduler to insert index value at morning 7.31 AM
//	 * 
//	 * @author LOKESH KUMAR
//	 *
//	 * @param execution
//	 * @throws ServletException
//	 */
//	@Scheduled(cron = "0 28 7 * * ?")
//	public void insertGlobalContractMasteArchive(ScheduledExecution execution) throws ServletException {
//		Log.info("Scheduler started to insert index value ");
//		contractService.createArchiveTableForContractMaster();
//		Log.info("Scheduler completed to insert index value");
//
////		contractEntityManager.deleteBSEContract();
//	}
}

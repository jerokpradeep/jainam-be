package in.codifi.common.scheduler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.controller.AnalysisController;
import in.codifi.common.service.AnalysisService;
import in.codifi.common.service.NFOFutureService;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class Scheduler {
	@Inject
	AnalysisService analysisService;
	@Inject
	AnalysisController analysisController;
	@Inject
	NFOFutureService nfoService;

	@Scheduled(every = "3m")
	public void schedule(ScheduledExecution se) {
		
		System.out.println("-------------Common utilds starts to clear 3m cache-----------------");
		HazelcastConfig.getInstance().getAnalysistopGainers().clear();
		HazelcastConfig.getInstance().getAnalysistopLosers().clear();
		HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekHigh().clear();
		HazelcastConfig.getInstance().getAnalysisfiftyTwoWeekLow().clear();
//		HazelcastConfig.getInstance().getActivityData().clear();
		System.out.println("-------------Common utilds End to clear 3m cache-----------------");
		
		System.out.println("-------------Common utilds starts to load 3m cache-----------------");
		analysisService.getTopGainers();
		analysisService.getTopLosers();
		analysisController.get52WeekHigh();
		analysisController.get52WeekLow();
//		nfoService.loadNFOFuture();
		System.out.println("-------------Common utilds End to load 3m cache-----------------");

	}

	/**
	 * Scheduler to clear Cache at morning 1:00 AM
	 * 
	 * @author Gowthaman M
	 *
	 * @param execution
	 * @throws ServletException
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void clearCache(ScheduledExecution execution) throws ServletException {
		System.out.println("Started clear cache in common utils");
		HazelcastConfig.getInstance().getSupportAndResistanceRestResponse().clear();
		System.out.println("Ended clear cache in common utils");
	}
	
	@Scheduled(cron = "0 0 6 * * ?")
	public void clearActivityDataCache(ScheduledExecution execution) throws ServletException {
		System.out.println("Started clear Activity Data cache in common utils");
		HazelcastConfig.getInstance().getActivityData().clear();
		System.out.println("Ended clear Activity Data cache in common utils");
		
		System.out.println("Started load Activity Data cache in common utils");
		analysisService.getActivityData();
		System.out.println("Ended load Activity Data cache in common utils");
	}

}

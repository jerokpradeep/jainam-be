package in.codifi.api.scheduler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.service.MarketWatchService;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class Scheduler {

	@Inject
	MarketWatchService mwService;

	@Scheduled(every = "3m")
	public void schedule(ScheduledExecution se) {
		mwService.getFiftytwoWeekHigh();
		mwService.getFiftytwoWeekLow();
		mwService.getTopGainers();
		mwService.getTopLosers();
		mwService.predefinedAdvanceKey();

	}

}

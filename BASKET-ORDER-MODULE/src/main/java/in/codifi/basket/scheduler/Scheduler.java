package in.codifi.basket.scheduler;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.springframework.scheduling.annotation.Scheduled;

import in.codifi.cache.CacheService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class Scheduler {

	@Inject
	CacheService cacheService;

	/**
	 * 
	 * Scheduler to delete expired contract at morning 6:30 AM (1:00 AM UTC)
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param se
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void run(ScheduledExecution execution) throws ServletException {

		Log.info("Started to delete basket order expiry scrips at -" + new Date());
		/** method to delete expired scrip **/
		cacheService.deleteExpiry();

	}

}

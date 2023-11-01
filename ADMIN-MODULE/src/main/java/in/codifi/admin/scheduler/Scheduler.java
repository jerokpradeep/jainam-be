package in.codifi.admin.scheduler;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;

import in.codifi.admin.service.AdminLogsService;
import in.codifi.admin.service.AdminProductEnableService;
import in.codifi.admin.service.LogsService;
import in.codifi.admin.service.UserService;
import in.codifi.cache.CacheService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class Scheduler {
	@Inject
	LogsService adminService;
	@Inject
	CacheService cacheService;
	@Inject
	UserService userService;
	@Inject
	AdminLogsService adminLogsService;
	@Inject
	AdminProductEnableService adminProductEnableService;

	@Scheduled(cron = "0 30 6 * * ?")
	public void run(ScheduledExecution execution) throws ServletException {

		Log.info("Scheduler started at -" + new Date());
		adminService.checkAccessLogTable();
		adminService.checkRestAccessLogTable();
		cacheService.loadTokenForPosition();
	}

	@Scheduled(cron = "0 10 0 * * ?")
	public void truncate(ScheduledExecution execution) throws ServletException {

		Log.info("truncate started at -" + new Date());
		userService.truncateUserLoggedInDetails();
		Log.info("truncate done successfully ");

	}

	@Scheduled(cron = "0 0 * * * ?")
//	@Scheduled(cron = "0 */2 * * * ?")
	public void insert(ScheduledExecution execution) throws ServletException {

		Log.info("insert url records started at -" + new Date());
		adminLogsService.getUrlBasedRecords1();
		Log.info("insert url records done successfully ");

	}

	@Scheduled(cron = "0 56 23 * * ?")
	public void insertlog(ScheduledExecution execution) throws ServletException {

		Log.info("insert loin records started at -" + new Date());
		adminLogsService.getLoginRecord();
		Log.info("insert loin records done successfully ");

	}

	@Scheduled(cron = "0 28 7 * * ?")
	public void truncateIndexValue(ScheduledExecution execution) throws ServletException {

		Log.info("insert loin records started at -" + new Date());
		adminProductEnableService.truncateIndexValue();
		Log.info("insert loin records done successfully ");

	}
}

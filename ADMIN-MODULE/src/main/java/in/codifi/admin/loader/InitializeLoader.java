package in.codifi.admin.loader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.esotericsoftware.minlog.Log;

import in.codifi.admin.service.AdminProductEnableService;
import in.codifi.admin.service.LogsService;
import in.codifi.cache.CacheService;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
@SuppressWarnings("serial")
public class InitializeLoader extends HttpServlet {
	@Inject
	LogsService adminService;
	@Inject
	CacheService cacheService;
	@Inject
	AdminProductEnableService adminPreferenceService;

	public void init(@Observes StartupEvent ev) throws ServletException {
		Log.error("Admin Initialize Loader Started");
		adminService.checkAccessLogTable();
		adminService.checkRestAccessLogTable();
		cacheService.loadTokenForPosition();
		cacheService.loadTokenForHoldings();
		adminPreferenceService.loadAdminPreference();
		Log.error("Admin Initialize Loader Ended");
	}

}

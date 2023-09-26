package in.codifi.api.loader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;

import in.codifi.api.cache.HazelCacheController;
import in.codifi.api.service.AdvanceMWService;
import in.codifi.api.service.Cacheservice;
import in.codifi.api.service.MarketWatchService;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
@SuppressWarnings("serial")
public class InitialLoader extends HttpServlet {
	@Autowired
	Cacheservice cacheservice;
	@Autowired
	MarketWatchService marketWatchService;
	@Inject
	AdvanceMWService advanceMwService;

	public void init(@Observes StartupEvent ev) throws ServletException {

		System.out.println(" Market watch data pre-Lodings are started");
//		HazelCacheController.getInstance().getMasterPredefinedMwList().clear();
//		HazelCacheController.getInstance().getAdvanceMWListByUserId().clear();
//		HazelCacheController.getInstance().getUserPerferenceModel().clear();

		cacheservice.loadUserMWData();

		cacheservice.loadPreDefinedMWData();

		marketWatchService.getFiftytwoWeekHigh();
		marketWatchService.getFiftytwoWeekLow();
		marketWatchService.getTopGainers();
		marketWatchService.getTopLosers();
		marketWatchService.predefinedAdvanceKey();
		System.out.println("screeners loaded successfully");
		System.out.println(" Market watch data pre-Lodings are ended");

	}
}

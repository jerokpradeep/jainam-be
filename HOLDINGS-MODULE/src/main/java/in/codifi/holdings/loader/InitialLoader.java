package in.codifi.holdings.loader;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;

import in.codifi.holdings.service.HoldingsService;
import io.quarkus.runtime.StartupEvent;

@SuppressWarnings("serial")
public class InitialLoader extends HttpServlet {
	
	@Inject
	HoldingsService holdingsService;

	public void init(@Observes StartupEvent ev) throws Exception {
		System.out.println("Holdings started Initial Loading");
		System.out.println("Holdings started to upload POA");
		holdingsService.getPoa();
		System.out.println("Holdings Ended to upload POA");
		System.out.println("Holdings Ended Initial Loading");

	}

}

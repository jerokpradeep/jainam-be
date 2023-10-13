package in.codifi.orders.loader;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import in.codifi.orders.service.ProductMasterService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@SuppressWarnings("serial")
public class LoadData {

	@Inject
	ProductMasterService productMasterService;

	/**
	 * Method to load all the start-up enents
	 * 
	 * @author Nesan
	 * @param ev
	 * @throws Exception
	 */
	public void init(@Observes StartupEvent ev) throws Exception {
		Log.info("Started to load all pre-Lodings in orders-module");

		productMasterService.loadProductMaster();

		Log.info("complated all pre-Lodings in orders-module");
	}

}

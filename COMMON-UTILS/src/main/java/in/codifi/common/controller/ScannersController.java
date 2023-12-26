package in.codifi.common.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.common.controller.spec.ScannersControllerSpec;
import in.codifi.common.model.response.GenericResponse;
import in.codifi.common.service.spec.ScannersServiceSpec;

@Path("/scanner")
public class ScannersController implements ScannersControllerSpec {
	@Inject
	ScannersServiceSpec scannersServiceSpec;

	/**
	 * Method to get Scanners
	 * 
	 * @author LOKESH
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getScannersDetails() {
		return scannersServiceSpec.getScannersDetails();
	}
}

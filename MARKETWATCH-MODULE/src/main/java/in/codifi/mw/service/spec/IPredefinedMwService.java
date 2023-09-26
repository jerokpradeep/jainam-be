package in.codifi.mw.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.mw.entity.primary.PredefinedMwEntity;
import in.codifi.mw.model.response.GenericResponse;

public interface IPredefinedMwService {

	/**
	 * Method to get all predefined market watch
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getAllPrefedinedMwScrips();

	/**
	 * Method to add the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> addscrip(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to delete the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> deletescrip(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to sort the script
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> sortMwScrips(PredefinedMwEntity predefinedEntity);

}

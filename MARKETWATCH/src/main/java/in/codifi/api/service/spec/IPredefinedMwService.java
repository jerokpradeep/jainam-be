package in.codifi.api.service.spec;

import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.api.entity.primary.PredefinedMwEntity;
import in.codifi.api.model.LatestPreDefinedMWReq;
import in.codifi.api.model.PreDefMwReqModel;
import in.codifi.api.model.ResponseModel;

public interface IPredefinedMwService {

	RestResponse<ResponseModel> getAllPrefedinedMwScrips();

	/**
	 * Method to add the script
	 * 
	 * @author SOWMIYA
	 */
	RestResponse<ResponseModel> addscrip(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to delete the script
	 * 
	 * @author SOWMIYA
	 */
	RestResponse<ResponseModel> deletescrip(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to sort the script
	 * 
	 * @author SOWMIYA
	 */
	RestResponse<ResponseModel> sortMwScrips(PredefinedMwEntity predefinedEntity);

	/**
	 * Method to provide all pre defined MW list
	 * 
	 * @author Dinesh
	 */
	RestResponse<ResponseModel> getPDMwScrips(PreDefMwReqModel pDto);

	/**
	 * Method to get pre defined market watch name
	 * 
	 * @author DINESH KUMAR
	 *
	 * @return
	 */
	RestResponse<ResponseModel> getMwNameList();
	
	/**
	 * Method to update Latest PreDefined MW
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<ResponseModel> updateLatestPreDefinedMW(List<LatestPreDefinedMWReq> req);

}

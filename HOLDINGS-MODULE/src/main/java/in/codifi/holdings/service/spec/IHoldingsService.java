package in.codifi.holdings.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.model.request.HoldingsReqModel;
import in.codifi.holdings.model.response.GenericResponse;

public interface IHoldingsService {

	/**
	 * Method to get CNC Holdings data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getHoldings(ClinetInfoModel info);

	/**
	 * Method to update poa status
	 * 
	 * @author Gowthaman
	 * @return
	 */
	RestResponse<GenericResponse> getPoa();

	/**
	 * 
	 * Method to get holdings data by product
	 * 
	 * @author Gowthaman M
	 * @param reqModel
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getHoldingsByProduct(HoldingsReqModel reqModel, ClinetInfoModel info);

	/**
	 * Method to get holdings for MTF product
	 * 
	 * @author Gowthaman M
	 * @param info
	 * @return
	 */
	RestResponse<GenericResponse> getMTFHoldings(ClinetInfoModel info);

}

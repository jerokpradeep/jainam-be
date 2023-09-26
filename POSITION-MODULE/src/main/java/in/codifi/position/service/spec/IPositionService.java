package in.codifi.position.service.spec;

import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.position.model.request.OrderDetails;
import in.codifi.position.model.request.PositionConversionReq;
import in.codifi.position.model.request.UserSession;
import in.codifi.position.model.response.GenericResponse;

public interface IPositionService {

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getposition(ClinetInfoModel info);

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getDailyPosition(ClinetInfoModel info);

	/**
	 * Method to get the position
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> getExpiryPosition(ClinetInfoModel info);

	/**
	 * Method to get the position conversion
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> positionConversion(PositionConversionReq positionConversionReq, ClinetInfoModel info);

	/**
	 * Method to user session in cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> putUserSession(UserSession session);

	/**
	 * Method to square off
	 * 
	 * @author Nesan
	 * @return
	 */
	RestResponse<GenericResponse> positionSquareOff(List<OrderDetails> orderDetails, ClinetInfoModel info);

}

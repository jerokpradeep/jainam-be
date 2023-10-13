package in.codifi.auth.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.auth.model.request.VendorReqModel;
import in.codifi.auth.model.response.GenericResponse;

public interface SSOServiceSpec {

	/**
	 * Method to authorize Vendor
	 * 
	 * @author Dinesh Kumar
	 * @param vendorReqModel
	 * @return
	 */
	RestResponse<GenericResponse> ssoAuthorizeVendor(VendorReqModel vendorReqModel);

	/**
	 * Method to check Vendor Authorization
	 * 
	 * @author Dinesh Kumar
	 * @param vendorReqModel
	 * @return
	 */
	RestResponse<GenericResponse> checkVendorAuthorization(VendorReqModel vendorReqModel);

	/**
	 * Method to authorize Vendor
	 * 
	 * @author Dinesh Kumar
	 * @param vendorReqModel
	 * @return
	 */
	RestResponse<GenericResponse> getUserDetails(VendorReqModel vendorReqModel);

	/**
	 * Method to check Vendor Authorization
	 * 
	 * @author Dinesh Kumar
	 * @param vendorReqModel
	 * @return
	 */
	RestResponse<GenericResponse> getUserDetailsByAuth(VendorReqModel vendorReqModel);

	/**
	 * Method to get vendor app deatils 
	 * 
	 * @author Dinesh Kumar
	 * @param authReq
	 * @return
	 */
	RestResponse<GenericResponse> getVendorAppDetails(VendorReqModel authReq);
}

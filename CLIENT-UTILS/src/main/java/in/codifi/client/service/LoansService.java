package in.codifi.client.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.DpAccountNum;
import in.codifi.client.entity.primary.ClientBasicDataEntity;
import in.codifi.client.model.response.GenericResponse;
import in.codifi.client.repository.ClientBasicDataRepository;
import in.codifi.client.service.spec.ILoansService;
import in.codifi.client.transformation.Bankdetails;
import in.codifi.client.transformation.ClientDetailsRespModel;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.PrepareResponse;
import in.codifi.client.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class LoansService implements ILoansService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ClientBasicDataRepository clientBasicDataRepository;

	/**
	 * method to get User Profile
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getLoansUserProfile(ClinetInfoModel info) {

		ClientDetailsRespModel response = new ClientDetailsRespModel();
		try {
			ClientBasicDataEntity clientBasicDataEntity = clientBasicDataRepository.getTermcode(info.getUserId());
			if (clientBasicDataEntity != null) {
				response = bindProfileData(clientBasicDataEntity, info.getUserId());
				return prepareResponse.prepareSuccessResponseObject(response);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * Method to bind Profile Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public ClientDetailsRespModel bindProfileData(ClientBasicDataEntity clientBasicData, String userId) {

		ClientDetailsRespModel responseModel = new ClientDetailsRespModel();
		try {

//			in.codifi.auth.ws.model.login.LoginRestResp loginDetails = HazelcastConfig.getInstance()
//					.getUserSessionDetails().get(userId + AppConstants.HAZEL_KEY_USER_DETAILS);

			responseModel.setActId("");
			responseModel.setActStatus("");
			responseModel.setAddress(clientBasicData.getAddress1() + " " + clientBasicData.getAddress2() + " "
					+ clientBasicData.getAddress3());

			List<Bankdetails> bankDetails = new ArrayList<>();
//			for (BracketDetailsModel bankDetail : UserProfileModel.getData().getBank_details()) {
//				Bankdetails detail = new Bankdetails();
//				if (StringUtil.isNotNullOrEmpty(bankDetail.getAccountNo())) {
//					String maskedAcctno = bankDetail.getAccountNo();
//					maskedAcctno = maskedAcctno.replaceAll(".(?=.{4})", "*");
//					detail.setAccNumber(maskedAcctno);
//				}
//				detail.setBankName(bankDetail.getBankName());
//				detail.setIsDefault(bankDetail.getIsDefault());
//				bankDetails.add(detail);
//			}
//			responseModel.setBankdetails(bankDetails);

			responseModel.setBranchId(clientBasicData.getBrCode());
			responseModel.setBrokerName("");
			responseModel.setCity(clientBasicData.getCity());
			responseModel.setPincode(clientBasicData.getPincode());

			responseModel.setClientName(clientBasicData.getNameAsperPan());
			responseModel.setCreatedDate("");
			responseModel.setCreatedTime("");
			List<DpAccountNum> dpAccountNum = new ArrayList<>();
//			for (DpDetailsModel dp : UserProfileModel.getData().getDp_details()) {
//				if (dp.getDefaultDP().equalsIgnoreCase("true")) {
//					DpAccountNum details = new DpAccountNum();
//					details.setDpAccountNumber("");
//					details.setDpId(dp.getDpId());
//					details.setBoId(dp.getClientBeneficiaryId());
//					details.setRepository(dp.getDpType());
//					details.setDpAccountName(dp.getDpName());
//					details.setEdisClientCode(loginDetails.getData().getOthers().getUserCode());
//					dpAccountNum.add(details);
//				}
//			}
			responseModel.setDpAccountNumber(dpAccountNum);

			responseModel.setEmail(clientBasicData.getEmail());
			List<String> exchange = new ArrayList<>();
//			for (String exc : loginDetails.getData().getExchanges()) {
//				if (exc.equalsIgnoreCase("NSE_EQ")) {
//					exchange.add("NSE");
//				} else if (exc.equalsIgnoreCase("NSE_FO")) {
//					exchange.add("NFO");
//				} else if (exc.equalsIgnoreCase("BSE_EQ")) {
//					exchange.add("BSE");
//				}
//			}

			responseModel.setExchange(exchange);

			responseModel.setMandateIdList(null);

			if (StringUtil.isNotNullOrEmpty(clientBasicData.getMobile())) {
				String maskedPhoneNo = clientBasicData.getMobile();
				responseModel.setMobNo(maskedPhoneNo);
			}

			responseModel.setOfficeAddress("");
			responseModel.setOrders(null);
			responseModel.setOrderTypes(null);

			if (StringUtil.isNotNullOrEmpty(clientBasicData.getPangir())) {
				String maskedPanNo = clientBasicData.getPangir();
				maskedPanNo = maskedPanNo.replaceAll(".(?=.{4})", "*");
				responseModel.setPan(maskedPanNo);
			}
			responseModel.setPriceTypes(null);
			responseModel.setProducts(null);

			List<String> productTypes = new ArrayList<>();

//			for (String proTypr : loginDetails.getData().getProductTypes()) {
//				if (proTypr.equalsIgnoreCase("INTRADAY")) {
//					productTypes.add("MIS");
//				} else if (proTypr.equalsIgnoreCase("DELIVERY")) {
//					productTypes.add("CNC");
//				} else if (proTypr.equalsIgnoreCase("COVER")) {
//					productTypes.add("BSE");
//				} else if (proTypr.equalsIgnoreCase("BRACKET")) {
//					productTypes.add("BSE");
//				} else if (proTypr.equalsIgnoreCase("MTF")) {
//					productTypes.add("MTF");
//				} else if (proTypr.equalsIgnoreCase("BTST")) {
//					productTypes.add("NRML");
//				}
//
//			}
			responseModel.setProductTypes(productTypes);
			responseModel.setState("");
			responseModel.setUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());

		}
		return responseModel;

	}

}

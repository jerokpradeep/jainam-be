package in.codifi.client.ws.remodeling;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.DpAccountNum;
import in.codifi.client.config.HazelcastConfig;
import in.codifi.client.transformation.Bankdetails;
import in.codifi.client.transformation.ClientDetailsRespModel;
import in.codifi.client.utility.AppConstants;
import in.codifi.client.utility.AppUtil;
import in.codifi.client.utility.StringUtil;
import in.codifi.client.ws.model.BracketDetailsModel;
import in.codifi.client.ws.model.DpDetailsModel;
import in.codifi.client.ws.model.UserProfileModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ClientDetailsRemodeling {

	@Inject
	AppUtil appUtil;

	/**
	 * Method to load Profile Data Into Cache
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public void loadProfileDataIntoCache(ClientDetailsRespModel respModel) {
		try {
			ClientDetailsModel responseModel = new ClientDetailsModel();
			responseModel.setPan(respModel.getPan());
			responseModel.setEmail(respModel.getEmail());
			responseModel.setMobNo(respModel.getMobNo());
			responseModel.setActId(respModel.getActId());
			responseModel.setClientName(respModel.getClientName());
			responseModel.setActStatus(respModel.getActStatus());
			responseModel.setCreatedDate(respModel.getCreatedDate());
			responseModel.setCreatedTime(respModel.getCreatedTime());
			responseModel.setAddress(respModel.getAddress());
			responseModel.setOfficeAddress(respModel.getOfficeAddress());
			responseModel.setCity(respModel.getCity());
			responseModel.setState(respModel.getState());
			responseModel.setExchange(respModel.getExchange());
			responseModel.setUserId(respModel.getUserId());
			responseModel.setBranchId(respModel.getBranchId());
			responseModel.setBrokerName(respModel.getBrokerName());
			List<DpAccountNum> dpAccountNum = new ArrayList<>();
			for (DpAccountNum dp : respModel.getDpAccountNumber()) {
				DpAccountNum details = new DpAccountNum();
				details.setDpAccountNumber("");
				details.setDpId(dp.getDpId());
				details.setBoId(dp.getBoId());
				details.setRepository(dp.getRepository());
				details.setEdisClientCode(respModel.getDpAccountNumber().get(0).getEdisClientCode());
				dpAccountNum.add(details);
			}
			responseModel.setDpAccountNumber(dpAccountNum);
			HazelcastConfig.getInstance().getClientDetails().remove(respModel.getUserId());
			HazelcastConfig.getInstance().getClientDetails().put(respModel.getUserId(), responseModel);
			System.out.println(responseModel.getMobNo());

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

	/**
	 * Method to bind Profile Data
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public ClientDetailsRespModel bindProfileData(UserProfileModel UserProfileModel, String userId) {

		ClientDetailsRespModel responseModel = new ClientDetailsRespModel();
		try {

			in.codifi.auth.ws.model.login.LoginRestResp loginDetails = HazelcastConfig.getInstance()
					.getUserSessionDetails().get(userId + AppConstants.HAZEL_KEY_USER_DETAILS);

			responseModel.setActId("");
			responseModel.setActStatus("");
			responseModel.setAddress(UserProfileModel.getData().getAddress());

			List<Bankdetails> bankDetails = new ArrayList<>();
			for (BracketDetailsModel bankDetail : UserProfileModel.getData().getBank_details()) {
				Bankdetails detail = new Bankdetails();
				if (StringUtil.isNotNullOrEmpty(bankDetail.getAccountNo())) {
					String maskedAcctno = bankDetail.getAccountNo();
					maskedAcctno = maskedAcctno.replaceAll(".(?=.{4})", "*");
					detail.setAccNumber(maskedAcctno);
				}
				detail.setBankName(bankDetail.getBankName());
				detail.setIsDefault(bankDetail.getIsDefault());
				bankDetails.add(detail);
			}
			responseModel.setBankdetails(bankDetails);

			responseModel.setBranchId("");
			responseModel.setBrokerName("");
			responseModel.setCity("");
			System.out.println("userName--" + loginDetails.getData().getUserName());
			responseModel.setClientName(loginDetails.getData().getUserName());
			responseModel.setCreatedDate("");
			responseModel.setCreatedTime("");
			List<DpAccountNum> dpAccountNum = new ArrayList<>();
			for (DpDetailsModel dp : UserProfileModel.getData().getDp_details()) {
				if (dp.getDefaultDP().equalsIgnoreCase("true")) {
					DpAccountNum details = new DpAccountNum();
					details.setDpAccountNumber("");
					details.setDpId(dp.getDpId());
					details.setBoId(dp.getClientBeneficiaryId());
					details.setRepository(dp.getDpType());
					details.setDpAccountName(dp.getDpName());
					details.setEdisClientCode(loginDetails.getData().getOthers().getUserCode());
					dpAccountNum.add(details);
				}
			}
			responseModel.setDpAccountNumber(dpAccountNum);

			responseModel.setEmail(UserProfileModel.getData().getEmail());
			List<String> exchange = new ArrayList<>();
			System.out.println("Got it 2" + loginDetails.getData().getExchanges().size());
			for (String exc : loginDetails.getData().getExchanges()) {
				if (exc.equalsIgnoreCase("NSE_EQ")) {
					exchange.add("NSE");
				} else if (exc.equalsIgnoreCase("NSE_FO")) {
					exchange.add("NFO");
				} else if (exc.equalsIgnoreCase("BSE_EQ")) {
					exchange.add("BSE");
				}
			}

			responseModel.setExchange(exchange);

			responseModel.setMandateIdList(null);

			if (StringUtil.isNotNullOrEmpty(UserProfileModel.getData().getMobileNo())) {
				String maskedPhoneNo = UserProfileModel.getData().getMobileNo();
//				maskedPhoneNo = maskedPhoneNo.replaceAll(".(?=.{4})", "*");
				responseModel.setMobNo(maskedPhoneNo);
			}

			responseModel.setOfficeAddress("");
			responseModel.setOrders(null);
			responseModel.setOrderTypes(null);

			if (StringUtil.isNotNullOrEmpty(UserProfileModel.getData().getPan())) {
				String maskedPanNo = UserProfileModel.getData().getPan();
				maskedPanNo = maskedPanNo.replaceAll(".(?=.{4})", "*");
				responseModel.setPan(maskedPanNo);
			}
			responseModel.setPriceTypes(null);
			responseModel.setProducts(null);

			List<String> productTypes = new ArrayList<>();

			for (String proTypr : loginDetails.getData().getProductTypes()) {
				if (proTypr.equalsIgnoreCase("INTRADAY")) {
					productTypes.add("MIS");
				} else if (proTypr.equalsIgnoreCase("DELIVERY")) {
					productTypes.add("CNC");
				} else if (proTypr.equalsIgnoreCase("COVER")) {
					productTypes.add("BSE");
				} else if (proTypr.equalsIgnoreCase("BRACKET")) {
					productTypes.add("BSE");
				} else if (proTypr.equalsIgnoreCase("MTF")) {
					productTypes.add("MTF");
				} else if (proTypr.equalsIgnoreCase("BTST")) {
					productTypes.add("NRML");
				}

			}
			responseModel.setProductTypes(productTypes);
			responseModel.setState("");
			responseModel.setUserId(userId);
			loadProfileDataIntoCache(responseModel);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());

		}
		return responseModel;

	}

}

package in.codifi.funds.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {

	@ConfigProperty(name = "appconfig.odin.url.limits")
	private String limitsUrl;

	@ConfigProperty(name = "appconfig.razorpay.key")
	String razorpayKey;

	@ConfigProperty(name = "appconfig.razorpay.secret")
	String razorpaySecret;

	@ConfigProperty(name = "appconfig.backoff.login")
	private String boLoginUrl;

	@ConfigProperty(name = "appconfig.backoff.bank.details")
	private String boBankDetails;

	@ConfigProperty(name = "appconfig.url.backoffice.login")
	private String boPayInLogin;

	@ConfigProperty(name = "appconfig.url.backoffice.checkmargin")
	private String boPayoutCheckMargin;

	@ConfigProperty(name = "appconfig.url.backoffice.payout")
	private String boPayOut;

	@ConfigProperty(name = "appconfig.url.backoffice.payin")
	private String boPayIn;

	@ConfigProperty(name = "appconfig.odin.url.bankDetails")
	private String bankDetails;

	@ConfigProperty(name = "appconfig.odin.url.xapikey")
	private String xApiKey;

	@ConfigProperty(name = "appconfig.odin.ifsc.url.razorpay")
	private String razorpayIfscUrl;

	@ConfigProperty(name = "appconfig.odin.withDrawal.url")
	private String withdrawalDetails;

	@ConfigProperty(name = "appconfig.odin.withDrawalRequest.url")
	private String withdrawalRequest;

	@ConfigProperty(name = "appconfig.odin.transactionList.url")
	private String transactionList;

	@ConfigProperty(name = "appconfig.odin.withDrawalRequestDelete.url")
	private String withdrawalRequestDelete;

}
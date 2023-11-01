package in.codifi.orders.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {

	@ConfigProperty(name = "appconfig.odin.url.xapikey")
	private String xApiKey;

	@ConfigProperty(name = "appconfig.odin.url.accesstoken")
	private String accessToken;

	@ConfigProperty(name = "appconfig.odin.url.placeorder")
	private String placeOrderUrl;

	@ConfigProperty(name = "appconfig.odin.url.modifyorder")
	private String modifyOrderurl;

	@ConfigProperty(name = "appconfig.odin.url.cancelorder")
	private String cancelOrderUrl;

	@ConfigProperty(name = "appconfig.odin.url.orderbook")
	private String orderBookUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.gtdOrderbook")
	private String gtdOrderbookUrl;

	@ConfigProperty(name = "appconfig.odin.url.tradebook")
	private String tradeBookUrl;

	@ConfigProperty(name = "appconfig.odin.url.squareoffpostion")
	private String squareOffPostionUrl;

	@ConfigProperty(name = "appconfig.odin.url.margin")
	private String marginUrl;

	@ConfigProperty(name = "appconfig.odin.url.base")
	private String baseUrl;

	@ConfigProperty(name = "appconfig.odin.url.coverorder")
	private String coverOrderUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.bracketorder")
	private String bracketOrderUrl;

//	@ConfigProperty(name = "appconfig.methods.priority")
//	private String[] priorityMethod;
	
	@ConfigProperty(name = "appconfig.odin.url.modifycoverorder")
	private String modifyCoverOrderurl;
	
	@ConfigProperty(name = "appconfig.odin.url.canclecoverorder")
	private String cancelCoverOrderurl;
	
	@ConfigProperty(name = "appconfig.odin.url.placesiporder")
	private String placeSipOrderUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.siporderbookurl")
	private String sipOrderBookUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.siporderbookrequest")
	private String sipOrderBookRequest;
	
	@ConfigProperty(name = "appconfig.odin.url.canclesiporderurl")
	private String cancleSipOrderUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.orderhistoryurl")
	private String orderHistoryUrl;
	
	@ConfigProperty(name = "appconfig.odin.url.brokeragedetails")
	private String brokerageDetails;
	
	@ConfigProperty(name = "appconfig.odin.url.brokerageandchargespageurl")
	private String brokerageAndChargesPage;
	
	@ConfigProperty(name = "appconfig.odin.url.getordermargininfo")
	private String getOrderMarginInfo;

	@ConfigProperty(name = "appconfig.odin.url.position")
	private String positionUrl;

}

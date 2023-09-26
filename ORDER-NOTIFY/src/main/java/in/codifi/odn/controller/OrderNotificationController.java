package in.codifi.odn.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.odn.controller.spec.OrderNotificationControllerSpec;
import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.WebsocketConnectionResp;
import in.codifi.odn.service.spec.OrderNotificationServiceSpec;
import in.codifi.odn.utility.OrderWebsocketClient;

@Path("order-notification")
public class OrderNotificationController implements OrderNotificationControllerSpec {

	@Inject
	OrderWebsocketClient weClient;

	@Inject
	OrderNotificationServiceSpec orderNotificationServiceSpec;

	@Override
	public void connectWebsocketInfo() {
		weClient.connectWebsocketInfo();
	}

	@Override
	public void sendPushNotification() {
		WebsocketConnectionResp orderFeedMode = WebsocketConnectionResp.builder()
				.symbol("TEST").orderStatus(1)
				.ucc("117995").build();
		this.orderNotificationServiceSpec.sendPushNotification(orderFeedMode);

	}
	
	@Override
	public void sendWebhooks() {
		OrderFeedModel orderFeedModel = OrderFeedModel.builder()
				.norenordno("0056012323")
				.status("Test").actid("939223").status("PENDING")
				.build();
		this.orderNotificationServiceSpec.sendWebhooksNotification(orderFeedModel);

	}
 //

	@Override
	public void sendEmail() {
		this.orderNotificationServiceSpec.sendMail();
		
	}
}

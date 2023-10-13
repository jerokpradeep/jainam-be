package in.codifi.odn.service.spec;

import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.WebsocketConnectionResp;

public interface OrderNotificationServiceSpec {

	void sendOrderStatus(String message);

	void saveTodatabase(OrderFeedModel s);

	void sendPushNotification(OrderFeedModel ordeStatusModel);

	void sendWebhooksNotification(OrderFeedModel ordeStatusModel);

	void sendOrderStatusOdin(String message);

	void saveTodatabase(WebsocketConnectionResp s);

	void sendPushNotification(WebsocketConnectionResp ordeStatusModel);

	void sendWebhooksNotification(WebsocketConnectionResp ordeStatusModel);

	boolean authenticatedSession(String message);

	void sendMail();

}

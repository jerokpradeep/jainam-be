package in.codifi.odn.service.spec;

import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.PushNotification;
import in.codifi.odn.model.WebsocketConnectionResp;

public interface PushNotificationServieSpec {

	void sendPushNotification(OrderFeedModel socket);
	
	void sendPushNotification(WebsocketConnectionResp socket);
	
	PushNotification buildPushMessage(OrderFeedModel socket, String fcmToken);
	
	PushNotification buildPushMessage(WebsocketConnectionResp socket, String fcmToken);

}

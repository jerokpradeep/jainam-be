package in.codifi.odn.service.spec;

import in.codifi.odn.model.OrderFeedModel;

public interface WebhooksServiceSpec {
	
	
	void sendWebhooksNotification(OrderFeedModel orderFeedModel);

}
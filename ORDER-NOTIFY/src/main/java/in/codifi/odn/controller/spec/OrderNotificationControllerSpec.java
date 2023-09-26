package in.codifi.odn.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface OrderNotificationControllerSpec {
	
	@Path("/connect")
	@GET
	public void connectWebsocketInfo();	
	
	
	@Path("/notify")
	@GET
	public void sendPushNotification();


	@Path("/webhooks")
	@GET
	void sendWebhooks();
	
	
	@Path("/sendMail")
	@GET
	void sendEmail();

}

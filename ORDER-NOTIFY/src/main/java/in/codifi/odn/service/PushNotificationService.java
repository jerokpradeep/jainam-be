package in.codifi.odn.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.esotericsoftware.minlog.Log;

import in.codifi.odn.config.ApplicationProperties;
import in.codifi.odn.model.Data;
import in.codifi.odn.model.Notification;
import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.PushNotification;
import in.codifi.odn.model.WebsocketConnectionResp;
import in.codifi.odn.service.spec.PushClientService;
import in.codifi.odn.service.spec.PushNotificationServieSpec;
import in.codifi.odn.utility.AppConstants;
import in.codifi.odn.utility.OrderStatusConstants;

@ApplicationScoped
public class PushNotificationService implements PushNotificationServieSpec {

	@Inject
	@RestClient
	PushClientService pushService;

	@Inject
	EntityManager entityManager;

	@Inject
	ApplicationProperties properties;

	@Override
	@Transactional
	public void sendPushNotification(OrderFeedModel socket) {
		TypedQuery<String> query = entityManager
				.createNamedQuery("TBL_DEVICE_MAPPING.listUniqueDeviceIds", String.class)
				.setParameter("userId", socket.getActid());
		try {
			List<String> fcmTokens = query.getResultList();
			fcmTokens.stream().forEach(token -> {
				PushNotification message = buildPushMessage(socket, token);
				sendPushNotificationMessage(message);
			});
		} catch (NoResultException e) {
			Log.error("NoResultException", e);
		}
	}

	private void sendPushNotificationMessage(PushNotification message) {
		Response resp = pushService.send(message);
		if (200 == resp.getStatus()) {
			Log.info("Push notification sent");
		}
	}

	@Override
	@Transactional
	public void sendPushNotification(WebsocketConnectionResp socket) {
		TypedQuery<String> query = entityManager
				.createNamedQuery("TBL_DEVICE_MAPPING.listUniqueDeviceIds", String.class)
				.setParameter("userId", socket.getUcc());
		try {
			List<String> fcmTokens = query.getResultList();
			fcmTokens.stream().forEach(token -> {
				PushNotification message = buildPushMessage(socket, token);
				if(StringUtils.isNotEmpty(message.getNotification().getBody())) {
					sendPushNotificationMessage(message);
				}
			});
		} catch (NoResultException e) {
			Log.error("NoResultException", e);
		}
	}

	@Override
	public PushNotification buildPushMessage(OrderFeedModel socket, String fcmToken) {
		String title = "Order status update";
		String message = "Your order id " + socket.getNorenordno() + " for " + socket.getTsym() + " is "
				+ socket.getStatus();
		return getPushNotificationMessage(fcmToken, title, message);
	}

	@Override
	public PushNotification buildPushMessage(WebsocketConnectionResp socket, String fcmToken) {
		String title = "Order status update";
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Your order id " + socket.getUniqueCode() + " for " + socket.getSymbol() + " is ");
		
		
		if (AppConstants.ORD_NRML.equalsIgnoreCase(socket.getMessageType())) {
			messageBuilder.append(OrderStatusConstants.orderStatusMap.get(socket.getOrderStatus()));
			if(socket.getOrderStatus()==8) {
				messageBuilder = new StringBuilder();
			}
		} else if (AppConstants.TRD_MSG.equalsIgnoreCase(socket.getMessageType())) {
			messageBuilder.append(OrderStatusConstants.orderStatusMap.get(socket.getStatus()));
		} else if (AppConstants.MULTILEG_ORDER_MSG.equalsIgnoreCase(socket.getMessageType())) {
			messageBuilder.append(OrderStatusConstants.orderStatusMap.get(socket.getStatus()));
		} else if(socket.getGTDOrderStatus()>0) {
			messageBuilder.append(OrderStatusConstants.gtdOrderMap.get(socket.getOrderStatus()));
		} else {
			messageBuilder = new StringBuilder();
		}
		return getPushNotificationMessage(fcmToken, title, messageBuilder.toString());
	}


	private PushNotification getPushNotificationMessage(String fcmToken, String title, String message) {
		return PushNotification.builder().to(fcmToken).click_action("FLUTTER_NOTIFICATION_CLICK").collapse_key("1234")
				.notification(Notification.builder().titleColor(properties.getTitleColor()).title(title)
						.channelId(properties.getChannelId()).androidChannelId(properties.getAndroidChannelId())
						.body(message).build())
				.data(Data.builder().type("Order").message(message).title(title).build()).build();
	}

}

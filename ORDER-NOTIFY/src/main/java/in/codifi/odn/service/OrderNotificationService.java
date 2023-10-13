package in.codifi.odn.service;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.WebsocketConnectionResp;
import in.codifi.odn.order.entity.OrderStatusFeedEntity;
import in.codifi.odn.repository.OrderNotificationRepository;
import in.codifi.odn.service.spec.EmailServiceSpec;
import in.codifi.odn.service.spec.OrderNotificationServiceSpec;
import in.codifi.odn.service.spec.PushNotificationServieSpec;
import in.codifi.odn.service.spec.WebhooksServiceSpec;
import in.codifi.odn.utility.AppConstants;
import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;

@ApplicationScoped
public class OrderNotificationService implements OrderNotificationServiceSpec {

	@Inject
	@PersistenceContext(unitName = "logs")
	EntityManager logsDbEntityManager;

	@Inject
	OrderNotificationRepository repository;

	@Inject
	PushNotificationServieSpec pushNotificationServieSpec;

	@Inject
	WebhooksServiceSpec webhooksServiceSpec;
	
	@Inject
	EmailServiceSpec emailService;

	public void sendOrderStatus(String message){
		if (StringUtils.isNotEmpty(message)) {
			OrderFeedModel s = null;
			try {
				s = new ObjectMapper().readValue(message, OrderFeedModel.class);
			} catch (JsonProcessingException e) {
				Log.error("JsonProcessingException", e);
			}
			if (ObjectUtils.isNotEmpty(s) && StringUtils.isNotEmpty(s.getStatus())
					&&  (AppConstants.ORDER_STATUS_LIST.contains(s.getStatus()))) {
					sendPushNotification(s);
//					saveTodatabase(s);
//					sendWebhooksNotification(s);
								
			}
		}
	}

	@Override
	public void sendPushNotification(OrderFeedModel s) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					pushNotificationServieSpec.sendPushNotification(s);
				} catch (Exception e) {
					Log.error("sendPushNotification order", e);
				} finally {
					pool.shutdown();
				}
			}
		});

	}

	@Override
	public void sendWebhooksNotification(OrderFeedModel s) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					webhooksServiceSpec.sendWebhooksNotification(s);
				} catch (Exception e) {
					Log.error("sendWebhooksNotification order", e);
				} finally {
					pool.shutdown();
				}
			}
		});
	}

	@Override
	public void saveTodatabase(OrderFeedModel s) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                Arc.container().requestContext().activate();

                performDatabaseOperation(s);
            } catch (Exception e) {
                Log.error("Save order", e);
            } finally {
            	 Arc.container().requestContext().terminate();
                pool.shutdown();
            }
        });
	}
	
	@Transactional
    private void performDatabaseOperation(OrderFeedModel s) {
        OrderStatusFeedEntity entity = new OrderStatusFeedEntity();
        mapEntity(entity, s);
        try {
        	 logsDbEntityManager.persist(entity);
             logsDbEntityManager.flush();
             }catch (Exception e) {
            	 Log.error("performDatabaseOperation order", e);
			}

       
    }

	private void mapEntity(OrderStatusFeedEntity entity, OrderFeedModel s) {
		try {
			BeanUtils.copyProperties(entity, s);
		} catch (IllegalAccessException | InvocationTargetException e) {
			Log.error("mapEntity", e);
		}
	}
	
	

	@Override
	public void sendOrderStatusOdin(String message) {
		if (StringUtils.isNotEmpty(message)) {
			WebsocketConnectionResp s = null;
			try {
				s = new ObjectMapper().readValue(message, WebsocketConnectionResp.class);
			} catch (JsonProcessingException e) {
				Log.error("JsonProcessingException", e);
			}
			if(AppConstants.J_MSG_TYPE_WEB.equalsIgnoreCase(s.getJMessageType())) {
				return;
			}
			if (s !=null) {	
				
					sendPushNotification(s);
//					/saveTodatabase(s);
//					sendWebhooksNotification(s);		
			}
		
	}

}

	@Override
	public void saveTodatabase(WebsocketConnectionResp s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPushNotification(WebsocketConnectionResp ordeStatusModel) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {

					pushNotificationServieSpec.sendPushNotification(ordeStatusModel);
				} catch (Exception e) {
					Log.error("sendPushNotification order", e);
				} finally {
					pool.shutdown();
				}
			}
		});
		
	}

	@Override
	public void sendWebhooksNotification(WebsocketConnectionResp ordeStatusModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean authenticatedSession(String message) {		
		if (StringUtils.isNotEmpty(message) && message.contains(AppConstants.J_MSG_TYPE_WEB)) {
			WebsocketConnectionResp s = null;
			try {
				s = new ObjectMapper().readValue(message, WebsocketConnectionResp.class);
			} catch (JsonProcessingException e) {
				Log.error("JsonProcessingException", e);
			}
			return !AppConstants.WEB_SOCKET_FAILED_CODE.equalsIgnoreCase(s.getJStatuscode());
		}
		return true;
	}

	@Override
	public void sendMail() {
		emailService.sendDefaultEmail("Test","test123","babin@codifi.in");
		
	}
	
}

package in.codifi.odn.utility;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.odn.config.ApplicationProperties;
import in.codifi.odn.config.HazelcastConfig;
import in.codifi.odn.model.Heartbeat;
import in.codifi.odn.model.WebsocketConnectionReq;
import in.codifi.odn.service.spec.AuthenticationServiceSpec;
import in.codifi.odn.service.spec.EmailServiceSpec;
import in.codifi.odn.service.spec.OrderNotificationServiceSpec;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@ClientEndpoint
@ApplicationScoped
public class OrderWebsocketClient {

	private static final long HEARTBEAT_INTERVAL_SECONDS = 20;

	@Inject
	ApplicationProperties properties;

	private Session session;
	public static int WEB_SOCKET_RETRY_COUNTER = 0;

	@Inject
	OrderNotificationServiceSpec orderNotificationService;

	@Inject
	AuthenticationServiceSpec authenticationServiceSpec;

	@Inject
	EmailServiceSpec emailServiceSpec;

	private String authToken;

	public void init(@Observes StartupEvent ev) throws ServletException {
		adminLogin();
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		if (StringUtils.isNotEmpty(authToken) && StringUtils.isNotEmpty(properties.getWebsocketEndpoint())) {
			this.scheduleHeartbeat();
			WebsocketConnectionReq reqModel = WebsocketConnectionReq.builder().jAPIKey(properties.getApiKey())
					.jRequestID(String.valueOf("CH-" + System.currentTimeMillis()))
					.jMessageType(properties.getJMessageType()).jSecretKey(properties.getJSecretKey()).jToken(authToken)
					.build();
			ObjectMapper mapper = new ObjectMapper();
			try {
				session.getAsyncRemote().sendText(mapper.writeValueAsString(reqModel), r -> {
					if (r.isOK()) {
						Log.info("Web socket Connected");
					} else {
						Log.info("Unable to connect Web socket");
					}
				});
			} catch (JsonProcessingException e) {
				Log.error("", e);
			}
		} else {
			Log.error("Auth token is empty");
			scheduleAdminLogin();
		}

	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		Log.info("WebSocket closed: " + reason.getReasonPhrase());
		if (WEB_SOCKET_RETRY_COUNTER <= 5) {
			WEB_SOCKET_RETRY_COUNTER++;
			scheduleReconnect(); // Schedule a reconnect attempt when the connection is closed
		} else {
			Log.info("Websocket retry  exceeded - " + WEB_SOCKET_RETRY_COUNTER);
			Log.info("Websocket retry  failed and not connected to web socket");
			
			emailServiceSpec.sendExceptionEmailToSupport("Websocket Connection Failed",OrderWebsocketClient.class.getName(), reason.getReasonPhrase());
		}

	}

	@OnMessage
	public void onMessage(String message, Session session) throws URISyntaxException {
		Log.info(message);
		if (orderNotificationService.authenticatedSession(message)) {
			orderNotificationService.sendOrderStatusOdin(message);
		} else {
			authToken = "";
			HazelcastConfig.getInstance().getWebSocketJtoken().put(this.properties.getApiKey(), "");
			scheduleAdminLogin();
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		Log.error("On Error session", throwable);
		if (WEB_SOCKET_RETRY_COUNTER <= 5) {
			WEB_SOCKET_RETRY_COUNTER++;
			scheduleReconnect(); // Schedule a reconnect attempt when the connection is closed
		}

	}

	private void adminLogin() {

		authToken = HazelcastConfig.getInstance().getWebSocketJtoken().get(this.properties.getApiKey());
		if (StringUtils.isEmpty(authToken)) {
			genarateAuthToken();
		}
		connectWs();

	}

	private void genarateAuthToken() {
		authToken = authenticationServiceSpec.getAuthToken();
		authenticationServiceSpec.getWsEndPoint();
		HazelcastConfig.getInstance().getWebSocketJtoken().put(this.properties.getApiKey(), authToken);
	}

	private void scheduleReconnect() {
		Log.info("Scheduling WebSocket reconnect...");
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.schedule(this::connectWs, 5, TimeUnit.SECONDS); // Retry after a delay
	}

	private void scheduleAdminLogin() {
		Log.info("Getting token...");
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.schedule(this::adminLogin, 5, TimeUnit.SECONDS); // Retry after a delay
	}

	private void connectWs() {
		if (StringUtils.isNotEmpty(properties.getWebsocketEndpoint())) {
			CommonUtils.trustedManagement();
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			try {
				container.connectToServer(this, new URI(properties.getWebsocketEndpoint()));
			} catch (DeploymentException e) {
				Log.error("DeploymentException connectWs:::: ", e);
			} catch (IOException e) {
				Log.error("IOException connectWs:::", e);
			} catch (URISyntaxException e) {
				Log.error("URISyntaxException connectWs::: ", e);
			}
		}
	}

	public void connectWebsocketInfo() {
		this.adminLogin();

	}

	private void scheduleHeartbeat() {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		// executorService.scheduleAtFixedRate(this::sendHeartbeat, 0,
		// HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
	}

	private void sendHeartbeat() {
		Heartbeat heartbeatModel = Heartbeat.builder().jMessageType("HEARTBEAT").build();
		ObjectMapper mapper = new ObjectMapper();

		try {
			this.session.getAsyncRemote().sendText(mapper.writeValueAsString(heartbeatModel), r -> {
				if (r.isOK()) {
					Log.info("Heartbeat sent successfully");
				} else {
					Log.error("Failed to send heartbeat");
				}
			});
		} catch (JsonProcessingException e) {
			Log.error("Error while sending heartbeat", e);
		}
	}

}
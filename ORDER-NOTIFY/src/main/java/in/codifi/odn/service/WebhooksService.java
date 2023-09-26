package in.codifi.odn.service;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.odn.model.OrderFeedModel;
import in.codifi.odn.model.OrderStatusModel;
import in.codifi.odn.service.spec.WebHooksClient;
import in.codifi.odn.service.spec.WebhooksServiceSpec;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class WebhooksService implements WebhooksServiceSpec {

	@Inject
	EntityManager entityManager;

	@Inject
	@RestClient
	WebHooksClient webhooksService;

	@Override
	@Transactional
	public void sendWebhooksNotification(OrderFeedModel orderFeedModel) {
		try {
			String webHooksUrl = getWebhooksUrl(orderFeedModel);
			if (StringUtils.isNotEmpty(webHooksUrl)) {
				sendOrderNotification(URI.create(webHooksUrl), getOrdeStatusModel(orderFeedModel));
			}
		} catch (NoResultException e) {
			Log.error("NoResultException - webHooksUrl ", e);
		}
	}

	private String getWebhooksUrl(OrderFeedModel feedModel) {
		String webhooksUrl = null;
		try {
			// get vendor details for account
			
			TypedQuery<String> query = entityManager.createNamedQuery("TBL_WEBHOOKS.listSubscribedUsers", String.class)
					.setParameter("userId", feedModel.getActid());
			webhooksUrl = query.getSingleResult();
		} catch (NoResultException e) {
			Log.info("NoResultException ");
		}catch (Exception e) {
			Log.error("getWebhooksUrl", e);
		}
		return webhooksUrl;
	}

	private OrderStatusModel getOrdeStatusModel(OrderFeedModel message) {
		return OrderStatusModel.builder().amo(message.getAmo()).blprc(message.getBlprc()).bpprc(message.getBpprc())
				.cancelqty(message.getCancelqty()).dscqty(message.getDscqty()).exch(message.getExch())
				.exchordid(message.getExchordid()).exchtm(message.getExchtm()).orderId(message.getNorenordno())
				.pcode(message.getPcode()).prc(message.getPrc()).prctyp(message.getPrctyp()).qty(message.getQty())
				.rejreason(message.getRejreason()).remarks(message.getRemarks()).reporttype(message.getReporttype())
				.ret(message.getRet()).status(message.getStatus()).trailprc(message.getTrailprc())
				.trgprc(message.getTrgprc()).trantype(message.getTrantype()).tsym(message.getTsym()).build();
	}

	public Uni<Void> sendOrderNotification(URI apiUri, OrderStatusModel apiModel) {
		WebHooksClient remoteApi = RestClientBuilder.newBuilder().baseUri(apiUri).build(WebHooksClient.class);
		return remoteApi.sendOrderNotification(apiModel);
	}

}

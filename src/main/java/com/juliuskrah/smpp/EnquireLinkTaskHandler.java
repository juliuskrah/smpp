package com.juliuskrah.smpp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

@Component
public class EnquireLinkTaskHandler implements ApplicationContextAware, ApplicationEventPublisherAware {
	private static final Logger log = LoggerFactory.getLogger(EnquireLinkTaskHandler.class);
	private final ApplicationProperties properties;
	private final SmppClient clientBootstrap;
	private ApplicationContext ctx;
	private ApplicationEventPublisher publisher;

	public EnquireLinkTaskHandler(ApplicationProperties properties, SmppClient clientBootstrap) {
		this.properties = properties;
		this.clientBootstrap = clientBootstrap;
	}

	@Scheduled(initialDelayString = "${sms.async.initial-delay}", fixedDelayString = "${sms.async.initial-delay}")
	void enquireLinkJob() {
		SmppSession session = SmppSessionDelegate.getSession();
		if (session != null && session.isBound()) {
			try {
				log.debug("sending enquire_link");
				EnquireLinkResp enquireLinkResp = session.enquireLink(new EnquireLink(),
						properties.getAsync().getTimeout());
				log.debug("enquire_link_resp: {}", enquireLinkResp);
			} catch (SmppTimeoutException e) {
				log.warn("Enquire link failed, executing reconnect; " + e);
				log.debug("", e);
				publisher.publishEvent(new SessionDisconnectedEvent(this));
			} catch (SmppChannelException e) {
				log.warn("Enquire link failed, executing reconnect; " + e);
				log.debug("", e);
				publisher.publishEvent(new SessionDisconnectedEvent(this));
			} catch (InterruptedException e) {
				log.info("Enquire link interrupted, probably killed by reconnecting");
			} catch (Exception e) {
				log.error("Enquire link failed, executing reconnect", e);
				publisher.publishEvent(new SessionDisconnectedEvent(this));
			}
		} else {
			log.error("enquire link running while session is not connected");
			publisher.publishEvent(new SessionDisconnectedEvent(this));
		}
	}

	@PreDestroy
	private void destroySession() {
		SmppSessionDelegate.destroySession();
	}

	@PostConstruct
	private void initSession() throws SmppBindException, SmppTimeoutException, SmppChannelException,
			UnrecoverablePduException, InterruptedException {
		ClientSmppSessionHandler sessionHandler = ctx.getBean(ClientSmppSessionHandler.class);
		SmppSession session = clientBootstrap.bind(Application.sessionConfiguration(properties), sessionHandler);
		SmppSessionDelegate.setSession(session);
		log.debug("Connection succeeded");
	}

	@Async
	@EventListener
	void onSessionDisconnect(SessionDisconnectedEvent event) {
		log.debug("Connection interrupted, reconnecting...");
		ClientSmppSessionHandler sessionHandler = ctx.getBean(ClientSmppSessionHandler.class);
		SmppSessionDelegate.destroySession();
		try {
			SmppSession session = clientBootstrap.bind(Application.sessionConfiguration(properties),
					sessionHandler);
			SmppSessionDelegate.setSession(session);
			log.debug("Connection succeeded");
		} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException e) {
			log.error("Connection failed", e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

}

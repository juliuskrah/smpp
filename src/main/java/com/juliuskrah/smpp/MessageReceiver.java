package com.juliuskrah.smpp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.component.smpp.SmppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Message receiver bean that log delivery receipts received
 * 
 * @author Julius Krah
 */
@Component
public class MessageReceiver {
	private final static Logger log = LoggerFactory.getLogger(MessageReceiver.class);

	public void receiveDeliveryReceipt(Exchange exchange) {
		if (exchange.getException() == null) {
			var message = exchange.getIn();
			log.info("Received id {}", message.getHeader(SmppConstants.ID));
			log.info("Text :- {}", message.getBody());
			log.info("Total delivered {}", message.getHeader(SmppConstants.DELIVERED));
			log.info("Message status {}", message.getHeader(SmppConstants.FINAL_STATUS));
			log.info("Submitted date {}", asLocalDateTime(message //
					.getHeader(SmppConstants.SUBMIT_DATE, Date.class)));
			log.info("Done date {}", asLocalDateTime(message //
					.getHeader(SmppConstants.DONE_DATE, Date.class)));
		} else
			log.error("Error receiving message", exchange.getException());
	}

	public void receiveDeliverSm(Exchange exchange) {
		log.info("receive deliver sm {}", exchange);
	}

	public void fallback(Exchange exchange) {
		log.info("receive fallback sm {}", exchange);
	}

	private static LocalDateTime asLocalDateTime(Date date) {
		if (date == null)
			return null;
		return Instant.ofEpochMilli(date.getTime()) //
				.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
package com.juliuskrah.smpp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.camel.Exchange;
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
    private final static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    public void receive(Exchange exchange) {
        if (exchange.getException() == null) {
            var message = exchange.getIn();
            logger.info("Received id {}", message.getHeader("CamelSmppId"));
            logger.info("Text :- {}", message.getBody());
            logger.info("Total delivered {}", message.getHeader("CamelSmppDelivered"));
            logger.info("Message status {}", message.getHeader("CamelSmppStatus"));
            logger.info("Submitted date {}", asLocalDateTime(message //
                    .getHeader("CamelSmppSubmitDate", Date.class)));
            logger.info("Done date {}", asLocalDateTime(message.getHeader("CamelSmppDoneDate", Date.class)));
        } else
            logger.error("Error receiving message", exchange.getException());
    }

    private static LocalDateTime asLocalDateTime(Date date) {
        if (date == null)
            return null;
        return Instant.ofEpochMilli(date.getTime()) //
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
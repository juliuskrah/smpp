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
 * MessageReceiver
 */
@Component
public class MessageReceiver {
    private final static Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    public void receive(Exchange exchange) {
        if (exchange.getException() == null) {
            var message = exchange.getIn();
            log.info("Received id {}", message.getHeader("CamelSmppId"));
            log.info("Text :- {}", message.getBody());
            log.info("Total delivered {}", message.getHeader("CamelSmppDelivered"));
            log.info("Message status {}", message.getHeader("CamelSmppStatus"));
            log.info("Submitted date {}", asLocalDateTime(message //
                    .getHeader("CamelSmppSubmitDate", Date.class)));
            log.info("Done date {}", asLocalDateTime(message.getHeader("CamelSmppDoneDate", Date.class)));
        } else
            log.error("Error receiving message", exchange.getException());
    }

    private static LocalDateTime asLocalDateTime(Date date) {
        if (date == null)
            return null;
        return Instant.ofEpochMilli(date.getTime()) //
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
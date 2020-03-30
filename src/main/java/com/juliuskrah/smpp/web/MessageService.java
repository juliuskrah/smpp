package com.juliuskrah.smpp.web;

import static com.juliuskrah.smpp.Application.toLocalDateTime;
import static com.juliuskrah.smpp.Application.toZonedDateTime;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.smpp.SmppCommandType;
import org.apache.camel.component.smpp.SmppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service that determines type of message to send
 * @author Julius Krah
 *
 */
@Service
public class MessageService {
	private static final Logger log = LoggerFactory.getLogger(MessageService.class);
	private final ProducerTemplate template;
	private final CamelContext context;

	public MessageService(ProducerTemplate template, CamelContext context) {
		this.template = template;
		this.context = context;
	}

	public void send(Message message) {
		var type = message.getType();
		switch (type) {
		case SUBMIT_SM:
			var messageIds = submitSm(message);
			log.info("From submit-sm {}", messageIds);
			break;
		case REPLACE_SM:
			replaceSm(message);
			break;
		case QUERY_SM:
			var result = querySm(message);
			log.info("From query {}", result);
			break;
		case CANCEL_SM:
			cancelSm(message);
			break;
		case DATA_SHORT_MESSAGE:
			dataSm(message);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> submitSm(Message message) {
		var destinationAddresses = message.getTo();
		var scheduleDeliveryTime = toDate(message.getScheduleDeliveryTime());
		log.info("Sending messages to {}", destinationAddresses);

		var builder = ExchangeBuilder.anExchange(context) //
				.withHeader(SmppConstants.DEST_ADDR, destinationAddresses) //
				.withBody(message.getContent());
		if (destinationAddresses.size() > 1)
			builder.withHeader(SmppConstants.COMMAND, SmppCommandType.SUBMIT_MULTI.getCommandName());
		if (scheduleDeliveryTime != null)
			builder.withHeader(SmppConstants.SCHEDULE_DELIVERY_TIME, scheduleDeliveryTime);
		if (message.getFrom() != null)
			builder.withHeader(SmppConstants.SOURCE_ADDR, message.getFrom());

		var result = template.send("smpp://{{camel.component.smpp.configuration.host}}", builder.build());
		if (result.getException() == null)
			return result.getMessage().getHeader(SmppConstants.ID, List.class);
		else
			throw new RuntimeException(result.getException());
	}

	private void replaceSm(Message message) {

	}

	public QueryMessageResult querySm(Message message) {
		var builder = ExchangeBuilder.anExchange(context) //
				.withHeader(SmppConstants.ID, message.getMessageId()) //
				.withHeader(SmppConstants.COMMAND, SmppCommandType.QUERY_SM.getCommandName());
		if (message.getFrom() != null)
			builder.withHeader(SmppConstants.SOURCE_ADDR, message.getFrom());
		var result = template.send("smpp://{{camel.component.smpp.configuration.host}}", builder.build());
		if (result.getException() == null) {
			var queryResult = new QueryMessageResult();
			var finalDate = result.getMessage().getHeader(SmppConstants.FINAL_DATE, Date.class);
			queryResult.setErrorCode(result.getMessage().getHeader(SmppConstants.ERROR, byte.class));
			queryResult.setMessageId(result.getMessage().getHeader(SmppConstants.ID, String.class));
			queryResult.setFinalDate(fromDate(finalDate));
			queryResult.setMessageStatus(result.getMessage().getHeader(SmppConstants.MESSAGE_STATE, String.class));
			result.getMessage().getHeader(SmppConstants.ID, List.class);
			return queryResult;
		} else
			throw new RuntimeException(result.getException());

	}

	private void cancelSm(Message message) {

	}

	private void dataSm(Message message) {

	}

	private Date toDate(LocalDateTime dateTime) {
		var zonedDateTime = toZonedDateTime(dateTime);
		if (zonedDateTime == null)
			return null;
		return Date.from(zonedDateTime.toInstant());
	}

	private LocalDateTime fromDate(Date date) {
		if (date == null)
			return null;
		return toLocalDateTime(date.toInstant());
	}

}

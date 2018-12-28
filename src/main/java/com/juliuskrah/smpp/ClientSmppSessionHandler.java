package com.juliuskrah.smpp;

import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.util.SmppUtil;

@Component
public class ClientSmppSessionHandler extends DefaultSmppSessionHandler implements ApplicationEventPublisherAware {
	private static final Logger log = LoggerFactory.getLogger(ClientSmppSessionHandler.class);
	private ApplicationEventPublisher publisher;
	private final ApplicationProperties properties;

	public ClientSmppSessionHandler(ApplicationProperties properties) {
		this.properties = properties;
	}

	private String mapDataCodingToCharset(byte dataCoding) {
		switch (dataCoding) {
		case SmppConstants.DATA_CODING_LATIN1:
			return CharsetUtil.NAME_ISO_8859_1;
		case SmppConstants.DATA_CODING_UCS2:
			return CharsetUtil.NAME_UCS_2;
		default:
			return CharsetUtil.NAME_GSM;
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public PduResponse firePduRequestReceived(PduRequest request) {
		PduResponse response = null;
		try {
			if (request instanceof DeliverSm) {
				String sourceAddress = ((DeliverSm) request).getSourceAddress().getAddress();
				String message = CharsetUtil.decode(((DeliverSm) request).getShortMessage(),
						mapDataCodingToCharset(((DeliverSm) request).getDataCoding()));
				log.debug("SMS Message Received: {}, Source Address: {}", message.trim(), sourceAddress);

				boolean isDeliveryReceipt = false;
				if (properties.getSmpp().isDetectDlrByOpts()) {
					isDeliveryReceipt = request.getOptionalParameters() != null;
				} else {
					isDeliveryReceipt = SmppUtil.isMessageTypeAnyDeliveryReceipt(((DeliverSm) request).getEsmClass());
				}

				if (isDeliveryReceipt) {
					DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(message, ZoneOffset.UTC);
					log.info("Received delivery from {} at {} with message-id {} and status {}", sourceAddress,
							dlr.getDoneDate(), dlr.getMessageId(), DeliveryReceipt.toStateText(dlr.getState()));
				}
			}
			response = request.createResponse();
		} catch (Throwable error) {
			log.warn("Error while handling delivery", error);
			response = request.createResponse();
			response.setResultMessage(error.getMessage());
			response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
		}
		return response;
	}

	@Override
	public void fireChannelUnexpectedlyClosed() {
		log.warn("SMPP session channel unexpectedly closed");
		publisher.publishEvent(new SessionDisconnectedEvent(this));
	}
}

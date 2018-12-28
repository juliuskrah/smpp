package com.juliuskrah.smpp;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		new Application().sendTextMessage("3299", "Hello World", "<replace>");
	}

	public SmppSessionConfiguration sessionConfiguration(ApplicationProperties properties) {
		SmppSessionConfiguration sessionConfig = new SmppSessionConfiguration();
		sessionConfig.setName("smpp.session");
		sessionConfig.setInterfaceVersion(SmppConstants.VERSION_3_4);
		sessionConfig.setType(SmppBindType.TRANSCEIVER);
		sessionConfig.setHost(properties.getSmpp().getHost());
		sessionConfig.setPort(properties.getSmpp().getPort());
		sessionConfig.setSystemId(properties.getSmpp().getUserId());
		sessionConfig.setPassword(properties.getSmpp().getPassword());
		sessionConfig.setSystemType(null);
		sessionConfig.getLoggingOptions().setLogBytes(false);
		sessionConfig.getLoggingOptions().setLogPdu(true);

		return sessionConfig;
	}

	@Bean(destroyMethod = "destroy")
	public SmppClient clientBootstrap(ApplicationProperties properties) {
		return new DefaultSmppClient(Executors.newCachedThreadPool(), properties.getAsync().getSmppSessionSize());
	}

	private void sendTextMessage(String sourceAddress, String message, String destinationAddress) {
		SmppSession session = SmppSessionDelegate.getSession();
		if (session != null && session.isBound()) {
			try {
				boolean requestDlr = true;
				SubmitSm submit = new SubmitSm();
				byte[] textBytes;
				textBytes = CharsetUtil.encode(message, CharsetUtil.CHARSET_ISO_8859_1);
				submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);
				if (requestDlr) {
					submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
				}

				if (textBytes != null && textBytes.length > 255) {
					submit.addOptionalParameter(
							new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, textBytes, "message_payload"));
				} else {
					submit.setShortMessage(textBytes);
				}

				submit.setSourceAddress(new Address((byte) 0x05, (byte) 0x01, sourceAddress));
				submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, destinationAddress));
				SubmitSmResp submitResponse = session.submit(submit, 10000);
				if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
					log.info("SMS submitted, message id {}", submitResponse.getMessageId());
				} else {
					throw new IllegalStateException(submitResponse.getResultMessage());
				}
			} catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException
					| InterruptedException e) {
				throw new IllegalStateException(e);
			}
			return;
		}
		throw new IllegalStateException("SMPP session is not connected");
	}

}

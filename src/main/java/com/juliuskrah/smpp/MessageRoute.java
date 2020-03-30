package com.juliuskrah.smpp;

import static org.apache.camel.component.smpp.SmppMessageType.*;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.smpp.SmppConstants;
import org.jsmpp.extra.ProcessRequestException;
import org.springframework.stereotype.Component;

/**
 * Router to route messages from SMSC to bean class
 * 
 * @author Julius Krah
 */
@Component
public class MessageRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// @formatter:off
        from("smpp://{{camel.component.smpp.configuration.host}}") //
            .doTry() //
                .choice() //
                    .when(header(SmppConstants.MESSAGE_TYPE).isEqualTo((DeliveryReceipt.toString()))) //
                        .bean("messageReceiver", "receiveDeliveryReceipt") //
                    .when(header(SmppConstants.MESSAGE_TYPE).isEqualTo((DeliverSm.toString()))) //
                        .bean("messageReceiver", "receiveDeliverSm") //
                    .otherwise() //
                        .bean("messageReceiver", "fallback") //
                .endChoice() //
            .endDoTry() //
            .doCatch(Exception.class) //
                .throwException(new ProcessRequestException("update of sms state failed", 100)) //
            .end();
        // @formatter:off
    }   
}
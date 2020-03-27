package com.juliuskrah.smpp;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * MessageRoute
 */
@Component
public class MessageRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("smpp://{{camel.component.smpp.configuration.host}}") //
                .to("bean:messageReceiver??method=receive");
    }   
}
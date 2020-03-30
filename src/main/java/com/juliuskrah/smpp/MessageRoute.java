package com.juliuskrah.smpp;

import org.apache.camel.builder.RouteBuilder;
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
        from("smpp://{{camel.component.smpp.configuration.host}}") //
                .to("bean:messageReceiver?method=receive");
    }   
}
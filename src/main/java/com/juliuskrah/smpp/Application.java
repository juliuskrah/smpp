package com.juliuskrah.smpp;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
/**
 * 
 * @author Julius Krah
 *
 */
@SpringBootApplication
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	@Autowired
	private CamelContext context;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	//#region
	private Exchange sendTextMessage(ProducerTemplate template, String sourceAddress, //
			String destinationAddress, String message) {
		var exchange = ExchangeBuilder.anExchange(context) //
				.withHeader("CamelSmppDestAddr", List.of(destinationAddress)) //
				.withHeader("CamelSmppSourceAddr", sourceAddress) //
				.withPattern(ExchangePattern.InOnly) //
				.withBody(message).build();
		// exceptions are not thrown from this method
		// exceptions are stored in Exchange#setException()
		return template.send("smpp://{{camel.component.smpp.configuration.host}}", exchange);
	}
	//#endregion

	//#region
	@Bean
	CommandLineRunner init(ProducerTemplate template) {
		return args -> {
			var exchange = sendTextMessage(template, "5432", "<telephone number>", "Hello World!");
			if (exchange.getException() == null)
				logger.info("Message Id - {}", exchange.getMessage().getHeader("CamelSmppId"));
			else
				logger.error("Could not send message", exchange.getException());
		};
	}
	//#endregion
}

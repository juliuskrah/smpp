package com.juliuskrah.smpp;

import org.apache.camel.component.smpp.SmppComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.jsmpp.session.SessionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Customizer that registers the {@link SessionStateListener}
 * 
 * @author Julius Krah
 *
 */
@Component
public class SmppCustomizer implements ComponentCustomizer<SmppComponent> {
	private static final Logger logger = LoggerFactory.getLogger(SmppCustomizer.class);

	@Override
	public void customize(SmppComponent component) {
		var configuration = component.getConfiguration();
		configuration.setSessionStateListener(new DefaultSessionStateListener());
		logger.debug("config {}", configuration);
	}

}

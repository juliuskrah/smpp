package com.juliuskrah.smpp;

import org.jsmpp.extra.SessionState;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation that just logs session state events
 * 
 * @author Julius Krah
 *
 */
public class DefaultSessionStateListener implements SessionStateListener {
	private final static Logger logger = LoggerFactory.getLogger(DefaultSessionStateListener.class);

	@Override
	public void onStateChange(SessionState newState, SessionState oldState, Session source) {
		logger.info("Session state changed from {} to {}", oldState, newState);
	}

}

package com.juliuskrah.smpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppSession;

public class SmppSessionDelegate {
	private static SmppSession session;
	private static final Logger log = LoggerFactory.getLogger(SmppSessionDelegate.class);

	private SmppSessionDelegate() {
	}

	public static void destroySession() {
		try {
			if (session != null) {
				log.debug("Cleaning up session...");

				session.destroy();
				session = null;
				// alternatively, could call close(), get outstanding requests from
				// the sendWindow (if we wanted to retry them later), then call shutdown()
			}
		} catch (Exception e) {
			log.warn("Destroy session error", e);
		}
	}

	public static SmppSession getSession() {
		return session;
	}

	public static void setSession(SmppSession session) {
		SmppSessionDelegate.session = session;
	}

}

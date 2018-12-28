package com.juliuskrah.smpp;

import org.springframework.context.ApplicationEvent;

public class SessionDisconnectedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public SessionDisconnectedEvent(Object source) {
		super(source);
	}

}

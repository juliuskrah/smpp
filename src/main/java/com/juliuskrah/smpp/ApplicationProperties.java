package com.juliuskrah.smpp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("sms")
public class ApplicationProperties {
	@NestedConfigurationProperty
	private final Async async = new Async();
	@NestedConfigurationProperty
	private final SMPP smpp = new SMPP();

	public Async getAsync() {
		return async;
	}

	public SMPP getSmpp() {
		return smpp;
	}

	public static class Async {
		/**
		 * This number should be lower than the value assigned to the core-pool-size
		 */
		private int smppSessionSize = 2;

		private int corePoolSize = 5;

		private int maxPoolSize = 50;

		private int queueCapacity = 10000;

		private int initialDelay = 1000;

		private int timeout = 10000;

		public int getSmppSessionSize() {
			return smppSessionSize;
		}

		public void setSmppSessionSize(int smppSessionSize) {
			this.smppSessionSize = smppSessionSize;
		}

		public int getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public int getMaxPoolSize() {
			return maxPoolSize;
		}

		public void setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		public int getQueueCapacity() {
			return queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		public int getInitialDelay() {
			return initialDelay;
		}

		public void setInitialDelay(int initialDelay) {
			this.initialDelay = initialDelay;
		}

		public int getTimeout() {
			return timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
	}

	public static class SMPP {
		private String host;
		private String userId;
		private String password;
		private int port = 2775;
		private boolean requestDelivery = false;
		private boolean detectDlrByOpts = false;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public boolean isRequestDelivery() {
			return requestDelivery;
		}

		public void setRequestDelivery(boolean requestDelivery) {
			this.requestDelivery = requestDelivery;
		}

		public boolean isDetectDlrByOpts() {
			return detectDlrByOpts;
		}

		public void setDetectDlrByOpts(boolean detectDlrByOpts) {
			this.detectDlrByOpts = detectDlrByOpts;
		}

	}

}

package com.juliuskrah.smpp.web;

import java.time.LocalDateTime;

/**
 * holder for query results
 * @author Julius Krah
 *
 */
public class QueryMessageResult {
	private String messageId;
	private byte errorCode;
	private LocalDateTime finalDate;
	private String messageStatus;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(byte errorCode) {
		this.errorCode = errorCode;
	}

	public LocalDateTime getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(LocalDateTime finalDate) {
		this.finalDate = finalDate;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	@Override
	public String toString() {
		return "QueryMessageResult [errorCode=" + errorCode + ", finalDate=" + finalDate + ", messageId=" + messageId
				+ ", messageStatus=" + messageStatus + "]";
	}

}

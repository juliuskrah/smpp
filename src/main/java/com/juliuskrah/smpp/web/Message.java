package com.juliuskrah.smpp.web;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.juliuskrah.smpp.validation.NonNullOnDependentField;

/**
 * 
 * @author Julius Krah
 *
 */
@NonNullOnDependentField(fieldName = "type", //
		fieldValue = { "QUERY_SM", "REPLACE_SM", "CANCEL_SM" }, //
		dependentFieldName = "messageId")
@NonNullOnDependentField(fieldName = "type", //
		fieldValue = { "SUBMIT_SM" }, //
		dependentFieldName = "content")
public class Message {
	private MessageType type = MessageType.SUBMIT_SM;
	private String from;
	private String messageId;
	@NotEmpty
	private List<@NotBlank String> to;
	private String content;
	@Future
	private LocalDateTime scheduleDeliveryTime;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getScheduleDeliveryTime() {
		return scheduleDeliveryTime;
	}

	public void setScheduleDeliveryTime(LocalDateTime scheduleDeliveryTime) {
		this.scheduleDeliveryTime = scheduleDeliveryTime;
	}

	@Override
	public String toString() {
		return "Message [content=" + content + ", from=" + from + ", messageId=" + messageId + ", scheduleDeliveryTime="
				+ scheduleDeliveryTime + ", to=" + to + ", type=" + type + "]";
	}

}

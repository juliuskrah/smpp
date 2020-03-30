package com.juliuskrah.smpp.web;

import static org.springframework.http.HttpStatus.*;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that supports the
 * <ul>
 * <li>The sending of messages {@code SubmitSm}, {@code SubmitMulti}</li>
 * <li>The replacement of messages {@code ReplaceSm}</li>
 * <li>The canceling of messages {@code CancelSm}</li>
 * <li>The querying of messages {@code QuerySm}</li>
 * <li>The sending of data messages {@code DataSm}</li>
 * </ul>
 * 
 * @author Julius Krah
 */
@RestController
@RequestMapping("messages")
public class MessageController {
	private static final Logger log = LoggerFactory.getLogger(MessageController.class);
	private final MessageService service;

	public MessageController(MessageService service) {
		this.service = service;
	}

	/**
	 * POST /messages
	 * @param message payload
	 */
	@PostMapping
	@ResponseStatus(ACCEPTED)
	public void sendMessage(@Valid @RequestBody Message message) {
		log.info("Received {}", message);
		service.send(message);
	}

	/**
	 * GET /messages/:id
	 * @param id message id
	 * @return
	 */
	@GetMapping("/{id}")
	public QueryMessageResult queryMessage(@PathVariable String id) {
		var message = new Message();
		message.setMessageId(id);
		return service.querySm(message);
	}
}

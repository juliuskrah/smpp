# Camel SMPP Example

Short Message Peer-to-Peer using Spring Boot and Camel. This is a demo application that sends SMS messages and listens for delivery.

## Implementation to Recieve delivery reports

Two classes are created to handle message delivery

- `MessageReceiver.java`
- `MessageRoute.java`

The message receiver is used to process the messages received:

```java
@Component
public class MessageReceiver {

    private final static Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    public void receiveDeliveryReceipt(Exchange exchange) {
        if (exchange.getException() == null) {
            var message = exchange.getIn();
            log.info("Received id {}", message.getHeader(SmppConstants.ID));
            log.info("Text :- {}", message.getBody());
            log.info("Total delivered {}", message.getHeader(SmppConstants.DELIVERED));
            log.info("Message status {}", message.getHeader(SmppConstants.FINAL_STATUS));
            log.info("Submitted date {}", asLocalDateTime(message
                    .getHeader(SmppConstants.SUBMIT_DATE, Date.class)));
            log.info("Done date {}", asLocalDateTime(message
                    .getHeader(SmppConstants.SUBMIT_DATE, Date.class)));
        } else
            log.error("Error receiving message", exchange.getException());
    }

    public void receiveDeliverSm(Exchange exchange) {
        // TODO
        log.info("receive deliver sm {}", exchange);
    }

    public void fallback(Exchange exchange) {
        // TODO
        log.info("receive fallback sm {}", exchange);
    }

    private static LocalDateTime asLocalDateTime(Date date) {
        if (date == null)
            return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
```

And the message router is used to route messages from the SMSC to my bean

```java
@Component
public class MessageRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("smpp://{{camel.component.smpp.configuration.host}}")
            .doTry()
                .choice()
                    .when(header(SmppConstants.MESSAGE_TYPE).isEqualTo((DeliveryReceipt.toString())))
                        .bean("messageReceiver", "receiveDeliveryReceipt")
                    .when(header(SmppConstants.MESSAGE_TYPE).isEqualTo((DeliverSm.toString())))
                        .bean("messageReceiver", "receiveDeliverSm")
                    .otherwise()
                        .bean("messageReceiver", "fallback")
                .endChoice()
            .endDoTry()
            .doCatch(Exception.class)
                .throwException(new ProcessRequestException("update of sms state failed", 100))
            .end();
    }
}
```

## Implementation to send SMS

Make a POST request request to `http://127.0.0.1:8080/messages` with sample payload:

```json
{
    "type": 0, // 0, 1, 2, 3, or 4
    "from": "3306", // Optional
    "messageId": "d2dd9955e7344d409c255a488d37718c", // Required when type is 1, 2, or 3
    "to": [
        "<telephone_number>"
    ],
    "content": "Hello world!", // Required when message type is 0 or 1
    "scheduleDeliveryTime": "2020-03-30T09:19:00" // Optional
}
```

`type` is the message type enum:

- 0: SUBMIT_SM
- 1: REPLACE_SM
- 2: QUERY_SM
- 3: CANCEL_SM
- 4: DATA_SHORT_MESSAGE

```java
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
}
```

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

    public void receive(Exchange exchange) {
        if (exchange.getException() == null) {
            var message = exchange.getIn();
            log.info("Received id {}", message.getHeader("CamelSmppId"));
            log.info("Text :- {}", message.getBody());
            log.info("Total delivered {}", message.getHeader("CamelSmppDelivered"));
            log.info("Message status {}", message.getHeader("CamelSmppStatus"));
            log.info("Submitted date {}", asLocalDateTime(message //
                    .getHeader("CamelSmppSubmitDate", Date.class)));
            log.info("Done date {}", asLocalDateTime(message.getHeader("CamelSmppDoneDate", Date.class)));
        } else
            log.error("Error receiving message", exchange.getException());
    }

    private static LocalDateTime asLocalDateTime(Date date) {
        if (date == null)
            return null;
        return Instant.ofEpochMilli(date.getTime()) //
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
        from("smpp://{{camel.component.smpp.configuration.host}}") //
                .to("bean:messageReceiver?method=receive");
    }
}
```

## Implementation to send SMS

I am using the `ProducerTemplate` to send SMS messages

```java
// class body omitted

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

@Bean
CommandLineRunner init(ProducerTemplate template) {
    return args -> {
        var exchange = sendTextMessage(template, "5432", "<telephone number>", "Hello World!");
        if (exchange.getException() == null)
            log.info("Message Id - {}", exchange.getMessage().getHeader("CamelSmppId"));
        else
            log.error("Could not send message", exchange.getException());
    };
}
```

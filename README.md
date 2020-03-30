# ClouhHopper SMPP Example

Short Message Peer-to-Peer using Spring Boot and CloudHopper. This is a demo application that sends SMS
messages and listens for delivery receipts.

## Branches

| Branch        | Purpose                                                       | Url   |
| ------------- |:-------------------------------------------------------------:| -----:|
| Master        | Uses CloudHopper SMPP library                                 | [https://github.com/juliuskrah/smpp/](https://github.com/juliuskrah/smpp) |
| Persistent    | Uses CloupHopper SMPP library and supports extending sessions |   [https://github.com/juliuskrah/smpp/tree/persistent](https://github.com/juliuskrah/smpp/tree/persistent) |
| Camel         | Uses Camel SMPP Component                                     |    [https://github.com/juliuskrah/smpp/tree/camel](https://github.com/juliuskrah/smpp/tree/camel) |
| Camel Rest    | Uses Camel SMPP Component and exposes a REST API              |     Coming soon  |

## To send an SMS

```java
private void sendTextMessage(SmppSession session, String sourceAddress, String message, String destinationAddress) {
    if (session.isBound()) {
        try {
            boolean requestDlr = true;
            SubmitSm submit = new SubmitSm();
            byte[] textBytes;
            textBytes = CharsetUtil.encode(message, CharsetUtil.CHARSET_ISO_8859_1);
            submit.setDataCoding(SmppConstants.DATA_CODING_LATIN1);
            if (requestDlr) {
                submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
            }

            if (textBytes != null && textBytes.length > 255) {
                submit.addOptionalParameter(
                        new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, textBytes, "message_payload"));
            } else {
                submit.setShortMessage(textBytes);
            }

            submit.setSourceAddress(new Address((byte) 0x05, (byte) 0x01, sourceAddress));
            submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, destinationAddress));
            SubmitSmResp submitResponse = session.submit(submit, 10000);
            if (submitResponse.getCommandStatus() == SmppConstants.STATUS_OK) {
                log.info("SMS submitted, message id {}", submitResponse.getMessageId());
            } else {
                throw new IllegalStateException(submitResponse.getResultMessage());
            }
        } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException |
        SmppChannelException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return;
    }
    throw new IllegalStateException("SMPP session is not connected");
}
```

## To recieve delivery reports

To receive delivery, an implementation of `SmppSessionListener` was added:

```java
@Override
public PduResponse firePduRequestReceived(PduRequest request) {
    PduResponse response = null;
    try {
        if (request instanceof DeliverSm) {
            String sourceAddress = ((DeliverSm) request).getSourceAddress().getAddress();
            String message = CharsetUtil.decode(((DeliverSm) request).getShortMessage(),
                    mapDataCodingToCharset(((DeliverSm) request).getDataCoding()));
            log.debug("SMS Message Received: {}, Source Address: {}", message.trim(), sourceAddress);

            boolean isDeliveryReceipt = false;
            if (properties.getSmpp().isDetectDlrByOpts()) {
                isDeliveryReceipt = request.getOptionalParameters() != null;
            } else {
                isDeliveryReceipt = SmppUtil.isMessageTypeAnyDeliveryReceipt(((DeliverSm) request).getEsmClass());
            }

            if (isDeliveryReceipt) {
                DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(message, ZoneOffset.UTC);
                log.info("Received delivery from {} at {} with message-id {} and status {}", sourceAddress,
                        dlr.getDoneDate(), dlr.getMessageId(), DeliveryReceipt.toStateText(dlr.getState()));
            }
        }
        response = request.createResponse();
    } catch (Throwable error) {
        log.warn("Error while handling delivery", error);
        response = request.createResponse();
        response.setResultMessage(error.getMessage());
        response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
    }
    return response;
}
```

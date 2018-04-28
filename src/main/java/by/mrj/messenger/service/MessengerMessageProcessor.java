package by.mrj.messenger.service;

import by.mrj.message.domain.Acknowledge;
import by.mrj.message.domain.Message;
import by.mrj.message.types.Command;
import by.mrj.message.types.ResponseStatus;
import by.mrj.message.util.MessageUtils;
import by.mrj.messaging.network.MessageProcessor;
import by.mrj.messenger.domain.TextMessage;
import by.mrj.messenger.printer.Printer;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MessengerMessageProcessor implements MessageProcessor {

    private final Printer consolePrinter;

    @Autowired
    public MessengerMessageProcessor(Printer consolePrinter) {this.consolePrinter = consolePrinter;}

    @Override
    public Message<?> process(Message<?> message) {
        Object payload = message.getPayload();

        if (!isAcceptable(payload))
            return null;

        if (!MessageUtils.verifyMessage(message)) {
            log.error("Message check failed.");
            return null;
        }

        @SuppressWarnings("unchecked")
        val msg = (Message<TextMessage>) message;
        consolePrinter.print(msg);

        Acknowledge ack = Acknowledge.builder()
                .address(message.getAddress())
                .responseStatus(ResponseStatus.OK)
                .correlationId(message.getChecksum())
                .build();

        return MessageUtils.makeMessageWithSig(ack, Command.ACKNOWLEDGE);
    }

    private boolean isAcceptable(Object payload) {
        return payload instanceof TextMessage;
    }
}

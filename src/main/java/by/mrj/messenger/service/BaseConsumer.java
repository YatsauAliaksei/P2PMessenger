package by.mrj.messenger.service;

import by.mrj.message.domain.Message;
import by.mrj.message.util.MessageUtils;
import by.mrj.messaging.network.MsgService;
import by.mrj.messaging.network.NetworkService;
import by.mrj.message.types.Command;
import by.mrj.messenger.domain.TextMessage;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
// fixme: not good idea to use common Consumer interface in that case. Idea doesn't show usages properly.
public class BaseConsumer implements Consumer<String> {

    private final NetworkService networkService;
    private final MsgService msgService;

    @Autowired
    public BaseConsumer(NetworkService networkService, MsgService msgService) {
        this.networkService = networkService;
        this.msgService = msgService;
    }

    @Override
    public void accept(String s) {
        CompletableFuture.runAsync(() -> {
            TextMessage payload = TextMessage.builder().text(s).timestamp(Instant.now()).build();
            Message<TextMessage> message = MessageUtils.makeMessageWithSig(payload, Command.HANDSHAKE);
            msgService.sendMessageToNetwork(message);
        }).handle((aVoid, throwable) -> {
            if (throwable != null) {
                log.error("Error occurred while sending message", throwable);
            }
            return aVoid;
        });
    }
}

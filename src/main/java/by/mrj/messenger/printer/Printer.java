package by.mrj.messenger.printer;

import by.mrj.messaging.network.Message;
import by.mrj.messenger.domain.TextMessage;

public interface Printer {

    void print(Message<TextMessage> message);
}

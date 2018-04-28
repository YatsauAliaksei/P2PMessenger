package by.mrj.messenger.printer;

import by.mrj.message.domain.Message;
import by.mrj.messenger.domain.TextMessage;

public interface Printer {

    void print(Message<TextMessage> message);
}

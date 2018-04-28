package by.mrj.messenger.printer;

import by.mrj.message.domain.Message;
import by.mrj.messenger.domain.TextMessage;

import org.springframework.stereotype.Component;

@Component
public class ConsolePrinter implements Printer {

    void printBody(Message<TextMessage> message) {
        System.out.print(message.getPayload().getText());
    }

    void printPrefix(Message<TextMessage> message) {
        System.out.print(message.getAddress().substring(0, 10) + ": ");
    }

    @Override
    public void print(Message<TextMessage> message) {
        printPrefix(message);
        printBody(message);

        System.out.println("\nMe: ");
    }
}

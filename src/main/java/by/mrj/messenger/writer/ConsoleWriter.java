package by.mrj.messenger.writer;

import javax.annotation.PostConstruct;
import java.io.Console;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsoleWriter implements Writer {

    private final Consumer<String> baseConsumer;

    @Autowired
    public ConsoleWriter(Consumer<String> baseConsumer) {
        this.baseConsumer = baseConsumer;
    }

    @PostConstruct
    void init() {
        write(baseConsumer);
    }

    @Override
    public void write(Consumer<String> baseConsumer) {
        InputStream in = System.in;

        Scanner scanner = new Scanner(in);
        String line;
        System.out.println("Me: ");
//        while ((line = console.readLine()) != null) {
        while ((line = scanner.nextLine()) != null) {
            baseConsumer.accept(line);
            System.out.println("\nMe: ");
        }
    }
}

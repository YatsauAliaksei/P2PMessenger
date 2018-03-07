package by.mrj.messenger.writer;

import java.util.function.Consumer;

public interface Writer {

    void write(Consumer<String> baseConsumer);
}

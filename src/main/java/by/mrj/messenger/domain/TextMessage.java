package by.mrj.messenger.domain;

import by.mrj.crypto.util.CryptoUtils;
import by.mrj.message.domain.Hashable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
@EqualsAndHashCode
public class TextMessage implements Hashable, Serializable {
    @NonNull
    String text;
    @NonNull
    Instant timestamp;

    @Override
    public String hash() {
        return CryptoUtils.doubleSha256(text + timestamp.getEpochSecond());
    }
}

package ru.kubsu.borshchevyk.sync.domain.model;

import lombok.Value;

import java.util.Base64;

/**
 * Domain model representing a synchronization token used for pagination and state tracking.
 *
 * @author Aleksey Timko
 */
@Value
public class SyncToken {
    String value;

    public SyncToken(String value) {
        this.value = value;
    }

    public static SyncToken encode(Long sequenceNumber) {
        if (sequenceNumber == null) {
            return new SyncToken(Base64.getEncoder().encodeToString("0".getBytes()));
        }
        return new SyncToken(Base64.getEncoder().encodeToString(sequenceNumber.toString().getBytes()));
    }

    public Long decode() {
        if (value == null || value.isEmpty()) {
            return 0L;
        }
        try {
            String decodedString = new String(Base64.getDecoder().decode(value));
            return Long.parseLong(decodedString);
        } catch (Exception e) {
            return 0L;
        }
    }
}

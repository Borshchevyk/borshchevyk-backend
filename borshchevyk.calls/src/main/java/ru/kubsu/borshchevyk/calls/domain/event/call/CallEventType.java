package ru.kubsu.borshchevyk.calls.domain.event.call;

import lombok.Getter;

@Getter
public enum CallEventType {
    INITIATED("INITIATED"),
    ENDED("ENDED"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");

    private final String name;

    CallEventType(String name) {
        this.name = name;
    }
}

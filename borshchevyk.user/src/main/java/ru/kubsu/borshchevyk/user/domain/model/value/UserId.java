package ru.kubsu.borshchevyk.user.domain.model.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class UserId {
    private final UUID value;
}

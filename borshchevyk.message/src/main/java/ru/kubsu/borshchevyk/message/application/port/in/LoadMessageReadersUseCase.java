package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageReadersQuery;

import java.util.List;
import java.util.UUID;

public interface LoadMessageReadersUseCase {
    List<UUID> loadMessageReaders(LoadMessageReadersQuery query);
}
package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.List;
import java.util.UUID;

public interface LoadMessageReadersUseCase {
    List<UUID> loadMessageReaders(UUID chatId, UUID messageId, UUID requesterId);
}

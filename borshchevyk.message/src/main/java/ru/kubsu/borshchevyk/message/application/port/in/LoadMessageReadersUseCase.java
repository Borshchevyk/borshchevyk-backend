package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.List;
import java.util.UUID;

/**
 * UseCase for loading the list of users who read a message.
 *
 * @author Aleksey Timko
 */
public interface LoadMessageReadersUseCase {
    List<UUID> loadMessageReaders(UUID chatId, UUID messageId, UUID requesterId);
}

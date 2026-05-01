package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import java.util.List;
import java.util.UUID;

/**
 * UseCase for loading pinned messages in a chat.
 *
 * @author Aleksey Timko
 */
public interface LoadPinnedMessagesUseCase {
    List<Message> loadPinnedMessages(UUID chatId, UUID requesterId);
}

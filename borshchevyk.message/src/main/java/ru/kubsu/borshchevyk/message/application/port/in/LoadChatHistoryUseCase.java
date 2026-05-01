package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;
import java.util.UUID;

/**
 * UseCase for loading the message history of a chat.
 *
 * @author Aleksey Timko
 */
public interface LoadChatHistoryUseCase {
    List<Message> loadChatHistory(UUID chatId, UUID userId, int page, int size);
}

package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;
import java.util.UUID;

/**
 * UseCase for loading comments of a specific message.
 *
 * @author Aleksey Timko
 */
public interface LoadMessageCommentsUseCase {
    List<Message> loadMessageComments(UUID chatId, UUID parentMessageId, UUID requesterId, int page, int size);
}

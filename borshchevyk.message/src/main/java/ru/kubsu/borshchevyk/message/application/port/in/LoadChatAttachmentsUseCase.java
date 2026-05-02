package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;
import java.util.UUID;

/**
 * UseCase for loading chat attachments.
 *
 * @author Aleksey Timko
 */
public interface LoadChatAttachmentsUseCase {
    List<Message> loadChatAttachments(UUID chatId, UUID userId, String type, int page, int size);
}
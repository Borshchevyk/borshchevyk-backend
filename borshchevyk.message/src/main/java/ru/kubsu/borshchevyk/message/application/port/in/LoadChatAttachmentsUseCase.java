package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatAttachmentsQuery;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

public interface LoadChatAttachmentsUseCase {
    List<Message> loadChatAttachments(LoadChatAttachmentsQuery query);
}
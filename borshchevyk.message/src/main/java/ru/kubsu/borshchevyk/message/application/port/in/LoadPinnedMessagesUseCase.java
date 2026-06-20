package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadPinnedMessagesQuery;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

public interface LoadPinnedMessagesUseCase {
    List<Message> loadPinnedMessages(LoadPinnedMessagesQuery query);
}
package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatHistoryQuery;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

public interface LoadChatHistoryUseCase {
    List<Message> loadChatHistory(LoadChatHistoryQuery query);
}
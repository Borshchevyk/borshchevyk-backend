package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageCommentsQuery;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

public interface LoadMessageCommentsUseCase {
    List<Message> loadMessageComments(LoadMessageCommentsQuery query);
}
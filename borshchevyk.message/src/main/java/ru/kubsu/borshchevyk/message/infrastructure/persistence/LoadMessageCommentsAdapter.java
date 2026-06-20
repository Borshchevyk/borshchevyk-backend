package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessageCommentsPort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadMessageCommentsAdapter implements LoadMessageCommentsPort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public List<Message> loadMessageComments(ChatId chatId, MessageId parentMessageId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.loadMessageComments(chatId.value(), parentMessageId.value(), userId.value(), pageable)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }
}

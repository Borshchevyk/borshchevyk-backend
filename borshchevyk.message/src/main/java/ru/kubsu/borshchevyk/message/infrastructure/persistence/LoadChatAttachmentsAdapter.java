package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatAttachmentsPort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadChatAttachmentsAdapter implements LoadChatAttachmentsPort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public List<Message> loadChatAttachments(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, String type, java.time.LocalDateTime historyClearedAt, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.loadChatAttachments(chatId.value(), userId.value(), type, historyClearedAt, pageable)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }
}

package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatAdapter implements ChatPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public Chat save(Chat chat) {
        ChatEntity entity = chatMapper.toEntity(chat);
        ChatEntity saved = chatRepository.save(entity);
        return chatMapper.toDomain(saved);
    }

    @Override
    public Optional<Chat> findById(ChatId chatId) {
        return chatRepository.findById(chatId.value())
                .map(chatMapper::toDomain);
    }

    @Override
    public List<Chat> findByIdIn(List<ChatId> chatIds) {
        List<java.util.UUID> uuids = chatIds.stream()
                .map(ChatId::value)
                .collect(Collectors.toList());
        return chatRepository.findByIdIn(uuids)
                .stream()
                .map(chatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Chat> findPrivateChatBetweenUsers(ru.kubsu.borshchevyk.message.domain.model.value.UserId userId1, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId2) {
        return chatRepository.findPrivateChatBetweenUsers(userId1.value(), userId2.value())
                .map(chatMapper::toDomain);
    }

    @Override
    public Optional<Chat> findByInviteCode(String inviteCode) {
        return chatRepository.findByInviteCode(inviteCode)
                .map(chatMapper::toDomain);
    }
}

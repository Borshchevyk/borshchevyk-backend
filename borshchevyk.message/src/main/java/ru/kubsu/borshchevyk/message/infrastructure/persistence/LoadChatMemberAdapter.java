package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadChatMemberAdapter implements LoadChatMemberPort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public Optional<ChatMember> findByChatIdAndUserId(ChatId chatId, UserId userId) {
        ChatMemberId id = new ChatMemberId(chatId.value(), userId.value());
        return chatMemberRepository.findById(id)
                .map(chatMemberMapper::toDomain);
    }
}

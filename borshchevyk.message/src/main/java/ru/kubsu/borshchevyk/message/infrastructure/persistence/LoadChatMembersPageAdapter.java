package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPagePort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

@Component
@RequiredArgsConstructor
public class LoadChatMembersPageAdapter implements LoadChatMembersPagePort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public Page<ChatMember> findByChatId(ChatId chatId, Pageable pageable) {
        return chatMemberRepository.findByChatId(chatId.value(), pageable)
                .map(chatMemberMapper::toDomain);
    }
}

package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

@Component
@RequiredArgsConstructor
public class SaveChatMemberAdapter implements SaveChatMemberPort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public ChatMember save(ChatMember member) {
        ChatMemberEntity entity = chatMemberMapper.toEntity(member);
        ChatMemberEntity saved = chatMemberRepository.save(entity);
        return chatMemberMapper.toDomain(saved);
    }
}

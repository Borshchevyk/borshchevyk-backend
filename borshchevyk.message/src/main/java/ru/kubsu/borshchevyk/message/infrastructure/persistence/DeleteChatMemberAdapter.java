package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.DeleteChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

@Component
@RequiredArgsConstructor
public class DeleteChatMemberAdapter implements DeleteChatMemberPort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public void delete(ChatMember member) {
        chatMemberRepository.delete(chatMemberMapper.toEntity(member));
    }
}

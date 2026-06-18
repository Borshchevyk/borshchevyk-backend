package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadChatMembersAdapter implements LoadChatMembersPort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public List<ChatMember> findByChatId(ChatId chatId) {
        return chatMemberRepository.findByChatId(chatId.value())
                .stream()
                .map(chatMemberMapper::toDomain)
                .collect(Collectors.toList());
    }
}

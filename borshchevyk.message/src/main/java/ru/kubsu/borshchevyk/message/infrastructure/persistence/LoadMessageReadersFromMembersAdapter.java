package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessageReadersFromMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadMessageReadersFromMembersAdapter implements LoadMessageReadersFromMembersPort {

    private final ChatMemberRepository chatMemberRepository;

    @Override
    public List<UserId> findReadersOfMessage(ChatId chatId, LocalDateTime messageCreatedAt) {
        return chatMemberRepository.findReadersOfMessage(chatId.value(), messageCreatedAt)
                .stream()
                .map(UserId::new)
                .collect(Collectors.toList());
    }
}

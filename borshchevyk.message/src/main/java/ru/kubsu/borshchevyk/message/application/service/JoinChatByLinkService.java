package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.JoinChatByLinkCommand;
import ru.kubsu.borshchevyk.message.application.port.in.JoinChatByLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatByInviteCodePort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinChatByLinkService implements JoinChatByLinkUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatMemberPort saveChatMemberPort;
    private final LoadChatByInviteCodePort loadChatByInviteCodePort;

    @Override
    @Transactional
    public Chat joinChatByLink(JoinChatByLinkCommand command) {
        log.info("User {} joining chat by invite link", command.userId());
        UserId userId = new UserId(command.userId());

        Chat chat = loadChatByInviteCodePort.findByInviteCode(command.inviteCode())
                .orElseThrow(() -> new ChatNotFoundException("Chat not found or invalid invite link"));

        if (chat.isDeleted()) {
             throw new ChatNotFoundException("Chat has been deleted");
        }

        Optional<ChatMember> existingMember = loadChatMemberPort.findByChatIdAndUserId(chat.getId(), userId);
        if (existingMember.isPresent()) {
            return chat;
        }

        ChatMember newMember = ChatMember.builder()
                .chatId(chat.getId())
                .userId(userId)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .lastReadAt(LocalDateTime.now())
                .build();

        saveChatMemberPort.save(newMember);
        return chat;
    }
}
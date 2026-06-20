package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.GenerateInviteLinkQuery;
import ru.kubsu.borshchevyk.message.application.port.in.GenerateInviteLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateInviteLinkService implements GenerateInviteLinkUseCase {

    private final SaveChatPort saveChatPort;
    private final LoadChatPort loadChatPort;

    private final LoadChatMemberPort loadChatMemberPort;

    @Override
    @Transactional
    public String generateInviteLink(GenerateInviteLinkQuery query) {

        log.info(
                "Generating invite link for chat {} by {}",
                query.chatId(),
                query.requesterId()
        );

        ChatId chatId = new ChatId(query.chatId());
        UserId requesterId = new UserId(query.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort
                .findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() ->
                        new UserNotInChatException(
                                "Requester is not in the chat"
                        ));

        chat.validateInviteLinkGeneration();

        requester.validateCanGenerateInviteLink();

        String inviteCode = "https://borshchevik.su/+" + UUID.randomUUID();

        chat.updateInviteCode(inviteCode);

        saveChatPort.save(chat);

        return inviteCode;
    }
}

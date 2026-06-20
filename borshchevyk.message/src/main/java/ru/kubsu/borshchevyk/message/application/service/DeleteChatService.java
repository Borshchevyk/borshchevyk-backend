package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteChatService implements DeleteChatUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatMembersPort loadChatMembersPort;
    private final SaveChatPort saveChatPort;
    private final LoadChatPort loadChatPort;

    private final PublishChatEventPort PublishChatEventPort;

    @Override
    @Transactional
    public void deleteChat(DeleteChatCommand command) {
        log.info("Deleting chat: {} by user: {}", command.chatId(), command.requesterId());

        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.chatId()));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        List<ChatMember> chatMembers = loadChatMembersPort.findByChatId(chatId);

        Consumer<ChatMember> publishMemberDeleted = chatMember ->
                PublishChatEventPort.publishChatEvent(chatMember.getUserId(), chatId, "DELETED");

        requester.validateCanDeleteChat(chat);

        chat.setDeleted(true);
        saveChatPort.save(chat);
        chatMembers.forEach(publishMemberDeleted);
    }
}
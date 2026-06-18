package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatReactionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateChatReactionsService implements UpdateChatReactionsUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatPort saveChatPort;
    private final LoadChatPort loadChatPort;

    @Override
    @Transactional
    public void updateChatReactions(UpdateChatReactionsCommand command) {
        log.info("User {} is updating allowed reactions in chat {}", command.requesterId(), command.chatId());

        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (requester.getRole() != ChatRole.OWNER && requester.getRole() != ChatRole.ADMIN) {
            throw new ForbiddenActionException("Only admins and owners can update allowed reactions");
        }

        chat.setAllowedReactions(command.allowedReactions());
        saveChatPort.save(chat);
    }
}
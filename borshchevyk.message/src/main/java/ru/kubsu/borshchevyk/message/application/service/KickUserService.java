package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;
import ru.kubsu.borshchevyk.message.application.port.in.KickUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.DeleteChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class KickUserService implements KickUserUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final DeleteChatMemberPort deleteChatMemberPort;

    @Override
    @Transactional
    public void kickUser(KickUserCommand command) {
        log.info("Kicking user {} from chat {} by {}", command.targetUserId(), command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        ChatMember target = loadChatMemberPort.findByChatIdAndUserId(chatId, targetUserId)
                .orElseThrow(() -> new UserNotInChatException("Target user is not in the chat"));

        requester.validateCanKickUsers(target);

        if (requesterId.equals(targetUserId)) {
            throw new ForbiddenActionException("Use leaveChat instead of kickUser to kick yourself");
        }

        deleteChatMemberPort.delete(target);
    }
}
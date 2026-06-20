package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatInfoUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions.PermissionType;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateChatInfoService implements UpdateChatInfoUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatPort saveChatPort;
    private final LoadChatPort loadChatPort;

    @Override
    @Transactional
    public void updateChatInfo(UpdateChatInfoCommand command) {
        log.info("Updating chat info for chat {} by user {}", command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot update info of private or saved messages chats");
        }

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!requester.getPermissions().hasPermission(PermissionType.CHANGE_CHAT_INFO) && requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("User does not have permission to change chat info");
        }

        boolean isTitleNotBlank = command.title() != null && !command.title().isBlank();
        chat.updateInfo(
                isTitleNotBlank ? command.title() : null,
                command.description(),
                command.commentsEnabled()
        );

        saveChatPort.save(chat);
    }
}
package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.PinChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.PublishChatEventPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMembersPort;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinChatService implements PinChatUseCase {
    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatMembersPort saveChatMembersPort;

    private final PublishChatEventPort PublishChatEventPort;

    @Override
    @Transactional
    public void pinChat(PinChatCommand command) {
        log.info("Pinning chat {} for user {}", command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        requester.setPinned(true);
        saveChatMembersPort.saveAll(List.of(requester));
        PublishChatEventPort.publishChatEvent(requesterId, chatId, "PINNED");
    }
}
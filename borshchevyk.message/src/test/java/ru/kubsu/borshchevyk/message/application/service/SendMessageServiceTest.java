package ru.kubsu.borshchevyk.message.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMessageServiceTest {

    @Mock private MessagePort messagePort;
    @Mock private ChatPort chatPort;
    @Mock private ChatMemberPort chatMemberPort;
    @Mock private MessageEventPublisherPort messageEventPublisherPort;
    @Mock private MediaPort mediaPort;

    @InjectMocks
    private SendMessageService sendMessageService;

    private UUID chatId;
    private UUID authorId;
    private Chat mockChat;
    private ChatMember mockMember;

    @BeforeEach
    void setUp() {
        chatId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        mockChat = mock(Chat.class);
        mockMember = mock(ChatMember.class);
    }

    @Test
    void sendMessage_shouldReturnSavedMessage_whenSuccess() {
        SendMessageCommand command = SendMessageCommand.builder()
                .chatId(chatId)
                .authorId(authorId)
                .text("Hello")
                .build();

        when(chatPort.findById(any(ChatId.class))).thenReturn(Optional.of(mockChat));
        when(chatMemberPort.findByChatIdAndUserId(any(ChatId.class), any(UserId.class)))
                .thenReturn(Optional.of(mockMember));
        when(mockChat.canMemberSendMessage(any(), anyBoolean())).thenReturn(true);
        when(messagePort.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message result = sendMessageService.sendMessage(command);

        assertNotNull(result);
        assertEquals("Hello", result.getText());
        verify(messageEventPublisherPort).publishMessageCreatedEvent(any(), any());
    }
}

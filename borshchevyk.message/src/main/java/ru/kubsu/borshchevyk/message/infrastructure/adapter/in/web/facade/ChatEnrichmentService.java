package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;
import java.util.List;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class ChatEnrichmentService {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final UserEnrichmentService userEnrichmentService;

    public ShortChatDto enrichChat(UUID chatId, UUID requesterId) {
        if (chatId == null) {
            return null;
        }
        
        Chat chat = chatPort.findById(new ChatId(chatId)).orElse(null);
        if (chat == null) {
            return new ShortChatDto(chatId, "Unknown Chat");
        }

        String title = "Unknown Chat";
        if (chat instanceof GroupChat gc) {
            title = gc.getTitle();
        } else if (chat instanceof Channel c) {
            title = c.getTitle();
        } else if (chat instanceof PrivateChat pc) {
            List<ChatMember> members = chatMemberPort.findByChatId(new ChatId(chatId));
            UUID partnerId = members.stream()
                .map(m -> m.getUserId().value())
                .filter(id -> !id.equals(requesterId))
                .findFirst()
                .orElse(null);

            if (partnerId != null) {
                ShortUserDto partner = userEnrichmentService.enrichUser(partnerId);
                if (partner != null) {
                    title = (partner.firstName() + " " + partner.lastName()).trim();
                    if (title.isEmpty()) {
                        title = partner.tag();
                    }
                } else {
                    title = "Unknown User";
                }
            } else {
                title = "Saved Messages"; // or something, if only 1 user
            }
        }

        return new ShortChatDto(chatId, title);
    }
}

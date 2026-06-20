package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.SavedMessages;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;
import java.util.List;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPort;

@Service
@RequiredArgsConstructor
public class ChatEnrichmentService {
    private final LoadChatPort loadChatPort;
    private final LoadChatMembersPort loadChatMembersPort;

    private final UserEnrichmentService userEnrichmentService;

    public ShortChatDto enrichChat(UUID chatId, UUID requesterId) {
        if (chatId == null) {
            return null;
        }

        Chat chat = loadChatPort.findById(new ChatId(chatId)).orElse(null);
        if (chat == null) {
            return new ShortChatDto(chatId, "Unknown Chat");
        }

        String title = chat.accept(new ru.kubsu.borshchevyk.message.domain.model.chat.visitor.ChatVisitor<String>() {
            @Override
            public String visit(PrivateChat c) {
                List<ChatMember> members = loadChatMembersPort.findByChatId(new ChatId(chatId));
                UUID partnerId = members.stream()
                        .map(m -> m.getUserId().value())
                        .filter(id -> !id.equals(requesterId))
                        .findFirst()
                        .orElse(null);

                if (partnerId != null) {
                    ShortUserDto partner = userEnrichmentService.enrichUser(partnerId);
                    if (partner != null) {
                        String name = (partner.firstName() + " " + partner.lastName()).trim();
                        if (name.isEmpty()) {
                            name = partner.tag();
                        }
                        return name;
                    }
                    return "Unknown User";
                }
                return "Saved Messages";
            }

            @Override
            public String visit(GroupChat c) {
                return c.getTitle();
            }

            @Override
            public String visit(Channel c) {
                return c.getTitle();
            }

            @Override
            public String visit(SavedMessages c) {
                return "Saved Messages";
            }
        });

        return new ShortChatDto(chatId, title);
    }
}

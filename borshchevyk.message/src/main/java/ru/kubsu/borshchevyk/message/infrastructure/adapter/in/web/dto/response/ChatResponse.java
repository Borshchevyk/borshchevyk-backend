package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrivateChatResponse.class, name = "PRIVATE"),
        @JsonSubTypes.Type(value = GroupChatResponse.class, name = "GROUP"),
        @JsonSubTypes.Type(value = ChannelResponse.class, name = "CHANNEL"),
        @JsonSubTypes.Type(value = SavedMessagesResponse.class, name = "SAVED_MESSAGES")
})
@Schema(description = "Chat details response base class")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ChatResponse {
    @Schema(description = "Unique identifier of the chat")
    private UUID id;
    @Schema(description = "Type of the chat")
    @JsonIgnore
    private ChatType type;
    @Schema(description = "Timestamp when the chat was created")
    private LocalDateTime createdAt;
    @Schema(description = "Indicates whether the chat can be deleted")
    private boolean isDeletable;
    @Schema(description = "Set of allowed reactions in the chat")
    private Set<String> allowedReactions;
    @Schema(description = "The last message in the chat")
    private String lastMessage;
    @Schema(description = "Timestamp of the last message")
    private LocalDateTime lastMessageAt;
    @Schema(description = "Number of unread messages for the requester")
    private long unreadCount;
    @Schema(description = "Indicates whether the chat is pinned by the requester")
    private boolean isPinned;
}

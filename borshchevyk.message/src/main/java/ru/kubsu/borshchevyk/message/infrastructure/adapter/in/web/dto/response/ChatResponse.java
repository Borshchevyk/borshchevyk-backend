package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
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
    private ChatType type;
    @Schema(description = "Timestamp when the chat was created")
    private LocalDateTime createdAt;
}

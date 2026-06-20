package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@JsonTypeName("GROUP")
@Schema(description = "Group Chat response")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GroupChatResponse extends ChatResponse {
    @Schema(description = "Title of the group chat")
    private String title;
    @Schema(description = "Description of the group chat")
    private String description;
    @Schema(description = "Whether comments are enabled")
    private boolean commentsEnabled;
}

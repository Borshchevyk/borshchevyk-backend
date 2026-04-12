package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "Channel response")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChannelResponse extends ChatResponse {
    @Schema(description = "Title of the channel")
    private String title;
    @Schema(description = "Description of the channel")
    private String description;
    @Schema(description = "Whether comments are enabled")
    private boolean commentsEnabled;
}

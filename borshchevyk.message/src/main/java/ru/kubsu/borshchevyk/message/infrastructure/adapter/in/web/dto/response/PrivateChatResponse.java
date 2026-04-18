package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
import java.time.LocalDateTime;

@JsonTypeName("PRIVATE")
@Schema(description = "Private Chat response")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PrivateChatResponse extends ChatResponse {
    @Schema(description = "Partner user ID")
    private UUID partnerId;
    @Schema(description = "Partner name")
    private String partnerName;
    @Schema(description = "Partner avatar URL")
    private String partnerAvatarUrl;
    @Schema(description = "Partner last online timestamp")
    private LocalDateTime partnerLastOnline;
}

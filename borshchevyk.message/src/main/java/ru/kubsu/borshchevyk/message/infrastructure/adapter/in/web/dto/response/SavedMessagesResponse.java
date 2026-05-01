package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@JsonTypeName("SAVED_MESSAGES")
@Schema(description = "Saved Messages response")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class SavedMessagesResponse extends ChatResponse {
}

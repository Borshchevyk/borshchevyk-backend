package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReactionEmbeddable {
    private UUID userId;
    private String reaction;
}

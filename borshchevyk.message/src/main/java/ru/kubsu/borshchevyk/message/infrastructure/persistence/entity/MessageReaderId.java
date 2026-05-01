package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReaderId implements Serializable {
    private UUID messageId;
    private UUID userId;
}

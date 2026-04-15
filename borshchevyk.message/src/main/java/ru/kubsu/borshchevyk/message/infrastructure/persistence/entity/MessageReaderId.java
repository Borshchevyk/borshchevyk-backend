package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReaderId implements Serializable {
    private UUID messageId;
    private UUID userId;
}

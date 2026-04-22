package ru.kubsu.borshchevyk.message.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment {
    private UUID id;
    private String type;
}

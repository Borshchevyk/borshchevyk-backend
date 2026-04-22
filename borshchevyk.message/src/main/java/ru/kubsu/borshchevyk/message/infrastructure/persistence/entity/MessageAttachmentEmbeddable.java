package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class MessageAttachmentEmbeddable {

    @Column(name = "attachment_id", nullable = false)
    private UUID id;

    @Column(name = "type")
    private String type;
}

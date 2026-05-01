package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Entity
@DiscriminatorValue("CHANNEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChannelEntity extends ChatEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "comments_enabled")
    @Builder.Default
    private boolean commentsEnabled = true;
}

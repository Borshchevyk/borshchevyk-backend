package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Entity
@DiscriminatorValue("SAVED_MESSAGES")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SavedMessagesEntity extends ChatEntity {
}

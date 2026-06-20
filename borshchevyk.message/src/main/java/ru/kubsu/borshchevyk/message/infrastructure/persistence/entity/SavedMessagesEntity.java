package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SAVED_MESSAGES")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SavedMessagesEntity extends ChatEntity {
}

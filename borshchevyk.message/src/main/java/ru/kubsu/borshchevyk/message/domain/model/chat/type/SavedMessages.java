package ru.kubsu.borshchevyk.message.domain.model.chat.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class SavedMessages extends Chat {

    @Override
    public <T> T accept(ru.kubsu.borshchevyk.message.domain.model.chat.visitor.ChatVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
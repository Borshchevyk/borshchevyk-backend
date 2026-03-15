package ru.kubsu.borshchevyk.auth.domain.model.value;

import lombok.Getter;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

@Getter
public class Tag {
    private String value;

    public Tag(String tag) {
        if (!tag.matches("^[a-zA-Z0-9_-]{3,16}$"))
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.TAG);
        this.value = tag;
    }
}

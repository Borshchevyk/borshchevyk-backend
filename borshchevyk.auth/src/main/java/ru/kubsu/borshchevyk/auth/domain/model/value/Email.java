package ru.kubsu.borshchevyk.auth.domain.model.value;

import lombok.Getter;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

@Getter
public class Email {
    private String value;

    public Email(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.EMAIL);
        this.value = email;
    }
}

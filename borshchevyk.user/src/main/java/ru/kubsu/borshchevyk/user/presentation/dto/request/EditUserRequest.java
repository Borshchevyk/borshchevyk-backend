package ru.kubsu.borshchevyk.user.presentation.dto.request;

import lombok.Data;

@Data
public class EditUserRequest {
    private String email;
    private String tag;
}

package ru.kubsu.borshchevyk.user.presentation.dto.response;

import lombok.Data;

@Data
public class EditUserResponse {
    private String userId;
    private String email;
    private String tag;
}

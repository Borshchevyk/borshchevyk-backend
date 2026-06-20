package ru.kubsu.borshchevyk.message.domain.model.user;

import lombok.EqualsAndHashCode;

import java.security.Principal;

public record UserPrincipal(@EqualsAndHashCode.Include String userId) implements Principal {
    @Override
    public String getName() {
        return userId;
    }
}
package ru.kubsu.borshchevyk.message.infrastructure.websocket;

import java.security.Principal;

/**
 * @author Aleksey Timko
 */
public class UserPrincipal implements Principal {
    private final String userId;

    public UserPrincipal(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId;
    }
}
package ru.kubsu.borshchevyk.user.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing a user in the system.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    /**
     * Unique identifier for the user.
     */
    @EqualsAndHashCode.Include
    private final UserId userId;

    /**
     * User's validated email address.
     */
    private Email email;

    /**
     * User's unique display tag.
     */
    private Tag tag;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's biography.
     */
    private String bio;

    /**
     * URL to the user's avatar image.
     */
    private String avatarUrl;

    /**
     * List of user's past and current avatar images.
     */
    @Builder.Default
    private List<String> avatars = new ArrayList<>();
}

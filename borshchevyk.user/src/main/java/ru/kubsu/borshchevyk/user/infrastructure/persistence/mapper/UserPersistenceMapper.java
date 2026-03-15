package ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.UUID;

/**
 * Mapper for converting between user domain models and persistence entities.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPersistenceMapper {

    /**
     * Maps a user domain model to its corresponding JPA entity.
     *
     * @param user the domain model of the user
     * @return the JPA entity representation of the user
     */
    @Mapping(target = "id", source = "userId.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "tag", source = "tag.value")
    UserJpaEntity toEntity(User user);

    /**
     * Maps a user JPA entity to its corresponding domain model.
     *
     * @param entity the JPA entity of the user
     * @return the domain model representation of the user
     */
    @Mapping(target = "userId", source = "id", qualifiedByName = "mapToUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "mapToEmail")
    @Mapping(target = "tag", source = "tag", qualifiedByName = "mapToTag")
    User toDomain(UserJpaEntity entity);

    /**
     * Maps a UUID to a UserId value object.
     *
     * @param id the UUID to map
     * @return the UserId value object
     */
    @Named("mapToUserId")
    default UserId mapToUserId(UUID id) {
        return id == null ? null : new UserId(id);
    }

    /**
     * Maps a string to an Email value object.
     *
     * @param value the email string to map
     * @return the Email value object
     */
    @Named("mapToEmail")
    default Email mapToEmail(String value) {
        return value == null ? null : new Email(value);
    }

    /**
     * Maps a string to a Tag value object.
     *
     * @param value the tag string to map
     * @return the Tag value object
     */
    @Named("mapToTag")
    default Tag mapToTag(String value) {
        return value == null ? null : new Tag(value);
    }
}

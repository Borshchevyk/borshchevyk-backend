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

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPersistenceMapper {

    @Mapping(target = "id", source = "userId.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "tag", source = "tag.value")
    UserJpaEntity toEntity(User user);

    @Mapping(target = "userId", source = "id", qualifiedByName = "mapToUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "mapToEmail")
    @Mapping(target = "tag", source = "tag", qualifiedByName = "mapToTag")
    User toDomain(UserJpaEntity entity);

    @Named("mapToUserId")
    default UserId mapToUserId(UUID id) {
        return id == null ? null : new UserId(id);
    }

    @Named("mapToEmail")
    default Email mapToEmail(String value) {
        return value == null ? null : new Email(value);
    }

    @Named("mapToTag")
    default Tag mapToTag(String value) {
        return value == null ? null : new Tag(value);
    }
}

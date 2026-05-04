package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.calls.domain.model.User;
import ru.kubsu.borshchevyk.grpc.UserResponse;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", source = "userId", qualifiedByName = "mapId")
    @Mapping(target = "avatarUrl", source = "avatarUrl", qualifiedByName = "mapAvatar")
    User toDomain(UserResponse response);

    @Named("mapId")
    default UUID mapId(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    @Named("mapAvatar")
    default String mapAvatar(String avatarUrl) {
        return (avatarUrl == null || avatarUrl.isEmpty()) ? null : avatarUrl;
    }
}
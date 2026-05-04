package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.entity.CallJpaEntity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper between Domain and JPA Entity for Calls.
 */
@Mapper(componentModel = "spring")
public interface CallJpaMapper {
    @Mapping(target = "id", source = "id", qualifiedByName = "mapCallIdToUuid")
    @Mapping(target = "initiatorId", source = "initiatorId", qualifiedByName = "mapUserIdToUuid")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapUserIdSetToUuidSet")
    CallJpaEntity toEntity(Call domain);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapUuidToCallId")
    @Mapping(target = "initiatorId", source = "initiatorId", qualifiedByName = "mapUuidToUserId")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapUuidSetToUserIdSet")
    Call toDomain(CallJpaEntity entity);

    @Named("mapCallIdToUuid")
    default UUID mapCallIdToUuid(CallId id) {
        return id != null ? id.value() : null;
    }

    @Named("mapUuidToCallId")
    default CallId mapUuidToCallId(UUID id) {
        return id != null ? new CallId(id) : null;
    }

    @Named("mapUserIdToUuid")
    default UUID mapUserIdToUuid(UserId id) {
        return id != null ? id.value() : null;
    }

    @Named("mapUuidToUserId")
    default UserId mapUuidToUserId(UUID id) {
        return id != null ? new UserId(id) : null;
    }

    @Named("mapUserIdSetToUuidSet")
    default Set<UUID> mapUserIdSetToUuidSet(Set<UserId> ids) {
        if (ids == null) return null;
        return ids.stream().map(UserId::value).collect(Collectors.toSet());
    }

    @Named("mapUuidSetToUserIdSet")
    default Set<UserId> mapUuidSetToUserIdSet(Set<UUID> ids) {
        if (ids == null) return null;
        return ids.stream().map(UserId::new).collect(Collectors.toSet());
    }
}

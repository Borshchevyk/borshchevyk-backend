package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.CallResponse;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for Web-layer DTOs.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Mapper(componentModel = "spring")
public interface CallWebMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "mapCallId")
    @Mapping(target = "initiatorId", source = "initiatorId", qualifiedByName = "mapUserId")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapUserIds")
    CallResponse toResponse(Call call);

    @Named("mapCallId")
    default UUID mapCallId(CallId id) {
        return id != null ? id.value() : null;
    }

    @Named("mapUserId")
    default UUID mapUserId(UserId id) {
        return id != null ? id.value() : null;
    }

    @Named("mapUserIds")
    default Set<UUID> mapUserIds(Set<UserId> userIds) {
        if (userIds == null) {
            return null;
        }
        return userIds.stream()
                .map(UserId::value)
                .collect(Collectors.toSet());
    }
}

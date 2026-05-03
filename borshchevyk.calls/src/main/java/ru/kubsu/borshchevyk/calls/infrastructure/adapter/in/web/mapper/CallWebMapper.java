package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUserInfoPort;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUsersBatchPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.User;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.UserDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for Web-layer DTOs.
 */
@RequiredArgsConstructor
@Mapper(componentModel = "spring")
public abstract class CallWebMapper {

    protected final GetUserInfoPort getUserInfoPort;
    protected final GetUsersBatchPort getUsersBatchPort;

    private final UserMapper userMapper;

    @Mapping(target = "id", source = "id", qualifiedByName = "mapCallId")
    @Mapping(target = "initiator", expression = "java(getInitiatorInfo(call))")
    @Mapping(target = "participants", expression = "java(getParticipantsInfo(call))")
    public abstract CallResponse toResponse(Call call);

    @Named("mapCallId")
    protected UUID mapCallId(CallId id) {
        return id != null ? id.value() : null;
    }

    protected UserDto getInitiatorInfo(Call call) {
        if (call.getInitiatorId() == null) {
            return null;
        }
        User user = getUserInfoPort.getUserInfo(call.getInitiatorId().value());
        return userMapper.toDto(user);
    }

    protected Set<UserDto> getParticipantsInfo(Call call) {
        if (call.getParticipants() == null || call.getParticipants().isEmpty()) {
            return Set.of();
        }
        
        List<UUID> userIds = call.getParticipants().stream()
                .map(UserId::value)
                .toList();
                
        List<User> users = getUsersBatchPort.getUsersBatch(userIds);
        
        return users.stream().map(userMapper::toDto).collect(Collectors.toSet());
    }
}

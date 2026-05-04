package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUserInfoPort;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUsersBatchPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.UserResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = WebUserMapper.class
)
public abstract class CallMapper {

    @Autowired
    protected GetUserInfoPort getUserInfoPort;

    @Autowired
    protected GetUsersBatchPort getUsersBatchPort;

    @Autowired
    protected WebUserMapper webUserMapper;

    @Mapping(target = "id", source = "id", qualifiedByName = "mapCallId")
    @Mapping(target = "initiator", expression = "java(getInitiatorInfo(call))")
    @Mapping(target = "participants", expression = "java(getParticipantsInfo(call))")
    public abstract CallResponse toResponse(Call call);

    @Named("mapCallId")
    protected UUID mapCallId(CallId id) {
        return id != null ? id.value() : null;
    }

    protected UserResponse getInitiatorInfo(Call call) {
        var user = getUserInfoPort.getUserInfo(call.getInitiatorId().value());
        return webUserMapper.toDto(user);
    }

    protected Set<UserResponse> getParticipantsInfo(Call call) {
        List<UUID> userIds = call.getParticipantsUUIDs().stream().toList();
        var users = getUsersBatchPort.getUsersBatch(userIds);

        return users.stream()
                .map(webUserMapper::toDto)
                .collect(Collectors.toSet());
    }
}
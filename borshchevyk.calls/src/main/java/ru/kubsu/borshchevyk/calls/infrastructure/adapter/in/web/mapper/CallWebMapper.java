package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.facade.UserEnrichmentService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for Web-layer DTOs.
 */
@Mapper(componentModel = "spring")
public abstract class CallWebMapper {

    @Autowired
    protected UserEnrichmentService userEnrichmentService;

    @Mapping(target = "id", source = "id", qualifiedByName = "mapCallId")
    @Mapping(target = "initiator", expression = "java(getInitiatorInfo(call))")
    @Mapping(target = "participants", expression = "java(getParticipantsInfo(call))")
    public abstract CallResponse toResponse(Call call);

    @Named("mapCallId")
    protected UUID mapCallId(CallId id) {
        return id != null ? id.value() : null;
    }

    protected ShortUserDto getInitiatorInfo(Call call) {
        if (call.getInitiatorId() == null) {
            return null;
        }
        return userEnrichmentService.getUserInfo(call.getInitiatorId().value());
    }

    protected Set<ShortUserDto> getParticipantsInfo(Call call) {
        if (call.getParticipants() == null || call.getParticipants().isEmpty()) {
            return Set.of();
        }
        
        List<UUID> userIds = call.getParticipants().stream()
                .map(UserId::value)
                .toList();
                
        Map<UUID, ShortUserDto> batchInfo = userEnrichmentService.getUsersBatch(userIds);
        
        return userIds.stream()
                .map(batchInfo::get)
                .collect(Collectors.toSet());
    }
}

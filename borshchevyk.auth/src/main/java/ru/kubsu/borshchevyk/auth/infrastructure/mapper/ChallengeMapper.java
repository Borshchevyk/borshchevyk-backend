package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChallengeRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.ChallengeResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeMapper {
    ChallengeCommand toCommand(ChallengeRequest request);
    ChallengeResponse toResponse(ChallengeResult result);
}

package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChallengeRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.ChallengeResponse;

/**
 * MapStruct mapper for challenge-related DTOs and results.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChallengeMapper {
    /**
     * Maps request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    ChallengeCommand toCommand(ChallengeRequest request);

    /**
     * Maps result to response.
     *
     * @param result the domain result
     * @return the response DTO
     */
    ChallengeResponse toResponse(ChallengeResult result);
}

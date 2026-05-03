package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RegisterRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.RegisterResponse;

/**
 * MapStruct mapper for registration-related DTOs and results.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterMapper {
    /**
     * Maps request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    RegisterCommand toCommand(RegisterRequest request);

    /**
     * Maps result to response.
     *
     * @param result the domain result
     * @return the response DTO
     */
    RegisterResponse toResponse(RegisterResult result);
}

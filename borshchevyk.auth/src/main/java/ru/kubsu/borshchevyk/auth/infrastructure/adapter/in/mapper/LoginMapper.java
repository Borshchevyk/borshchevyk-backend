package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.LoginRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.LoginResponse;

/**
 * MapStruct mapper for login-related DTOs and results.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginMapper {
    /**
     * Maps request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    LoginCommand toCommand(LoginRequest request);

    /**
     * Maps result to response.
     *
     * @param result the domain result
     * @return the response DTO
     */
    LoginResponse toResponse(LoginResult result);
}

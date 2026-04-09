package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.VerifyRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.VerifyResponse;

/**
 * MapStruct mapper for verification-related DTOs and results.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VerifyMapper {
    /**
     * Maps request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    VerifyCommand toCommand(VerifyRequest request);

    /**
     * Maps refresh request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand toCommand(ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RefreshRequest request);

    /**
     * Maps result to response.
     *
     * @param result the domain result
     * @return the response DTO
     */
    VerifyResponse toResponse(VerifyResult result);
}

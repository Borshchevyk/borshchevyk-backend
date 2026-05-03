package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RefreshRequest;

/**
 * MapStruct mapper for verification-related DTOs and results.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RefreshMapper {
    /**
     * Maps refresh request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    RefreshCommand toCommand(RefreshRequest request);
}

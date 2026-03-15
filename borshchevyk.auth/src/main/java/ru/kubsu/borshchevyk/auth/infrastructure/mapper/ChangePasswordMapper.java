package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChangePasswordCommand;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChangePasswordRequest;

/**
 * MapStruct mapper for converting password change requests to commands.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChangePasswordMapper {
    /**
     * Maps request to command.
     *
     * @param request the request DTO
     * @return the command
     */
    ChangePasswordCommand toCommand(ChangePasswordRequest request);
}

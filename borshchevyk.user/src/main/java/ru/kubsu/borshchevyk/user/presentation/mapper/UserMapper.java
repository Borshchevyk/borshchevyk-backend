package ru.kubsu.borshchevyk.user.presentation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.presentation.dto.request.EditUserRequest;
import ru.kubsu.borshchevyk.user.presentation.dto.response.EditUserResponse;

/**
 * Mapper for converting between presentation and application/domain layers.
 * Uses MapStruct for automatic implementation generation.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Maps user ID and edit request to an EditUserCommand.
     *
     * @param userId UUID of the user
     * @param request Data transfer object with fields to update
     * @return Command for the application service
     */
    @Mapping(target = "userId", source = "userId")
    EditUserCommand toCommand(String userId, EditUserRequest request);

    /**
     * Maps an EditUserResult to an EditUserResponse.
     *
     * @param result Result from the application service
     * @return Response DTO for the client
     */
    EditUserResponse toResponse(EditUserResult result);
}

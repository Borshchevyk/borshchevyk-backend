package ru.kubsu.borshchevyk.user.presentation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.presentation.dto.request.EditUserRequest;
import ru.kubsu.borshchevyk.user.presentation.dto.response.EditUserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", source = "userId")
    EditUserCommand toCommand(String userId, EditUserRequest request);

    EditUserResponse toResponse(EditUserResult result);
}

package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RegisterRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.RegisterResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterMapper {
    RegisterCommand toCommand(RegisterRequest request);
    RegisterResponse toResponse(RegisterResult result);
}
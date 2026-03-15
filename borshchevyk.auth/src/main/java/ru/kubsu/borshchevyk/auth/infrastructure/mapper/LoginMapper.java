package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.LoginRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.LoginResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginMapper {
    LoginCommand toCommand(LoginRequest request);
    LoginResponse toResponse(LoginResult result);
}